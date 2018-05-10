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
package uk.co.saiman.webmodules.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
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
import uk.co.saiman.webmodules.WebModule;
import uk.co.saiman.webmodules.WebModules;

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

  @Reference
  private WebModules resources;

  @Activate
  void activate(ModuleServerConfiguration config, BundleContext context) throws Exception {
    this.config = config;
    this.context = context;
  }

  @Deactivate
  void deactivate() {}

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String path = request.getPathInfo();

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
      loadModuleProvisioner(response, pathComponents[1], pathComponents[2]);

    } else {
      loadModuleResource(response, pathComponents[1], pathComponents[2], pathComponents[3]);
    }
  }

  protected void loadModuleProvisioner(
      HttpServletResponse response,
      String bundleName,
      String bundleVersion) {
    Bundle bundle = getBundle(bundleName, bundleVersion);
    System.out.println(bundle);
  }

  /*
   * Helper to find a bundle
   */
  private Bundle getBundle(String bsn, String version) {
    Version v = new Version(version);
    for (Bundle bundle : context.getBundles()) {
      if (bsn.equals(bundle.getSymbolicName()) && v.equals(bundle.getVersion()))
        return bundle;
    }
    return null;
  }

  protected void loadModuleResource(
      HttpServletResponse response,
      String moduleName,
      String moduleVersion,
      String resource) throws IOException {
    WebModule module = resources
        .getResource(moduleName, Version.parseVersion(moduleVersion))
        .orElse(null);

    if (module == null) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }

    response.setContentType("application/javascript");

    try (InputStream in = module.resource(resource).openStream()) {
      OutputStream out = response.getOutputStream();

      byte[] buffer = new byte[4096];
      int length;
      while ((length = in.read(buffer)) > 0) {
        out.write(buffer, 0, length);
      }

      out.flush();
    } catch (NullPointerException e) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
  }
}