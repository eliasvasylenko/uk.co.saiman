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
 * This file is part of uk.co.saiman.webmodules.commonjs.
 *
 * uk.co.saiman.webmodules.commonjs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules.commonjs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodules.commonjs.registry.impl;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.json.JSONTokener;

import uk.co.saiman.webmodules.commonjs.registry.PackageRoot;
import uk.co.saiman.webmodules.commonjs.registry.PackageVersion;
import uk.co.saiman.webmodules.commonjs.registry.RegistryResolutionException;

public class PackageRootImpl implements PackageRoot {
  private static final String VERSIONS_KEY = "versions";

  private final URL url;
  private final String name;
  private final Set<String> versions;

  public PackageRootImpl(URL registryRootUrl, String name) {
    try {
      this.url = new URL(registryRootUrl, name);
    } catch (MalformedURLException e) {
      throw new RegistryResolutionException(
          "Failed to resolve package root " + name + " from URL " + registryRootUrl,
          e);
    }

    this.name = name;

    try (InputStream inputStream = url.openStream()) {
      JSONObject object = new JSONObject(new JSONTokener(inputStream));

      this.versions = new HashSet<>(object.getJSONObject(VERSIONS_KEY).keySet());
    } catch (Exception e) {
      throw new RegistryResolutionException("Failed to load package root from URL " + url, e);
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public URL getUrl() {
    return url;
  }

  @Override
  public synchronized Stream<String> getPackageVersions() {
    return versions.stream();
  }

  @Override
  public PackageVersion getPackageVersion(String version) {
    return new PackageVersionImpl(
        url,
        name,
        versions
            .stream()
            .filter(v -> v.equals(version))
            .findAny()
            .orElseThrow(
                () -> new RegistryResolutionException("Cannot locate package version " + version)));
  }
}
