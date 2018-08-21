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
import static java.util.Collections.emptySet;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.json.JSONTokener;

import uk.co.saiman.webmodule.ModuleFormat;
import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.WebModuleConstants;
import uk.co.saiman.webmodule.commonjs.RegistryResolutionException;

public class RepositoryConfiguration {
  private final ModuleFormat defaultFormat;
  private final Map<PackageId, BundleConfiguration> bundleConfigurations;

  private RepositoryConfiguration(
      ModuleFormat defaultFormat,
      Set<BundleConfiguration> bundleConfigurations) {
    this.defaultFormat = defaultFormat;
    this.bundleConfigurations = bundleConfigurations
        .stream()
        .collect(toMap(BundleConfiguration::getId, identity()));
  }

  public static RepositoryConfiguration loadPath(Path configurationPath) {
    try (InputStream inputStream = newInputStream(configurationPath)) {
      return loadJson(new JSONObject(new JSONTokener(inputStream)));
    } catch (Exception e) {
      throw new RegistryResolutionException(
          "Failed to load initial dependencies from path " + configurationPath,
          e);
    }
  }

  public static RepositoryConfiguration loadJson(JSONObject root) {
    ModuleFormat defaultFormat = loadDefaultFormat(root);
    Set<BundleConfiguration> bundleConfigurations = loadConfigurations(root, defaultFormat);

    return new RepositoryConfiguration(defaultFormat, bundleConfigurations);
  }

  public Stream<BundleConfiguration> getInitialBundleConfigurations() {
    return bundleConfigurations.values().stream();
  }

  public BundleConfiguration getBundleConfiguration(PackageId id) {
    BundleConfiguration configurations = bundleConfigurations.get(id);
    if (configurations == null) {
      configurations = getDefaultConfiguration(id);
    }
    return configurations;
  }

  private BundleConfiguration getDefaultConfiguration(PackageId id) {
    return BundleConfiguration.getDefault(id, defaultFormat);
  }

  private static ModuleFormat loadDefaultFormat(JSONObject configuration) {
    String defaultFormat = configuration.optString("format", null);
    if (defaultFormat == null) {
      return WebModuleConstants.CJS_FORMAT;
    } else {
      return new ModuleFormat(defaultFormat);
    }
  }

  private static Set<BundleConfiguration> loadConfigurations(
      JSONObject configuration,
      ModuleFormat defaultFormat) {
    configuration = configuration.optJSONObject("dependencies");

    if (configuration != null) {
      Set<BundleConfiguration> configurations = new HashSet<>();

      for (String dependency : configuration.keySet()) {
        BundleConfiguration dependencies = BundleConfiguration
            .loadJson(configuration, dependency, defaultFormat);

        configurations.add(dependencies);
      }

      return configurations;
    } else {
      return emptySet();
    }
  }
}
