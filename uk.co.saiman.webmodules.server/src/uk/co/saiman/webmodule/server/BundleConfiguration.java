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

import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static org.osgi.namespace.service.ServiceNamespace.CAPABILITY_OBJECTCLASS_ATTRIBUTE;
import static uk.co.saiman.webmodule.WebModuleConstants.AMD_FORMAT;
import static uk.co.saiman.webmodule.WebModuleConstants.ESM_FORMAT;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.json.JSONObject;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

import uk.co.saiman.webmodule.ModuleFormat;
import uk.co.saiman.webmodule.WebModule;

public class BundleConfiguration {
  private static final int STRING_INDENTATION = 4;
  private static final String ESCAPED_SLASH = "%2F";

  private final Bundle bundle;
  private final String pathRoot;

  private final Set<WebModule> visibleModules;
  private final Set<WebModule> visibleModulesClosure;

  private final JSONObject meta;
  private final JSONObject paths;
  private final JSONObject globalMap;
  private final JSONObject contextMap;

  public BundleConfiguration(Bundle bundle, String pathRoot) {
    this.bundle = bundle;
    this.pathRoot = pathRoot;

    this.visibleModules = getVisibleWebModules(bundle);
    this.visibleModulesClosure = getVisibleWebModulesClosure(visibleModules);

    meta = configureMeta();
    paths = configurePaths();
    globalMap = configureGlobalMap();
    contextMap = configureContextMap();
  }

  @Override
  public String toString() {
    return meta.toString(STRING_INDENTATION)
        + paths.toString(STRING_INDENTATION)
        + globalMap.toString(STRING_INDENTATION)
        + contextMap.toString(STRING_INDENTATION);
  }

  public void writeConfig(OutputStream output) throws IOException {
    writeConfig(output, meta);
    writeConfig(output, paths);
    writeConfig(output, globalMap);
    writeConfig(output, contextMap);
  }

  private void writeConfig(OutputStream output, JSONObject configuration) throws IOException {
    Writer out = new OutputStreamWriter(output);

    out.write("System.config(");
    configuration.write(out);
    out.write(");\n");
    out.flush();
  }

  private static boolean isWebModule(ServiceReference<?> reference) {
    Object classes = reference.getProperty(CAPABILITY_OBJECTCLASS_ATTRIBUTE);

    if (classes instanceof String[]) {
      return stream((String[]) classes).anyMatch(WebModule.class.getName()::equals);

    } else if (classes instanceof String) {
      return WebModule.class.getName().equals((String) classes);

    } else {
      return false;
    }
  }

  private static Set<WebModule> getVisibleWebModules(Bundle bundle) {
    return unmodifiableSet(
        concat(stream(bundle.getRegisteredServices()), stream(bundle.getServicesInUse()))
            .filter(BundleConfiguration::isWebModule)
            .map(reference -> bundle.getBundleContext().getService(reference))
            .map(service -> (WebModule) service)
            .collect(toSet()));
  }

  private static Set<WebModule> getVisibleWebModulesClosure(Set<WebModule> visibleModules) {
    Deque<WebModule> queue = new LinkedList<>(visibleModules);
    Set<WebModule> visitied = new HashSet<>();

    while (!queue.isEmpty()) {
      WebModule webModule = queue.poll();

      if (visitied.add(webModule)) {
        webModule.dependencies().forEach(queue::add);
      }
    }

    return unmodifiableSet(visitied);
  }

  private JSONObject configurePaths() {
    JSONObject configuration = new JSONObject();

    configuration.put("baseURL", pathRoot);

    JSONObject paths = new JSONObject();
    configuration.put("paths", paths);

    for (WebModule webModule : visibleModulesClosure) {
      paths
          .put(
              getVersionedModuleName(webModule),
              getModulePath(webModule) + "/" + webModule.entryPoint());
    }

    return configuration;
  }

  private JSONObject configureMeta() {
    JSONObject configuration = new JSONObject();

    JSONObject metas = new JSONObject();
    configuration.put("meta", metas);

    for (WebModule webModule : visibleModulesClosure) {
      JSONObject meta = new JSONObject();
      ModuleFormat format = webModule.format();
      if (format.equals(ESM_FORMAT)) {
        format = AMD_FORMAT;
      }
      meta.put("format", format.toString());
      metas.put(pathRoot + "/" + getModulePath(webModule) + "/*", meta);
    }

    return configuration;
  }

  private JSONObject configureContextMap() {
    JSONObject configuration = new JSONObject();

    JSONObject maps = new JSONObject();
    configuration.put("map", maps);

    for (WebModule webModule : visibleModulesClosure) {
      JSONObject map = new JSONObject();

      webModule.dependencies().forEach(m -> {
        map.put(m.id().toString(), getVersionedModuleName(m));
      });

      maps.put(getVersionedModuleName(webModule), map);
    }

    return configuration;
  }

  private JSONObject configureGlobalMap() {
    JSONObject configuration = new JSONObject();

    JSONObject map = new JSONObject();
    configuration.put("map", map);

    getVisibleWebModules(bundle).forEach(webModule -> {
      map.put(webModule.id().toString(), getVersionedModuleName(webModule));
    });

    return configuration;
  }

  private String getModulePath(WebModule webModule) {
    return getEscapedModuleName(webModule) + "/" + webModule.version();
  }

  private String getEscapedModuleName(WebModule webModule) {
    return webModule.id().toString().replaceAll("/", ESCAPED_SLASH);
  }

  private String getVersionedModuleName(WebModule webModule) {
    return getEscapedModuleName(webModule) + ESCAPED_SLASH + webModule.version();
  }
}
