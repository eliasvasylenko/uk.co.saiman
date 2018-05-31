/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.webmodules.
 *
 * uk.co.saiman.webmodules is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodule.impl;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static org.osgi.namespace.service.ServiceNamespace.CAPABILITY_OBJECTCLASS_ATTRIBUTE;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;
import static uk.co.saiman.webmodule.WebModuleConstants.AMD_FORMAT;
import static uk.co.saiman.webmodule.WebModuleConstants.CJS_FORMAT;
import static uk.co.saiman.webmodule.WebModuleConstants.ESM_FORMAT;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.http.whiteboard.annotations.RequireHttpWhiteboard;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.log.Log;
import uk.co.saiman.webmodule.ModuleFormat;
import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.WebModule;

@RequireHttpWhiteboard
@Designate(ocd = WebModuleServlet.ModuleServerConfiguration.class, factory = true)
@Component(
    service = Servlet.class,
    immediate = true,
    configurationPid = WebModuleServlet.CONFIGURATION_PID,
    configurationPolicy = ConfigurationPolicy.REQUIRE)
public class WebModuleServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @ObjectClassDefinition(
      name = "Module Server Configuration",
      description = "The module server provides OSGi powered dependency management for javascript modules")
  public @interface ModuleServerConfiguration {
    @AttributeDefinition(name = "Servlet pattern")
    String osgi_http_whiteboard_servlet_pattern() default "/"
        + WebModuleServlet.CONFIGURATION_PID
        + "/*";
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.webmodules.server";

  private ModuleServerConfiguration config;
  private BundleContext context;

  @Reference
  private Log log;

  private Map<PackageId, Map<Version, NavigableSet<ServiceReference<WebModule>>>> modules = new HashMap<>();

  @Activate
  void activate(ModuleServerConfiguration config, BundleContext context) throws Exception {
    this.config = config;
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
    String path = requestPath.substring(servletPath.length(), requestPath.length());

    if (path == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    String[] pathComponents = path.split("/", 4);

    if (pathComponents.length < 3) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    if (pathComponents.length < 4) {
      loadModuleProvisioner(request, response, pathComponents[1], pathComponents[2]);

    } else {
      loadModuleResource(response, pathComponents[1], pathComponents[2], pathComponents[3]);
    }
  }

  protected void loadModuleProvisioner(
      HttpServletRequest request,
      HttpServletResponse response,
      String bundleName,
      String bundleVersion) throws IOException {
    Bundle bundle = getBundle(bundleName, bundleVersion);

    if (bundle == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    response.setContentType("application/javascript");

    String pathRoot = request.getServletPath();
    JSONObject paths = getPaths(pathRoot, bundle);
    writeResource(response, "require.js");
    writeResource(response, "importer.js");
    writeResource(response, "loader.js");
    writeConfig(response, paths);
  }

  private void writeResource(HttpServletResponse response, String resource) throws IOException {
    try (InputStream in = getClass().getResourceAsStream("/static/" + resource)) {
      if (in == null) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
      }

      OutputStream out = response.getOutputStream();

      byte[] buffer = new byte[4096];
      int length;
      while ((length = in.read(buffer)) > 0) {
        out.write(buffer, 0, length);
      }

      out.flush();
    }
  }

  private JSONObject getPaths(String pathRoot, Bundle bundle) throws IOException {
    JSONObject paths = new JSONObject();

    Deque<WebModule> webModules = getVisibleWebModules(bundle)
        .collect(toCollection(LinkedList::new));
    Set<WebModule> processed = new HashSet<>();

    while (!webModules.isEmpty()) {
      WebModule webModule = webModules.poll();

      if (processed.add(webModule)) {
        paths.put(webModule.id().toString(), getPath(pathRoot, webModule));

        webModule.dependencies().forEach(webModules::add);
      }
    }

    System.out.println(bundle.getSymbolicName());
    System.out.println(getVisibleWebModules(bundle).collect(toList()));
    System.out.println(processed);
    System.out.println("        %%%%%%5");
    System.out.println(paths.toString(4));

    return paths;
  }

  private Stream<WebModule> getVisibleWebModules(Bundle bundle) {
    return concat(stream(bundle.getRegisteredServices()), stream(bundle.getServicesInUse()))
        .filter(this::isWebModule)
        .map(reference -> bundle.getBundleContext().getService(reference))
        .map(service -> (WebModule) service);
  }

  private boolean isWebModule(ServiceReference<?> reference) {
    Object classes = reference.getProperty(CAPABILITY_OBJECTCLASS_ATTRIBUTE);

    if (classes instanceof String[]) {
      return stream((String[]) classes).anyMatch(WebModule.class.getName()::equals);

    } else if (classes instanceof String) {
      return WebModule.class.getName().equals((String) classes);

    } else {
      return false;
    }
  }

  private void writeConfig(HttpServletResponse response, JSONObject paths) throws IOException {
    JSONObject map = new JSONObject();
    map.put("*", paths);

    JSONObject config = new JSONObject();
    config.put("map", map);

    Writer out = new OutputStreamWriter(response.getOutputStream());

    out.write("require.config(");
    config.write(out);
    out.write(")");
    out.flush();
  }

  private String getPath(String pathRoot, WebModule webModule) throws IOException {
    String name = webModule.id().toString();
    name = name.replaceAll("/", "%2F");
    String version = webModule.version().toString();

    String location = pathRoot + "/" + name + "/" + version + "/";

    Set<ModuleFormat> formats = webModule.formats().collect(toSet());
    if (formats.contains(AMD_FORMAT)) {
      return location + webModule.entryPoints().getEntryPoint(AMD_FORMAT).path();

    } else if (formats.contains(CJS_FORMAT)) {
      return location + webModule.entryPoints().getEntryPoint(CJS_FORMAT).path();

    } else if (formats.contains(ESM_FORMAT)) {
      /*
       * TODO we need to do some crazy babel transpilation here. We also need to cache
       * the result have the "modules" map contain some internal object not just a
       * service reference to deal with all this.
       */
      return location + webModule.entryPoints().getEntryPoint(ESM_FORMAT).path();

    } else {
      /*
       * This could be handled a little better. We should perhaps link to a generated
       * module which outputs an error to the browser console. But this works and the
       * error is traceable at least.
       */
      return "Can't find supported module type amongst candidates: " + formats;
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
      String resource) throws IOException {
    moduleName = URLDecoder.decode(moduleName, "UTF-8");

    WebModule module;
    synchronized (modules) {
      System.out.println(PackageId.parseId(moduleName));
      System.out.println(Version.parseVersion(moduleVersion));
      System.out.println(modules);
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

    response.setContentType("application/javascript");

    System.out.println("££££££££££");
    System.out.println(module);
    System.out.println(module.id());
    System.out.println(module.dependencies().collect(toList()));
    System.out.println(module.formats().collect(toList()));
    System.out.println(resource);

    try (InputStream in = module.resource(resource).openStream()) {
      if (in == null) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
      }

      OutputStream out = response.getOutputStream();

      byte[] buffer = new byte[4096];
      int length;
      while ((length = in.read(buffer)) > 0) {
        out.write(buffer, 0, length);
      }

      out.flush();
    }
  }
}