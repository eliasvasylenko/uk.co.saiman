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
package uk.co.saiman.webmodule.commonjs.repository;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.webmodule.ModuleFormat;
import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.commonjs.registry.PackageRoot;
import uk.co.saiman.webmodule.commonjs.registry.PackageVersion;
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

  void configureVersions(Range version) {
    packageRoot
        .getPackageVersions()
        .parallel()
        .filter(version::matches)
        .forEach(this::configureVersion);
  }

  private void configureVersion(Version version) {
    CommonJsBundleVersion bundleVersion = resources.get(version);

    if (bundleVersion == null) {
      try {
        bundleVersion = fetchVersion(version);
      } catch (Exception e) {
        getLog().log(Level.WARN, "Cannot initialize version " + version, e);
        return;
      }

      CommonJsBundleVersion bundleVersionFinal = bundleVersion;
      bundleVersionFinal.getDependencies().forEach(dependency -> {
        repository.configureBundle(dependency, bundleVersionFinal.getDependencyRange(dependency));
      });
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
