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

import static java.util.Arrays.stream;
import static uk.co.saiman.webmodules.commonjs.registry.cache.Cache.getBytes;
import static uk.co.saiman.webmodules.commonjs.registry.cache.Retention.STRONG;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.json.JSONTokener;

import uk.co.saiman.webmodules.commonjs.registry.Archive;
import uk.co.saiman.webmodules.commonjs.registry.ArchiveType;
import uk.co.saiman.webmodules.commonjs.registry.PackageVersion;
import uk.co.saiman.webmodules.commonjs.registry.RegistryResolutionException;
import uk.co.saiman.webmodules.commonjs.registry.cache.Cache;
import uk.co.saiman.webmodules.semver.Range;
import uk.co.saiman.webmodules.semver.Version;

public class PackageVersionImpl implements PackageVersion {
  private static final String LOCAL_FILE = "packageVersion.json";
  private static final String DEPENDENCIES_KEY = "dependencies";
  private static final String DIST_KEY = "dist";
  private static final String SHASUM_KEY = "shasum";

  private final URL remote;
  private final Path local;
  private final String name;
  private final Version version;
  private final String sha1;
  private final Map<ArchiveType, String> archives;
  private final Map<String, Range> dependencies;

  public PackageVersionImpl(URL remote, Path local, String name, Version version) {
    this.name = name;
    this.version = version;

    try {
      this.remote = new URL(remote, name + "/" + version);
    } catch (MalformedURLException e) {
      throw new RegistryResolutionException(
          "Failed to resolve package version " + version + " from URL " + remote,
          e);
    }

    Path file;
    try {
      this.local = local.resolve(version.toString());
      Files.createDirectories(local);

      file = new Cache(this.local)
          .fetchResource(
              LOCAL_FILE,
              entry -> entry.writeBytes(getBytes(this.remote.openStream())),
              STRONG);
    } catch (IOException e) {
      throw new RegistryResolutionException(
          "Failed to cache package version " + version + " at " + local,
          e);
    }

    try (InputStream input = Files.newInputStream(file)) {
      JSONObject object = new JSONObject(new JSONTokener(input));

      JSONObject dependencies = object.optJSONObject(DEPENDENCIES_KEY);
      this.dependencies = new HashMap<>();
      if (dependencies != null) {
        for (String dependency : dependencies.keySet()) {
          this.dependencies.put(dependency, Range.parse(dependencies.getString(dependency)));
        }
      }

      JSONObject dist = object.optJSONObject(DIST_KEY);
      this.archives = new HashMap<>();
      if (dist != null) {
        for (String archiveType : dist.keySet()) {
          stream(ArchiveType.values())
              .filter(a -> a.typeName().equals(archiveType.trim()))
              .findFirst()
              .ifPresent(type -> archives.put(type, dist.getString(archiveType)));
        }
      }

      this.sha1 = dist.optString(SHASUM_KEY, null);
    } catch (IOException e) {
      throw new RegistryResolutionException(
          "Failed to load package version " + version + " from " + file,
          e);
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Version getVersion() {
    return version;
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
  public Optional<String> getSha1() {
    return Optional.ofNullable(sha1);
  }

  @Override
  public Stream<ArchiveType> getArchives() {
    return archives.keySet().stream();
  }

  @Override
  public synchronized Archive getArchive(ArchiveType type) {
    String archiveUrl = archives.get(type);

    if (archiveUrl == null) {
      throw new RegistryResolutionException("Cannot locate archive type " + type);
    }

    return new ArchiveImpl(archiveUrl, type);
  }

  @Override
  public Stream<String> getDependencies() {
    return dependencies.keySet().stream();
  }

  @Override
  public Range getDependencyRange(String module) {
    Range range = dependencies.get(module);

    if (range == null) {
      throw new RegistryResolutionException("Cannot locate dependency " + module);
    }

    return range;
  }
}
