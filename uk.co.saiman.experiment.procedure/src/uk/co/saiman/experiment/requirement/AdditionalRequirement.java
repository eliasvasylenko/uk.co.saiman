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

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.dependency.Dependency;
import uk.co.saiman.experiment.dependency.source.Observation;
import uk.co.saiman.experiment.dependency.source.Provision;
import uk.co.saiman.experiment.variables.Variables;

public abstract class AdditionalRequirement<T extends Dependency> {
  AdditionalRequirement() {}

  public static <T> AdditionalResultRequirement<T> on(
      Observation<T> observation,
      BiFunction<? super ExperimentPath<Absolute>, ? super Variables, ? extends Stream<? extends ExperimentPath<?>>> dependencies) {
    return new AdditionalResultRequirement<>(observation, dependencies);
  }

  public static <T> AdditionalResourceRequirement<T> on(Provision<T> provision) {
    return new AdditionalResourceRequirement<>(provision);
  }
}
