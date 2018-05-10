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
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.json.JSONTokener;

import uk.co.saiman.webmodules.commonjs.registry.Archive;
import uk.co.saiman.webmodules.commonjs.registry.ArchiveType;
import uk.co.saiman.webmodules.commonjs.registry.PackageVersion;
import uk.co.saiman.webmodules.commonjs.registry.RegistryResolutionException;

public class PackageVersionImpl implements PackageVersion {
  private static final String DIST_KEY = "dist";
  private static final String SHASUM_KEY = "shasum";

  private final URL url;
  private final String name;
  private final String version;
  private final String sha1;
  private final Map<ArchiveType, String> archives;

  public PackageVersionImpl(URL packageRootUrl, String name, String version) {
    try {
      this.url = new URL(packageRootUrl, name + "/" + version);
    } catch (MalformedURLException e) {
      throw new RegistryResolutionException(
          "Failed to resolve package version "
              + version
              + " for root "
              + name
              + " from URL "
              + packageRootUrl,
          e);
    }

    this.name = name;
    this.version = version;

    try (InputStream inputStream = url.openStream()) {
      JSONObject object = new JSONObject(new JSONTokener(inputStream));
      JSONObject dist = object.getJSONObject(DIST_KEY);

      this.archives = dist
          .keySet()
          .stream()
          .flatMap(this::findArchiveType)
          .collect(toMap(identity(), a -> dist.getString(a.typeName())));

      this.sha1 = dist.optString(SHASUM_KEY, null);
    } catch (Exception e) {
      throw new RegistryResolutionException(
          "Failed to load package version from URL " + this.url,
          e);
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getVersion() {
    return version;
  }

  @Override
  public URL getUrl() {
    return url;
  }

  @Override
  public Optional<String> getSha1() {
    return Optional.ofNullable(sha1);
  }

  @Override
  public Stream<ArchiveType> getArchives() {
    return archives.keySet().stream();
  }

  private Stream<ArchiveType> findArchiveType(String name) {
    return stream(ArchiveType.values()).filter(a -> a.typeName().equals(name));
  }

  @Override
  public synchronized Archive getArchive(ArchiveType type) {
    String archiveUrl = archives.get(type);

    if (archiveUrl == null) {
      throw new RegistryResolutionException("Cannot locate archive type " + type);
    }

    return new ArchiveImpl(archiveUrl, type);
  }
}
