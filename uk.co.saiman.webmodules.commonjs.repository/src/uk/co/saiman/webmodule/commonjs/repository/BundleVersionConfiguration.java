/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
import uk.co.saiman.webmodule.commonjs.Dependency;

public class BundleVersionConfiguration {
  private final Dependency version;
  private final String entryPoint;
  private final ModuleFormat format;
  private final Map<PackageId, Dependency> dependencies;

  public BundleVersionConfiguration(Dependency version, ModuleFormat format) {
    this(version, null, format, null);
  }

  private BundleVersionConfiguration(
      Dependency version,
      String entryPoint,
      ModuleFormat format,
      Map<PackageId, Dependency> dependencies) {
    this.version = requireNonNull(version);
    this.entryPoint = entryPoint;
    this.format = requireNonNull(format);
    this.dependencies = dependencies;
  }

  public static BundleVersionConfiguration loadJson(
      PackageId id,
      JSONObject configuration,
      ModuleFormat defaultFormat) {
    return new BundleVersionConfiguration(
        loadVersion(id, configuration),
        loadEntryPoint(configuration),
        loadFormat(configuration, defaultFormat),
        loadDependencies(configuration));
  }

  public static BundleVersionConfiguration getDefault(PackageId id, ModuleFormat defaultFormat) {
    return new BundleVersionConfiguration(Dependency.empty(id), defaultFormat);
  }

  private static Dependency loadVersion(PackageId id, JSONObject configuration) {
    String range = configuration.optString(VERSION_ATTRIBUTE, null);
    if (range == null) {
      return null;
    } else {
      return Dependency.parse(id, range);
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

  private static Map<PackageId, Dependency> loadDependencies(JSONObject configuration) {
    JSONObject dependencies = configuration.optJSONObject(DEPENDENCIES_ATTRIBUTE);
    if (dependencies == null) {
      return null;
    } else {
      Map<PackageId, Dependency> dependencyVersions = new HashMap<>();
      for (String dependencyString : dependencies.keySet()) {
        PackageId id = PackageId.parseId(dependencyString);
        Dependency dependency = Dependency.parse(id, dependencies.getString(dependencyString));

        dependencyVersions.put(id, dependency);
      }
      return dependencyVersions;
    }
  }

  Dependency version() {
    return version;
  }

  public Optional<String> entryPoint() {
    return Optional.ofNullable(entryPoint);
  }

  public ModuleFormat format() {
    return format;
  }

  public Optional<Map<PackageId, Dependency>> dependencies() {
    return Optional.ofNullable(dependencies);
  }
}
