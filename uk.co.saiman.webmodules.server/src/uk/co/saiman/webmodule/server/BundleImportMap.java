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

import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static org.osgi.namespace.service.ServiceNamespace.CAPABILITY_OBJECTCLASS_ATTRIBUTE;

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

import uk.co.saiman.webmodule.WebModule;

public class BundleImportMap {
  private static final int STRING_INDENTATION = 4;
  private static final String ESCAPED_SLASH = "%2F";

  private final JSONObject importMap;

  public BundleImportMap(Bundle bundle, String pathRoot) {
    var visibleModules = getVisibleWebModules(bundle);
    var visibleModulesClosure = getVisibleWebModulesClosure(visibleModules);

    this.importMap = configureImportMap(pathRoot, visibleModules, visibleModulesClosure);

    System.out.println(toString());
  }

  @Override
  public String toString() {
    return importMap.toString(STRING_INDENTATION);
  }

  public void writeImportMap(OutputStream output) throws IOException {
    try (Writer out = new OutputStreamWriter(output)) {
      importMap.write(out);
    }
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
            .filter(BundleImportMap::isWebModule)
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

  private JSONObject configureImportMap(
      String pathRoot,
      Set<WebModule> visibleModules,
      Set<WebModule> visibleModulesClosure) {
    JSONObject importMap = new JSONObject();

    JSONObject imports = new JSONObject();
    importMap.put("imports", imports);

    JSONObject scopes = new JSONObject();
    importMap.put("scopes", scopes);

    /*
     * base imports
     */

    visibleModules.forEach(m -> addModule(imports, pathRoot, m));

    /*
     * scoped imports
     * 
     * TODO try to put these in the base imports map first. If an entry already
     * exists in the imports with a different version, only then put it in the scope
     * map.
     */

    for (WebModule webModule : visibleModulesClosure) {
      JSONObject scope = new JSONObject();

      webModule.dependencies().forEach(m -> addModule(scope, pathRoot, m));

      if (!scope.isEmpty()) {
        scopes.put(getModulePath(pathRoot, webModule), scope);
      }
    }

    return importMap;
  }

  private void addModule(JSONObject map, String pathRoot, WebModule webModule) {
    map
        .put(
            webModule.id().toString(),
            getModulePath(pathRoot, webModule) + "/" + webModule.entryPoint());
    map.put(webModule.id().toString() + "/", getModulePath(pathRoot, webModule) + "/");
  }

  private String getModulePath(String pathRoot, WebModule webModule) {
    return pathRoot + "/" + getEscapedModuleName(webModule) + "/" + webModule.version();
  }

  private String getEscapedModuleName(WebModule webModule) {
    return webModule.id().toString().replaceAll("/", ESCAPED_SLASH);
  }
}
