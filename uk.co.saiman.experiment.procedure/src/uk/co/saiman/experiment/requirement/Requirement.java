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
 * This file is part of uk.co.saiman.experiment.graph.
 *
 * uk.co.saiman.experiment.graph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.graph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.requirement;

import uk.co.saiman.experiment.dependency.Dependency;
import uk.co.saiman.experiment.dependency.Product;
import uk.co.saiman.experiment.dependency.source.Observation;
import uk.co.saiman.experiment.dependency.source.Preparation;
import uk.co.saiman.experiment.dependency.source.Production;
import uk.co.saiman.experiment.dependency.source.Provision;
import uk.co.saiman.experiment.requirement.ResultRequirement.Cardinality;

public abstract class Requirement<T extends Dependency> {
  public static NoRequirement none() {
    return NoRequirement.INSTANCE;
  }

  public static <T> ResultRequirement<T> on(Observation<T> observation) {
    return new ResultRequirement<>(observation);
  }

  public static <T> ResultRequirement<T> on(Observation<T> observation, Cardinality cardinality) {
    return new ResultRequirement<>(observation, cardinality);
  }

  public static <T> ConditionRequirement<T> on(Preparation<T> preparation) {
    return new ConditionRequirement<>(preparation);
  }

  public static <T> ResourceRequirement<T> on(Provision<T> provision) {
    return new ResourceRequirement<>(provision);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Product> ProductRequirement<T> on(Production<?> production) {
    return production instanceof Observation<?>
        ? (ProductRequirement<T>) on((Observation<?>) production)
        : (ProductRequirement<T>) on((Preparation<?>) production);
  }

  // TODO sealed interface when language feature becomes available
  Requirement() {}
}