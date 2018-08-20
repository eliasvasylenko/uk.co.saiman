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

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import uk.co.saiman.webmodule.ModuleFormat;
import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.semver.Range;
import uk.co.saiman.webmodule.semver.Version;

public class BundleConfiguration {
  private final PackageId id;
  private final Range version;
  private final Set<BundleVersionConfiguration> configurations;

  private final ModuleFormat defaultFormat;

  private BundleConfiguration(
      PackageId id,
      BundleVersionConfiguration configuration,
      ModuleFormat defaultFormat) {
    this.id = id;
    this.version = configuration.version();
    this.configurations = singleton(configuration);
    this.defaultFormat = defaultFormat;
  }

  private BundleConfiguration(
      PackageId id,
      Set<BundleVersionConfiguration> configurations,
      ModuleFormat defaultFormat) {
    this.id = id;
    this.version = new Range(
        configurations
            .stream()
            .map(BundleVersionConfiguration::version)
            .flatMap(Range::getComparatorSets)
            .collect(toList()));
    this.configurations = configurations;
    this.defaultFormat = defaultFormat;
  }

  public static BundleConfiguration loadJson(
      JSONObject root,
      String dependency,
      ModuleFormat defaultFormat) {
    PackageId id = PackageId.parseId(dependency);
    Object configuration = root.get(dependency);

    if (configuration instanceof String) {
      Range version = Range.parse((String) configuration);
      return new BundleConfiguration(
          id,
          new BundleVersionConfiguration(version, defaultFormat),
          defaultFormat);

    } else if (configuration instanceof JSONObject) {
      return new BundleConfiguration(
          id,
          BundleVersionConfiguration.loadJson((JSONObject) configuration, defaultFormat),
          defaultFormat);

    } else if (configuration instanceof JSONArray) {
      Set<BundleVersionConfiguration> configurations = new HashSet<>();
      for (Object element : (JSONArray) configuration) {
        configurations
            .add(BundleVersionConfiguration.loadJson((JSONObject) element, defaultFormat));
      }
      return new BundleConfiguration(id, configurations, defaultFormat);

    } else {
      throw new IllegalArgumentException("Unrecognised dependency configuration " + configuration);
    }
  }

  public static BundleConfiguration getDefault(PackageId id, ModuleFormat defaultFormat) {
    return new BundleConfiguration(
        id,
        BundleVersionConfiguration.getDefault(defaultFormat),
        defaultFormat);
  }

  PackageId getId() {
    return id;
  }

  public Stream<BundleVersionConfiguration> getInitialVersionConfigurations() {
    return configurations.stream();
  }

  public Range getInitialVersionConfigurationRange() {
    return version;
  }

  public Stream<BundleVersionConfiguration> getVersionConfigurations(Version version) {
    if (this.version.matches(version)) {
      return configurations.stream().filter(c -> c.version().matches(version));
    } else {
      return Stream.of(BundleVersionConfiguration.getDefault(defaultFormat));
    }
  }
}
