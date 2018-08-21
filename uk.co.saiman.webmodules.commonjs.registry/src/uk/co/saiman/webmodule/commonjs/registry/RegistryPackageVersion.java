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
 * This file is part of uk.co.saiman.webmodules.commonjs.registry.
 *
 * uk.co.saiman.webmodules.commonjs.registry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules.commonjs.registry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodule.commonjs.registry;

import static java.util.Arrays.stream;
import static uk.co.saiman.webmodule.commonjs.cache.Cache.getBytes;
import static uk.co.saiman.webmodule.commonjs.cache.Retention.STRONG;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.json.JSONTokener;

import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.commonjs.Dependency;
import uk.co.saiman.webmodule.commonjs.PackageVersion;
import uk.co.saiman.webmodule.commonjs.RegistryResolutionException;
import uk.co.saiman.webmodule.commonjs.Resource;
import uk.co.saiman.webmodule.commonjs.ResourceType;
import uk.co.saiman.webmodule.commonjs.cache.Cache;
import uk.co.saiman.webmodule.semver.Version;

public class RegistryPackageVersion implements PackageVersion {
  private static final String LOCAL_FILE = "packageVersion.json";
  private static final String DEPENDENCIES_KEY = "dependencies";
  private static final String DIST_KEY = "dist";
  private static final String SHASUM_KEY = "shasum";

  private final PackageId name;
  private final Version version;
  private final String sha1;
  private final Map<ResourceType, String> archives;
  private final Map<PackageId, Dependency> dependencies;

  public RegistryPackageVersion(JSONObject object, PackageId name, Version version) {
    this.name = name;
    this.version = version;

    this.dependencies = new HashMap<>();
    this.archives = new HashMap<>();
    this.sha1 = loadObject(object);
  }

  public RegistryPackageVersion(URL remote, Path local, PackageId name, Version version) {
    this.name = name;
    this.version = version;

    URL remoteResolved;
    try {
      remoteResolved = new URL(remote, PackageId.urlEncodeName(name) + "/" + version);
    } catch (MalformedURLException | UnsupportedEncodingException e) {
      throw new RegistryResolutionException(
          "Failed to resolve package version " + version + " from URL " + remote,
          e);
    }

    Path localResolved;
    Path file;
    try {
      localResolved = local.resolve(version.toString());
      Files.createDirectories(local);

      file = new Cache(localResolved)
          .fetchResource(
              LOCAL_FILE,
              entry -> entry.writeBytes(getBytes(remoteResolved.openStream())),
              STRONG);
    } catch (IOException e) {
      throw new RegistryResolutionException(
          "Failed to cache package version " + version + " at " + local,
          e);
    }

    try (InputStream input = Files.newInputStream(file)) {
      JSONObject object = new JSONObject(new JSONTokener(input));

      this.dependencies = new HashMap<>();
      this.archives = new HashMap<>();
      this.sha1 = loadObject(object);
    } catch (Exception e) {
      throw new RegistryResolutionException(
          "Failed to load package version " + version + " from " + file,
          e);
    }
  }

  private String loadObject(JSONObject object) {
    JSONObject dependencies = object.optJSONObject(DEPENDENCIES_KEY);
    if (dependencies != null) {
      for (String dependencyId : dependencies.keySet()) {
        PackageId id = PackageId.parseId(dependencyId);
        Dependency dependency = Dependency.parse(id, dependencies.getString(dependencyId));

        this.dependencies.put(id, dependency);
      }
    }

    JSONObject dist = object.optJSONObject(DIST_KEY);
    if (dist != null) {
      for (String archiveType : dist.keySet()) {
        stream(ResourceType.values())
            .filter(a -> a.typeName().equals(archiveType.trim()))
            .findFirst()
            .ifPresent(type -> archives.put(type, dist.getString(archiveType)));
      }
    }

    return dist.optString(SHASUM_KEY, null);
  }

  @Override
  public PackageId getName() {
    return name;
  }

  @Override
  public Version getVersion() {
    return version;
  }

  @Override
  public Stream<ResourceType> getResources() {
    return archives.keySet().stream();
  }

  @Override
  public synchronized Resource getResource(ResourceType type) {
    String archiveUrl = archives.get(type);

    if (archiveUrl == null) {
      throw new RegistryResolutionException("Cannot locate archive type " + type);
    }

    return sha1 == null
        ? new RegistryResource(archiveUrl, type)
        : new RegistryResource(archiveUrl, type, sha1);
  }

  @Override
  public Stream<PackageId> getDependencies() {
    return dependencies.keySet().stream();
  }

  @Override
  public Dependency getDependency(PackageId module) {
    Dependency range = dependencies.get(module);

    if (range == null) {
      throw new RegistryResolutionException("Cannot locate dependency " + module);
    }

    return range;
  }
}
