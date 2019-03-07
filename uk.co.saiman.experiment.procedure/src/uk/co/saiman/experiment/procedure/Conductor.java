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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
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

import uk.co.saiman.experiment.product.Observation;
import uk.co.saiman.experiment.product.Preparation;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Production;

/**
 * An implementation of this interface represents a type of experiment node
 * which can appear in an experiment, for example "Spectrum", "Chemical Map",
 * "Stage Position", etc. Only one instance of such an implementation typically
 * needs to be registered with any given workspace.
 * 
 * <p>
 * The experiment type instance contains methods for managing the state and
 * processing of nodes of its type.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the type of the {@link #requirement() requirement}
 */
public interface Conductor<T extends Product> {
  /**
   * It is required that productions be returned in the order in which they are
   * fulfilled, as this ordering is used to validate that experiment step
   * interdependencies are satisfiable.
   * 
   * @return the productions made by this conductor
   */
  Stream<? extends Production<?>> productions();

  Stream<? extends Variable<?>> variables();

  Requirement<T> requirement();

  /*
   * Execution is cheap and resources are unlimited, so we may proceed
   * automatically when necessary
   */
  default boolean isAutomatic() {
    return false;
  }

  ExperimentConfiguration<S> configureExperiment(ConfigurationContext context);

  /**
   * Process this experiment type for a given node.
   * 
   * @param context the processing context
   */
  void conduct(ConductionContext<T> context);

  /*
   * TODO instead of having the S type parameter and a single variables object,
   * how about we attach any number of ExperimentVariable objects to a conductor
   * (or experiment step) each specifying their own `Class<T> variableType() and
   * `Accessor<S, ?> accessor()`?
   */

  public static boolean produces(Conductor<?, ?> conductor, Production<?> production) {
    return conductor.productions().anyMatch(production::equals);
  }

  /**
   * The observations which are made by experiment steps which follow this
   * procedure.
   * 
   * @return a stream of the observations which are prepared by the procedure
   */
  public static Stream<? extends Observation<?>> observations(Conductor<?, ?> conductor) {
    return conductor
        .productions()
        .filter(Observation.class::isInstance)
        .map(p -> (Observation<?>) p);
  }

  /**
   * The preparations which are made by experiment steps which follow this
   * procedure.
   * 
   * @return a stream of the preparations which are prepared by the procedure
   */
  public static Stream<Preparation<?>> preparations(Conductor<?, ?> conductor) {
    return conductor
        .productions()
        .filter(Preparation.class::isInstance)
        .map(p -> (Preparation<?>) p);
  }

  public static Optional<? extends Production<?>> production(Conductor<?, ?> conductor, String id) {
    return conductor.productions().filter(c -> c.id().equals(id)).findAny();
  }
}
