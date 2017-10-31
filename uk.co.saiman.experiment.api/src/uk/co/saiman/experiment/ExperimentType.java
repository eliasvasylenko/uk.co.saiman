/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.api.
 *
 * uk.co.saiman.experiment.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.api is distributed in the hope that it will be useful,
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
import java.util.Optional;

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
 * @param <S>
 *          the type of the data describing the experiment state, including
 *          configuration and results
 */
public interface ExperimentType<S> {
  /**
   * @return the unique and persistent ID of the experiment type
   */
  String getId();

  /**
   * @return the human readable name of the experiment type
   */
  String getName();

  default boolean hasAutomaticExecution() {
    return false;
  }

  default boolean isExecutionContextDependent() {
    return true;
  }

  /**
   * @param context
   *          the node which the configuration is being requested for
   * @return a new state object suitable for an instance of
   *         {@link ExperimentNode} over this type.
   */
  S createState(ConfigurationContext<S> context);

  /**
   * Execute this experiment type for a given node.
   * 
   * @param context
   *          the execution context
   */
  void execute(ExecutionContext<S> context);

  /**
   * If this experiment node produces a result upon execution, get the type of
   * the result which is produced.
   * 
   * @return an optional containing the type of the result, or else an empty
   *         optional
   */
  default Optional<? extends ResultType<?>> getResultType() {
    return Optional.empty();
  }

  /**
   * Test whether a node of this type may follow from the given directly
   * preceding node and be validly added as its child.
   * 
   * @param parentNode
   *          the candidate parent node
   * @return true if a node of this type may be added as a child, false
   *         otherwise
   */
  boolean mayComeAfter(ExperimentNode<?, ?> parentNode);

  /**
   * Test whether a node of the given type may follow from the given node and be
   * validly added as its child. The penultimate descendant node should be a
   * descendant of a node of this type.
   * <p>
   * This test is performed on all ancestors when an attempt is made to add a
   * new node.
   * 
   * @param penultimateDescendantNode
   *          the candidate parent node
   * @param descendantNodeType
   *          the candidate child node
   * @return true if a node of the given type may be added as a child of the
   *         given node, false otherwise
   */
  boolean mayComeBefore(
      ExperimentNode<?, ?> penultimateDescendantNode,
      ExperimentType<?> descendantNodeType);

  /**
   * @return the exact generic type of the configuration interface
   */
  default TypeToken<S> getStateType() {
    return forType(getThisType())
        .resolveSupertype(ExperimentType.class)
        .resolveTypeArgument(new TypeParameter<S>() {})
        .getTypeToken();
  }

  default Type getThisType() {
    return getClass();
  }
}
