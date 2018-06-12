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

import static java.util.Objects.requireNonNull;
import static uk.co.saiman.webmodule.WebModuleConstants.VERSION_ATTRIBUTE;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.DEPENDENCIES_ATTRIBUTE;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.ENTRY_POINT_ATTRIBUTE;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.FORMAT_ATTRIBUTE;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;

import uk.co.saiman.webmodule.ModuleFormat;
import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.semver.Range;

public class BundleVersionConfiguration {
  private final Range version;
  private final String entryPoint;
  private final ModuleFormat format;
  private final Map<PackageId, Range> dependencies;

  public BundleVersionConfiguration(Range version, ModuleFormat format) {
    this(version, null, format, null);
  }

  private BundleVersionConfiguration(
      Range version,
      String entryPoint,
      ModuleFormat format,
      Map<PackageId, Range> dependencies) {
    this.version = requireNonNull(version);
    this.entryPoint = entryPoint;
    this.format = requireNonNull(format);
    this.dependencies = dependencies;
  }

  public static BundleVersionConfiguration loadJson(
      JSONObject configuration,
      ModuleFormat defaultFormat) {
    return new BundleVersionConfiguration(
        loadVersion(configuration),
        loadEntryPoint(configuration),
        loadFormat(configuration, defaultFormat),
        loadDependencies(configuration));
  }

  public static BundleVersionConfiguration getDefault(ModuleFormat defaultFormat) {
    return new BundleVersionConfiguration(Range.EMPTY, defaultFormat);
  }

  private static Range loadVersion(JSONObject configuration) {
    String range = configuration.optString(VERSION_ATTRIBUTE, null);
    if (range == null) {
      return null;
    } else {
      return Range.parse(range);
    }
  }

  private static String loadEntryPoint(JSONObject configuration) {
    return configuration.optString(ENTRY_POINT_ATTRIBUTE, null);
  }

  private static ModuleFormat loadFormat(JSONObject configuration, ModuleFormat defaultFormat) {
    String format = configuration.optString(FORMAT_ATTRIBUTE, null);
    if (format == null) {
      return defaultFormat;
    } else {
      return new ModuleFormat(format);
    }
  }

  private static Map<PackageId, Range> loadDependencies(JSONObject configuration) {
    JSONObject dependencies = configuration.optJSONObject(DEPENDENCIES_ATTRIBUTE);
    if (dependencies == null) {
      return null;
    } else {
      Map<PackageId, Range> dependencyVersions = new HashMap<>();
      for (String dependency : dependencies.keySet()) {
        dependencyVersions
            .put(PackageId.parseId(dependency), Range.parse(dependencies.getString(dependency)));
      }
      return dependencyVersions;
    }
  }

  Range version() {
    return version;
  }

  public Optional<String> entryPoint() {
    return Optional.ofNullable(entryPoint);
  }

  public ModuleFormat format() {
    return format;
  }

  public Optional<Map<PackageId, Range>> dependencies() {
    return Optional.ofNullable(dependencies);
  }
}
