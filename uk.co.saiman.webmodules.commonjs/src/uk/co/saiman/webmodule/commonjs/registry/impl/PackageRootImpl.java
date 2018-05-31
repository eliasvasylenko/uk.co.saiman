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
package uk.co.saiman.webmodule.commonjs.registry.impl;

import static uk.co.saiman.webmodule.commonjs.registry.cache.Cache.getBytes;
import static uk.co.saiman.webmodule.commonjs.registry.cache.Retention.STRONG;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.json.JSONTokener;

import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.commonjs.registry.PackageRoot;
import uk.co.saiman.webmodule.commonjs.registry.PackageVersion;
import uk.co.saiman.webmodule.commonjs.registry.RegistryResolutionException;
import uk.co.saiman.webmodule.commonjs.registry.cache.Cache;
import uk.co.saiman.webmodule.semver.Version;

public class PackageRootImpl implements PackageRoot {
  private static final String LOCAL_FILE = "packageRoot.json";
  private static final String VERSIONS_KEY = "versions";

  private final URL remote;
  private final Path local;
  private final PackageId name;
  private final Set<Version> versions;
  private final Map<Version, PackageVersionImpl> inlineVersions;

  public PackageRootImpl(URL remote, Path local, PackageId name) {
    this.name = name;

    try {
      this.remote = new URL(remote, PackageId.urlEncodeName(name));
    } catch (MalformedURLException | UnsupportedEncodingException e) {
      throw new RegistryResolutionException(
          "Failed to resolve package root " + name + " from URL " + remote,
          e);
    }

    Path file;
    try {
      this.local = local.resolve(name.toString());
      Files.createDirectories(local);

      file = new Cache(this.local)
          .fetchResource(
              LOCAL_FILE,
              entry -> entry.writeBytes(getBytes(this.remote::openStream)),
              STRONG);
    } catch (IOException e) {
      throw new RegistryResolutionException(
          "Failed to cache package root " + name + " at " + local,
          e);
    }

    try (InputStream input = Files.newInputStream(file)) {
      JSONObject object = new JSONObject(new JSONTokener(input));

      this.versions = new HashSet<>();
      this.inlineVersions = new HashMap<>();
      loadObject(object);
    } catch (Exception e) {
      throw new RegistryResolutionException(
          "Failed to load package root " + name + " from " + file,
          e);
    }
  }

  private void loadObject(JSONObject object) {
    JSONObject versions = object.getJSONObject(VERSIONS_KEY);

    for (String versionString : versions.keySet()) {
      Version version = Version.parse(versionString);
      this.versions.add(version);

      JSONObject packageVersion = versions.optJSONObject(versionString);
      if (packageVersion != null) {
        this.inlineVersions.put(version, new PackageVersionImpl(packageVersion, name, version));
      }
    }
  }

  @Override
  public PackageId getName() {
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
    PackageVersionImpl inlineVersion = inlineVersions.get(version);
    if (inlineVersion != null) {
      return inlineVersion;
    }

    if (!versions.contains(version)) {
      throw new RegistryResolutionException("Cannot locate package version " + version);
    }

    return new PackageVersionImpl(remote, local, name, version);
  }
}
