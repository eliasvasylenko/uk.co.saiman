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

import static java.util.stream.Collectors.toSet;
import static uk.co.saiman.webmodules.commonjs.registry.cache.Cache.getBytes;
import static uk.co.saiman.webmodules.commonjs.registry.cache.Retention.STRONG;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.json.JSONTokener;

import uk.co.saiman.webmodules.commonjs.registry.PackageRoot;
import uk.co.saiman.webmodules.commonjs.registry.PackageVersion;
import uk.co.saiman.webmodules.commonjs.registry.RegistryResolutionException;
import uk.co.saiman.webmodules.commonjs.registry.cache.Cache;
import uk.co.saiman.webmodules.semver.Version;

public class PackageRootImpl implements PackageRoot {
  private static final String LOCAL_FILE = "packageRoot.json";
  private static final String VERSIONS_KEY = "versions";

  private final URL remote;
  private final Path local;
  private final String name;
  private final Set<Version> versions;

  public PackageRootImpl(URL remote, Path local, String name) {
    this.name = name;

    try {
      this.remote = new URL(remote, name);
    } catch (MalformedURLException e) {
      throw new RegistryResolutionException(
          "Failed to resolve package root " + name + " from URL " + remote,
          e);
    }

    Path file;
    try {
      this.local = local.resolve(name);
      Files.createDirectories(local);

      file = new Cache(this.local)
          .fetchResource(
              LOCAL_FILE,
              entry -> entry.writeBytes(getBytes(this.remote.openStream())),
              STRONG);
    } catch (IOException e) {
      throw new RegistryResolutionException(
          "Failed to cache package root " + name + " at " + local,
          e);
    }

    try (InputStream input = Files.newInputStream(file)) {
      JSONObject object = new JSONObject(new JSONTokener(input));

      this.versions = object
          .getJSONObject(VERSIONS_KEY)
          .keySet()
          .stream()
          .map(Version::parse)
          .collect(toSet());
    } catch (IOException e) {
      throw new RegistryResolutionException(
          "Failed to load package root " + name + " from " + file,
          e);
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public URL getUrl() {
    return remote;
  }

  @Override
  public Path getLocal() {
    return local.resolve(LOCAL_FILE);
  }

  @Override
  public synchronized Stream<Version> getPackageVersions() {
    return versions.stream();
  }

  @Override
  public PackageVersion getPackageVersion(Version version) {
    return new PackageVersionImpl(
        remote,
        local,
        name,
        versions
            .stream()
            .filter(v -> v.equals(version))
            .findAny()
            .orElseThrow(
                () -> new RegistryResolutionException("Cannot locate package version " + version)));
  }
}
