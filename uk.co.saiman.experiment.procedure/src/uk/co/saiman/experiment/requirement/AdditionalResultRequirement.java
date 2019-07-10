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
 * This file is part of uk.co.saiman.experiment.procedure.
 *
 * uk.co.saiman.experiment.procedure is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.procedure is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.requirement;

import java.util.function.BiFunction;
import java.util.stream.Stream;

import uk.co.saiman.experiment.graph.ExperimentPath;
import uk.co.saiman.experiment.graph.ExperimentPath.Absolute;
import uk.co.saiman.experiment.production.Observation;
import uk.co.saiman.experiment.production.Result;
import uk.co.saiman.experiment.variables.Variables;

public class AdditionalResultRequirement<T> extends AdditionalRequirement<Result<T>> {
  private final Observation<T> production;
  private final BiFunction<? super ExperimentPath<Absolute>, ? super Variables, ? extends Stream<? extends ExperimentPath<?>>> dependencies;

  AdditionalResultRequirement(
      Observation<T> production,
      BiFunction<? super ExperimentPath<Absolute>, ? super Variables, ? extends Stream<? extends ExperimentPath<?>>> dependencies) {
    this.production = production;
    this.dependencies = dependencies;
  }

  public Observation<T> production() {
    return production;
  }

  public Stream<? extends ExperimentPath<?>> dependencies(
      ExperimentPath<Absolute> path,
      Variables variables) {
    return dependencies.apply(path, variables);
  }
}
