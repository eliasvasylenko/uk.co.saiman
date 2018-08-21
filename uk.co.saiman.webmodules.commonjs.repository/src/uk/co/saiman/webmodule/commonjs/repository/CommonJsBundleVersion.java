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
 * This file is part of uk.co.saiman.webmodules.commonjs.repository.
 *
 * uk.co.saiman.webmodules.commonjs.repository is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules.commonjs.repository is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodule.commonjs.repository;

import static java.nio.file.Files.newInputStream;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.webmodule.commonjs.cache.Cache.getBytes;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.osgi.framework.Version;

import uk.co.saiman.webmodule.ModuleFormat;
import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.commonjs.Dependency;
import uk.co.saiman.webmodule.commonjs.PackageVersion;
import uk.co.saiman.webmodule.commonjs.RegistryResolutionException;
import uk.co.saiman.webmodule.commonjs.Resource;
import uk.co.saiman.webmodule.commonjs.ResourceType;
import uk.co.saiman.webmodule.commonjs.cache.Cache;
import uk.co.saiman.webmodule.commonjs.cache.CacheEntry;

public class CommonJsBundleVersion {
  private static final String SHASUM_CACHE = ".shasum";

  static final String PACKAGE_ROOT = "package/";
  private static final String PACKAGE_JSON = "package.json";
  private static final String DIST = "dist";

  static final String RESOURCE_ROOT = "static/";

  private final CommonJsBundle bundle;
  private final PackageVersion packageVersion;

  private final Version version;

  private final ResourceType resourceType;
  private final Cache cache;
  private JSONObject packageJson;
  private Path packageDist;

  private final Map<ModuleFormat, BundleVersionConfiguration> formatConfigurations;
  private final Map<ModuleFormat, CommonJsResource> resources;
  private final Map<ModuleFormat, CommonJsJar> jars;

  public CommonJsBundleVersion(
      CommonJsBundle bundle,
      PackageVersion version,
      List<BundleVersionConfiguration> configurations) {
    this.bundle = bundle;
    this.packageVersion = version;

    this.version = version.getVersion().toOsgiVersion();
    this.resourceType = ResourceType.TARBALL;
    Path cacheRoot = bundle.getRepository().getCache();
    Path cachePath = getSha1()
        .map(sha1 -> cacheRoot.resolve(SHASUM_CACHE).resolve(sha1))
        .orElse(
            cacheRoot
                .resolve(bundle.getModuleName().toString())
                .resolve(version.getVersion().toString()));
    this.cache = new Cache(cachePath);

    this.formatConfigurations = new HashMap<>();
    this.resources = new HashMap<>();
    this.jars = new HashMap<>();

    for (BundleVersionConfiguration configuration : configurations) {
      ModuleFormat format = configuration.format();
      if (!formatConfigurations.containsKey(format))
        formatConfigurations.put(format, configuration);
    }
  }

  public CommonJsBundle getBundle() {
    return bundle;
  }

  public Stream<ModuleFormat> getFormats() {
    return formatConfigurations.keySet().stream();
  }

  public Optional<CommonJsResource> getResource(ModuleFormat format) {
    synchronized (resources) {
      BundleVersionConfiguration configuration = formatConfigurations.get(format);
      if (configuration == null) {
        return Optional.empty();
      } else {
        CommonJsResource resource = resources
            .computeIfAbsent(
                format,
                r -> new CommonJsResource(
                    getBundle().getModuleName(),
                    getVersion(),
                    configuration,
                    getPackageJson()));
        return Optional.ofNullable(resource);
      }
    }
  }

  public Stream<CommonJsResource> getResources() {
    return formatConfigurations.keySet().stream().map(this::getResource).map(Optional::get);
  }

  public Optional<CommonJsJar> getJar(ModuleFormat format) {
    synchronized (jars) {
      BundleVersionConfiguration configuration = formatConfigurations.get(format);
      if (configuration == null) {
        return Optional.empty();
      } else {
        CommonJsJar jar = jars
            .computeIfAbsent(
                format,
                r -> new CommonJsJar(
                    cache,
                    getResource(format).get(),
                    getPackageDist(),
                    getBundle().getModuleName(),
                    getBundle().getBundleSymbolicName(format),
                    getVersion(),
                    getBundle().getRepository().getBundleSymbolicNamePrefix()));
        return Optional.ofNullable(jar);
      }
    }
  }

  public Stream<CommonJsJar> getJars() {
    return formatConfigurations.keySet().stream().map(this::getJar).map(Optional::get);
  }

  public uk.co.saiman.webmodule.semver.Version getSemver() {
    return packageVersion.getVersion();
  }

  public Version getVersion() {
    return version;
  }

  public Stream<PackageId> getDependencies() {
    return packageVersion.getDependencies();
  }

  public Dependency getDependencyVersion(PackageId module) {
    return packageVersion.getDependency(module);
  }

  private Optional<String> getSha1() {
    return packageVersion.getResource(resourceType).getSha1();
  }

  synchronized JSONObject getPackageJson() {
    if (packageJson == null) {
      try {
        packageJson = new JSONObject(new JSONTokener(newInputStream(fetchPackageJson())));
      } catch (JSONException | IOException e) {
        throw new RegistryResolutionException("Failed to open " + PACKAGE_JSON, e);
      }
    }
    return packageJson;
  }

  private Path fetchPackageJson() throws IOException {
    return cache.fetchResource(PACKAGE_JSON, entry -> {
      switch (resourceType) {
      case TARBALL:
        extractTarballPackageJson(packageVersion.getResource(ResourceType.TARBALL), entry);

      default:
        throw new RegistryResolutionException(
            "No supported archive types amongst candidates "
                + packageVersion.getResources().collect(toList()));
      }
    });
  }

  private void extractTarballPackageJson(Resource archive, CacheEntry entry) {
    try (TarGzInputStream input = new TarGzInputStream(
        archive.getUrl().openStream(),
        getSha1().orElse(null))) {

      input.findEntry(PACKAGE_ROOT + PACKAGE_JSON);

      entry.writeBytes(getBytes(input));
    } catch (IOException e) {
      throw new RegistryResolutionException(
          "Failed to extract archive from URL " + archive.getUrl(),
          e);
    }
  }

  public synchronized Path getPackageDist() {
    if (packageDist == null) {
      try {
        packageDist = fetchPackageDist();
      } catch (JSONException | IOException e) {
        throw new RegistryResolutionException("Failed to open " + DIST, e);
      }
    }
    return packageDist;
  }

  private Path fetchPackageDist() throws IOException {
    return cache.fetchResource(DIST, entry -> {
      if (packageVersion.getResources().anyMatch(ResourceType.TARBALL::equals)) {
        extractTarballPackageDist(packageVersion.getResource(ResourceType.TARBALL), entry);

      } else {
        throw new RegistryResolutionException(
            "No supported archive types amongst candidates "
                + packageVersion.getResources().collect(toList()));
      }
    });
  }

  private void extractTarballPackageDist(Resource archive, CacheEntry entry) {
    try (TarGzInputStream input = new TarGzInputStream(
        archive.getUrl().openStream(),
        getSha1().orElse(null))) {

      TarArchiveEntry tarEntry = input.getNextTarEntry();
      while (tarEntry != null) {
        if (tarEntry.isFile()) {
          entry.writeBytes(tarEntry.getName(), getBytes(input));
        }
        tarEntry = input.getNextTarEntry();
      }
    } catch (IOException e) {
      throw new RegistryResolutionException(
          "Failed to extract archive from URL " + archive.getUrl(),
          e);
    }
  }
}
