/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.declaration.
 *
 * uk.co.saiman.experiment.declaration is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.declaration is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.output;

import static uk.co.saiman.experiment.dependency.ProductPath.toResult;

import java.util.stream.Stream;

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.dependency.ProductPath;
import uk.co.saiman.experiment.dependency.Result;

public interface Output {
  Stream<Result<?>> results();

  <U extends ExperimentPath<U>> Stream<ProductPath<U, ? extends Result<?>>> resultPaths(
      ExperimentPath<U> path);

  <T extends Result<?>> T resolveResult(ProductPath<?, T> path);

  default <T> Result<T> resolveResult(ExperimentPath<?> path, Class<T> type) {
    return resolveResult(toResult(path, type));
  }
}
