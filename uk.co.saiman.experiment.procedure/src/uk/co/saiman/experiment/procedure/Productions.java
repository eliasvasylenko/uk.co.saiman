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
package uk.co.saiman.experiment.procedure;

import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.product.Nothing;
import uk.co.saiman.experiment.product.Observation;
import uk.co.saiman.experiment.product.Preparation;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Production;

public class Productions {
  private Productions() {}

  public static boolean produces(Conductor<?> conductor, Production<?> production) {
    return conductor.products().anyMatch(production::equals);
  }

  /**
   * The observations which are made by experiment steps which follow this
   * procedure.
   * 
   * @return a stream of the observations which are prepared by the procedure
   */
  public static Stream<? extends Observation<?>> observations(Conductor<?> conductor) {
    return conductor
        .products()
        .filter(Observation.class::isInstance)
        .map(p -> (Observation<?>) p);
  }

  /**
   * The preparations which are made by experiment steps which follow this
   * procedure.
   * 
   * @return a stream of the preparations which are prepared by the procedure
   */
  public static Stream<Preparation<?>> preparations(Conductor<?> conductor) {
    return conductor
        .products()
        .filter(Preparation.class::isInstance)
        .map(p -> (Preparation<?>) p);
  }

  public static Optional<? extends Production<?>> production(Conductor<?> conductor, String id) {
    return conductor.products().filter(c -> c.id().equals(id)).findAny();
  }

  @SuppressWarnings("unchecked")
  public static <T extends Product> Optional<Conductor<? super T>> asDependent(
      Conductor<?> conductor,
      Production<T> production) {
    return conductor.directRequirement().resolveDependency(production).isPresent()
        ? Optional.of((Conductor<? super T>) conductor)
        : Optional.empty();
  }

  @SuppressWarnings("unchecked")
  public static <S> Optional<Conductor<Nothing>> asIndependent(Conductor<?> conductor) {
    return conductor.directRequirement().isIndependent()
        ? Optional.of((Conductor<Nothing>) conductor)
        : Optional.empty();
  }
}
