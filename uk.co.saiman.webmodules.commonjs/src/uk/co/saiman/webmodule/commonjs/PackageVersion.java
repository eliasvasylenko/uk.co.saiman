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
package uk.co.saiman.webmodule.commonjs;

import java.util.stream.Stream;

import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.semver.Version;

/**
 * A CommonJS registry package version. Provides at least one {@link Resource
 * resource} containing the module, but may contain multiple resources of
 * different {@link ResourceType types}. Also provides the dependencies of the
 * package version.
 * 
 * @author Elias N Vasylenko
 */
public interface PackageVersion {
  PackageId getName();

  Version getVersion();

  Stream<ResourceType> getResources();

  Resource getResource(ResourceType type);

  Stream<PackageId> getDependencies();

  Dependency getDependency(PackageId module);
}
