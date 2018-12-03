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
package uk.co.saiman.experiment;

import static uk.co.saiman.reflection.token.TypeToken.forType;

import java.lang.reflect.Type;
import java.util.stream.Stream;

import uk.co.saiman.reflection.token.TypeParameter;
import uk.co.saiman.reflection.token.TypeToken;

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
 * @param <S> the type of the data describing the experiment state, including
 *        configuration and results
 */
public interface Procedure<S> {
  /**
   * @return a stream of the conditions which are prepared by the procedure
   */
  Stream<Condition> preparedConditions();

  /**
   * @return a stream of the requirements of the procedure
   */
  Stream<Condition> requiredConditions();

  /**
   * The observations made by the procedure. An experiment step which observes a
   * procedure will {@link ExperimentStep#getResult(Observation) provide a result}
   * for each observation made by the procedure.
   * 
   * @return a stream of the observations which are made by the procedure
   */
  Stream<Dependency<?>> dependencies();

  /**
   * The observations made by the procedure. An experiment step which observes a
   * procedure will {@link ExperimentStep#getResult(Observation) provide a result}
   * for each observation made by the procedure.
   * 
   * @return a stream of the observations which are made by the procedure
   */
  Stream<Observation<?>> observations();

  default boolean satisfiesRequirementsOf(Procedure<?> procedure) {
    return procedure
        .requiredConditions()
        .allMatch(requirement -> preparedConditions().anyMatch(requirement::equals));
  }

  /*
   * Execution is cheap enough that
   */
  default boolean hasAutomaticExecution() {
    return false;
  }

  /**
   * @param context the node which the configuration is being requested for
   * @return a new state object suitable for an instance of {@link ExperimentStep}
   *         over this type.
   */
  S configureVariables(ExperimentContext<S> context);

  /**
   * Process this experiment type for a given node.
   * 
   * @param context the processing context
   */
  void proceed(ProcedureContext<S> context);

  /**
   * @return the exact generic type of the configuration interface
   */
  default TypeToken<S> getVariablesType() {
    return forType(getThisType())
        .resolveSupertype(Procedure.class)
        .resolveTypeArgument(new TypeParameter<S>() {})
        .getTypeToken();
  }

  default Type getThisType() {
    return getClass();
  }
}
