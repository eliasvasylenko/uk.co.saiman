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
package uk.co.saiman.experiment.instruction;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import uk.co.saiman.experiment.graph.ExperimentPath;
import uk.co.saiman.experiment.production.Product;
import uk.co.saiman.experiment.requirement.ProductRequirement;
import uk.co.saiman.experiment.requirement.Requirement;

public class IndirectRequirements {
  private final ProductRequirement<?> requirement;
  private final Function<Instruction<?>, Stream<? extends ExperimentPath<?>>> dependencies;

  public IndirectRequirements(
      ProductRequirement<?> requirement,
      Function<Instruction<?>, Stream<? extends ExperimentPath<?>>> dependencies) {
    this.requirement = requirement;
    this.dependencies = dependencies;
  }

  public ProductRequirement<?> requirement() {
    return requirement;
  }

  public Stream<? extends ExperimentPath<?>> dependencies(Instruction<?> instruction) {
    return dependencies.apply(instruction);
  }

  public <U extends Product> Optional<IndirectRequirements> matching(Requirement<U> requirement) {
    return this.requirement.equals(requirement)
        ? Optional.of((IndirectRequirements) this)
        : Optional.empty();
  }
}
