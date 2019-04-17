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
 * This file is part of uk.co.saiman.webmodules.extender.
 *
 * uk.co.saiman.webmodules.extender is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules.extender is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodule.extender.impl;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;
import static uk.co.saiman.webmodule.WebModuleConstants.ID_ATTRIBUTE;
import static uk.co.saiman.webmodule.WebModuleConstants.VERSION_ATTRIBUTE;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.ENTRY_POINT_ATTRIBUTE;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.FORMAT_ATTRIBUTE;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.RESOURCE_ROOT_ATTRIBUTE;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;

import uk.co.saiman.webmodule.ModuleFormat;
import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.WebModule;

class WebModuleImpl implements WebModule {
  private final PackageId id;
  private final Version version;
  private final String root;
  private final Bundle bundle;
  private final ModuleFormat format;
  private final String entryPoint;
  private final Set<WebModule> dependencies;

  public WebModuleImpl(BundleCapability capability, Collection<? extends WebModule> dependencies) {
    Map<String, Object> attributes = new HashMap<>(capability.getAttributes());

    this.id = getId(attributes);
    this.version = getVersion(attributes);
    this.root = getRoot(attributes);
    this.format = getFormat(attributes);
    this.entryPoint = getEntryPoint(attributes);
    this.bundle = capability.getResource().getBundle();
    this.dependencies = new HashSet<>(dependencies);
  }

  private PackageId getId(Map<String, Object> attributes) {
    return PackageId.parseId((String) attributes.get(ID_ATTRIBUTE));
  }

  private Version getVersion(Map<String, Object> attributes) {
    return (Version) attributes.get(VERSION_ATTRIBUTE);
  }

  private String getRoot(Map<String, Object> attributes) {
    String root = (String) attributes.get(RESOURCE_ROOT_ATTRIBUTE);
    if (root.endsWith("/"))
      root = root.substring(0, root.length() - 1);
    return root;
  }

  private ModuleFormat getFormat(Map<String, Object> attributes) {
    return new ModuleFormat((String) attributes.get(FORMAT_ATTRIBUTE));
  }

  private String getEntryPoint(Map<String, Object> attributes) {
    String entryPoint = (String) attributes.get(ENTRY_POINT_ATTRIBUTE);
    if (entryPoint.startsWith("./"))
      entryPoint = entryPoint.substring(2);
    return entryPoint;
  }

  @Override
  public PackageId id() {
    return id;
  }

  @Override
  public Version version() {
    return version;
  }

  @Override
  public ModuleFormat format() {
    return format;
  }

  @Override
  public String entryPoint() {
    return entryPoint;
  }

  @Override
  public String openResource(String name) throws IOException {
    URL resource = bundle.getResource(root + "/" + name);
    if (resource == null) {
      throw new FileNotFoundException(name);
    }

    try (InputStream in = resource.openStream()) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      return reader.lines().collect(joining(lineSeparator()));
    }
  }

  @Override
  public Stream<WebModule> dependencies() {
    return dependencies.stream();
  }
}
