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

import uk.co.saiman.experiment.production.Dependency;
import uk.co.saiman.experiment.production.Observation;
import uk.co.saiman.experiment.production.Preparation;
import uk.co.saiman.experiment.production.Product;
import uk.co.saiman.experiment.production.Production;
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

  @SuppressWarnings("unchecked")
  public static <T extends Product> ProductRequirement<T> on(Production<?> production) {
    return production instanceof Observation<?>
        ? (ProductRequirement<T>) on((Observation<?>) production)
        : (ProductRequirement<T>) on((Preparation<?>) production);
  }

  // TODO sealed interface when language feature becomes available
  Requirement() {}

  public boolean isIndependent() {
    return false;
  }
}
