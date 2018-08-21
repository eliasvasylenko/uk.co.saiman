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

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.log.Log.Level.WARN;
import static uk.co.saiman.webmodule.commonjs.DependencyKind.GIT;
import static uk.co.saiman.webmodule.commonjs.DependencyKind.URI;
import static uk.co.saiman.webmodule.commonjs.DependencyKind.VERSION_RANGE;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.log.Log;
import uk.co.saiman.webmodule.ModuleFormat;
import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.commonjs.Dependency;
import uk.co.saiman.webmodule.commonjs.PackageVersion;
import uk.co.saiman.webmodule.commonjs.registry.PackageRoot;
import uk.co.saiman.webmodule.semver.Range;
import uk.co.saiman.webmodule.semver.Version;

public class CommonJsBundle {
  private final CommonJsRepository repository;
  private final PackageRoot packageRoot;
  private final String bundleName;
  private final BundleConfiguration configuration;
  private final Map<uk.co.saiman.webmodule.semver.Version, CommonJsBundleVersion> resources;

  public CommonJsBundle(
      CommonJsRepository repository,
      PackageRoot packageRoot,
      BundleConfiguration configuration) {
    this.repository = repository;
    this.packageRoot = packageRoot;
    this.bundleName = createBundleSymbolicName();
    this.configuration = configuration;
    this.resources = new HashMap<>();
  }

  void configureDependency(Dependency dependency) {
    if (dependency.getKind() == VERSION_RANGE) {
      configureVersions(dependency.getVersion(VERSION_RANGE).get());

    } else if (dependency.getKind() == GIT) {
      configureGit(dependency.getVersion(GIT).get());

    } else if (dependency.getKind() == URI) {
      configureGit(dependency.getVersion(URI).get());
    }
  }

  void configureVersions(Range version) {
    packageRoot
        .getPackageVersions()
        .parallel()
        .filter(version::matches)
        .forEach(this::configureVersion);
  }

  void configureGit(URI gitUri) {
    getLog().log(WARN, format("Unsupported git dependency %s in repo %s", gitUri, getRepository()));
  }

  void configureUri(URI uri) {
    getLog().log(WARN, format("Unsupported URI dependency %s in repo %s", uri, getRepository()));
  }

  private void configureVersion(Version version) {
    CommonJsBundleVersion bundleVersion = resources.get(version);

    if (bundleVersion == null) {
      try {
        bundleVersion = fetchVersion(version);
      } catch (Exception e) {
        getLog().log(WARN, "Cannot initialize version " + version, e);
        return;
      }

      CommonJsBundleVersion bundleVersionFinal = bundleVersion;
      bundleVersionFinal
          .getDependencies()
          .map(bundleVersionFinal::getDependencyVersion)
          .forEach(repository::configureBundle);
    }
  }

  private CommonJsBundleVersion fetchVersion(Version version) {
    List<BundleVersionConfiguration> configuration = this.configuration
        .getVersionConfigurations(version)
        .collect(toList());
    PackageVersion packageVersion = packageRoot.getPackageVersion(version);
    CommonJsBundleVersion bundleVersion = new CommonJsBundleVersion(
        this,
        packageVersion,
        configuration);

    synchronized (resources) {
      if (resources.containsKey(version)) {
        bundleVersion = resources.get(version);
      } else {
        resources.put(version, bundleVersion);
      }
    }

    return bundleVersion;
  }

  Log getLog() {
    return repository.getLog();
  }

  public CommonJsRepository getRepository() {
    return repository;
  }

  public PackageId getModuleName() {
    return packageRoot.getName();
  }

  public String getBundleSymbolicName(ModuleFormat format) {
    return bundleName + "." + format;
  }

  private String createBundleSymbolicName() {
    PackageId name = packageRoot.getName();
    String nameString = name.getScope().map(s -> s + "." + name.getName()).orElse(name.getName());
    return repository.getBundleSymbolicNamePrefix()
        + "."
        + stream(nameString.split("[^a-zA-Z0-9._-]")).collect(joining("_"));
  }

  public Stream<uk.co.saiman.webmodule.semver.Version> getSemvers() {
    return resources.keySet().stream();
  }

  public Stream<CommonJsBundleVersion> getBundleVersions() {
    return resources.values().stream();
  }

  public Stream<ModuleFormat> getFormats() {
    return getBundleVersions().flatMap(CommonJsBundleVersion::getFormats).distinct();
  }

  public Stream<CommonJsBundleVersion> getBundleVersions(ModuleFormat format) {
    return getBundleVersions().filter(v -> v.getFormats().anyMatch(format::equals));
  }

  public Optional<CommonJsBundleVersion> getBundleVersion(
      uk.co.saiman.webmodule.semver.Version semver) {
    return Optional.ofNullable(resources.get(semver));
  }
}
