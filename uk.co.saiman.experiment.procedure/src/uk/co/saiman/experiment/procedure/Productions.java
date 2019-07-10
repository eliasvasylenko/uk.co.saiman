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

import uk.co.saiman.experiment.dependency.Nothing;
import uk.co.saiman.experiment.instruction.Executor;
import uk.co.saiman.experiment.production.Observation;
import uk.co.saiman.experiment.production.Preparation;
import uk.co.saiman.experiment.production.Product;
import uk.co.saiman.experiment.production.Production;
import uk.co.saiman.experiment.requirement.NoRequirement;
import uk.co.saiman.experiment.requirement.ProductRequirement;

public class Productions {
  private Productions() {}

  public static boolean produces(Executor<?> executor, Production<?> production) {
    return executor.products().anyMatch(production::equals);
  }

  /**
   * The observations which are made by experiment steps which follow this
   * procedure.
   * 
   * @return a stream of the observations which are prepared by the procedure
   */
  public static Stream<? extends Observation<?>> observations(Executor<?> executor) {
    return executor.products().filter(Observation.class::isInstance).map(p -> (Observation<?>) p);
  }

  /**
   * The preparations which are made by experiment steps which follow this
   * procedure.
   * 
   * @return a stream of the preparations which are prepared by the procedure
   */
  public static Stream<Preparation<?>> preparations(Executor<?> executor) {
    return executor.products().filter(Preparation.class::isInstance).map(p -> (Preparation<?>) p);
  }

  public static Optional<? extends Production<?>> production(Executor<?> executor, String id) {
    return executor.products().filter(c -> c.id().equals(id)).findAny();
  }

  @SuppressWarnings("unchecked")
  public static Optional<Executor<? extends Product>> asDependent(
      Executor<?> executor,
      Executor<?> parentExecutor) {
    return executor.mainRequirement() instanceof ProductRequirement<?>
        && Productions
            .produces(
                parentExecutor,
                ((ProductRequirement<?>) executor.mainRequirement()).production())
                    ? Optional.of((Executor<? extends Product>) executor)
                    : Optional.empty();
  }

  @SuppressWarnings("unchecked")
  public static <T extends Product> Optional<Executor<? super T>> asDependent(
      Executor<?> executor,
      Production<T> production) {
    return executor.mainRequirement() instanceof ProductRequirement<?>
        && ((ProductRequirement<?>) executor.mainRequirement()).production().equals(production)
            ? Optional.of((Executor<? super T>) executor)
            : Optional.empty();
  }

  @SuppressWarnings("unchecked")
  public static <S> Optional<Executor<Nothing>> asIndependent(Executor<?> executor) {
    return executor.mainRequirement() instanceof NoRequirement
        ? Optional.of((Executor<Nothing>) executor)
        : Optional.empty();
  }
}
