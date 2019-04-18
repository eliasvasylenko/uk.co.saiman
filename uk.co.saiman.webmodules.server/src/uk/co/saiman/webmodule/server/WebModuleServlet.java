/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.webmodules.server.
 *
 * uk.co.saiman.webmodules.server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules.server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodule.server;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.annotations.RequireHttpWhiteboard;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.log.Log;
import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.WebModule;

@RequireHttpWhiteboard
@Designate(ocd = WebModuleServlet.ModuleServerConfiguration.class, factory = true)
@Component(service = Servlet.class, immediate = true, configurationPid = WebModuleServlet.CONFIGURATION_PID, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class WebModuleServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @ObjectClassDefinition(name = "Module Server Configuration", description = "The module server provides OSGi powered dependency management for javascript modules")
  public @interface ModuleServerConfiguration {
    @AttributeDefinition(name = "Servlet pattern")
    String osgi_http_whiteboard_servlet_pattern() default "/"
        + WebModuleServlet.CONFIGURATION_PID
        + "/*";
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.webmodules.server";

  private BundleContext context;

  @Reference
  private Log log;

  private Map<PackageId, Map<Version, NavigableSet<ServiceReference<WebModule>>>> modules = new HashMap<>();

  @Activate
  void activate(ModuleServerConfiguration config, BundleContext context) throws Exception {
    // this.config = config;
    this.context = context;
  }

  @Deactivate
  void deactivate() {}

  @Reference(cardinality = MULTIPLE, policy = DYNAMIC)
  synchronized void register(ServiceReference<WebModule> registration) {
    WebModule module = registration.getBundle().getBundleContext().getService(registration);
    synchronized (modules) {
      modules
          .computeIfAbsent(module.id(), id -> new HashMap<>())
          .computeIfAbsent(module.version(), version -> new TreeSet<>())
          .add(registration);
    }
  }

  synchronized void unregister(ServiceReference<WebModule> registration) {
    WebModule module = registration.getBundle().getBundleContext().getService(registration);
    synchronized (modules) {
      Map<Version, NavigableSet<ServiceReference<WebModule>>> versions = modules.get(module.id());
      Set<ServiceReference<WebModule>> references = versions.get(module.version());
      if (references.remove(registration)) {
        versions.remove(module.version());
        if (versions.isEmpty()) {
          modules.remove(module.id());
        }
      }
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String servletPath = request.getServletPath();
    String requestPath = request.getRequestURI();
    String path = requestPath.substring(servletPath.length() + 1, requestPath.length());

    if (path == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    String[] pathComponents = path.split("/", 3);

    if (pathComponents.length == 1) {
      switch (path) {
      case "importmap-shim.js":
        response.setContentType("application/javascript");
        writeResource(response, "dist/es-module-shims.js");
        break;

      case "importmap.json":
        String bundleId = request.getParameterMap().get("bundle")[0];
        String bundleVersion = request.getParameterMap().get("version")[0];
        loadImportMap(request, response, bundleId, bundleVersion);
        break;

      default:
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
      }

    } else if (pathComponents.length < 3) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);

    } else {
      loadModuleResource(response, pathComponents[0], pathComponents[1], pathComponents[2]);
    }
  }

  protected void loadImportMap(
      HttpServletRequest request,
      HttpServletResponse response,
      String bundleName,
      String bundleVersion)
      throws IOException {
    Bundle bundle = getBundle(bundleName, bundleVersion);

    if (bundle == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    BundleImportMap configuration = new BundleImportMap(bundle, request.getServletPath());

    configuration.writeImportMap(response.getOutputStream());
  }

  private void writeResource(HttpServletResponse response, String resource) throws IOException {
    try (var in = getClass().getResourceAsStream("/static/" + resource);
        var out = response.getOutputStream()) {
      if (in == null) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
      }

      byte[] buffer = new byte[4096];
      int length;
      while ((length = in.read(buffer)) > 0) {
        out.write(buffer, 0, length);
      }
    }
  }

  private Bundle getBundle(String bsn, String version) {
    Version v = new Version(version);
    for (Bundle bundle : context.getBundles()) {
      if (bsn.equals(bundle.getSymbolicName()) && v.equals(bundle.getVersion())) {
        return bundle;
      }
    }
    return null;
  }

  protected void loadModuleResource(
      HttpServletResponse response,
      String moduleName,
      String moduleVersion,
      String resource)
      throws IOException {
    moduleName = URLDecoder.decode(moduleName, "UTF-8");

    WebModule module;
    synchronized (modules) {
      try {
        ServiceReference<WebModule> registration = modules
            .get(PackageId.parseId(moduleName))
            .get(Version.parseVersion(moduleVersion))
            .descendingIterator()
            .next();
        module = registration.getBundle().getBundleContext().getService(registration);
      } catch (NullPointerException e) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
      }
    }

    String source = module.openResource(resource);
    response.setContentType("application/javascript");

    try (var writer = new OutputStreamWriter(response.getOutputStream())) {
      writer.write(source);
    }
  }
}
