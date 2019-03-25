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
 * This file is part of uk.co.saiman.msapex.experiment.
 *
 * uk.co.saiman.msapex.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment.step.provider;

import java.util.Optional;

import uk.co.saiman.experiment.procedure.Conductor;
import uk.co.saiman.experiment.procedure.Productions;
import uk.co.saiman.experiment.procedure.Template;
import uk.co.saiman.experiment.product.Nothing;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Production;
import uk.co.saiman.properties.Localized;

public interface ExperimentStepProvider<T extends Product> {
  Localized<String> name();

  Conductor<T> conductor();

  Optional<Template<T>> createStep();

  @SuppressWarnings("unchecked")
  default <U extends Product> Optional<ExperimentStepProvider<? super U>> asDependent(
      Production<U> production) {
    return Productions
        .asDependent(conductor(), production)
        .map(c -> (ExperimentStepProvider<? super U>) this);
  }

  @SuppressWarnings("unchecked")
  default <S> Optional<ExperimentStepProvider<Nothing>> asIndependent() {
    return Productions.asIndependent(conductor()).map(c -> (ExperimentStepProvider<Nothing>) this);
  }
}
