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

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.reflection.token.TypeToken.forType;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.collection.StreamUtilities;
import uk.co.saiman.observable.ObservableValue;
import uk.co.saiman.reflection.token.TypeArgument;
import uk.co.saiman.reflection.token.TypeToken;
import uk.co.saiman.reflection.token.TypedReference;

/**
 * This class provides a common interface for manipulating, inspecting, and
 * reflecting over the constituent nodes of an experiment. Each node is
 * associated with an implementation of {@link ExperimentType}.
 * <p>
 * Instances of {@link ExperimentNode} are constructed internally by a
 * {@link Workspace workspace} according to their {@link ExperimentType type}.
 * 
 * @author Elias N Vasylenko
 * @param <T>
 *          the exact type of the experiment type
 * @param <S>
 *          the type of the data describing the experiment configuration
 */
public interface ExperimentNode<T extends ExperimentType<S>, S> {
  /**
   * @return The ID of the node, as configured via
   *         {@link ExperimentConfigurationContext}. The ID should be unique
   *         amongst the children of a node's parent.
   */
  String getId();

  /**
   * @return the experiment workspace containing this experiment
   */
  Workspace getWorkspace();

  /**
   * @return the current state object of the experiment node
   */
  S getState();

  /**
   * @return the type of the experiment
   */
  T getType();

  /**
   * @return the parent part of this experiment, if present, otherwise an empty
   *         optional
   */
  Optional<ExperimentNode<?, ?>> getParent();

  /**
   * @return the path of the experiment node data relative to the
   *         {@link Workspace#getRootPath() workspace root}.
   */
  Path getDataPath();

  /**
   * @return the absolute path of the experiment node data from the
   *         {@link Workspace#getRootPath() workspace root}.
   */
  Path getAbsoluteDataPath();

  /**
   * @return the node's index in its parent's list of children
   */
  default int getIndex() {
    return getParent().map(p -> p.getChildren().collect(toList()).indexOf(this)).orElse(
        getWorkspace().getExperiments().collect(toList()).indexOf(this));
  }

  /**
   * @return the root part of the experiment tree this part occurs in
   */
  default Experiment getExperiment() {
    return (Experiment) getAncestor(getWorkspace().getExperimentRootType()).get();
  }

  /**
   * @return a list of all ancestors, nearest first, inclusive of the node
   *         itself
   */
  default Stream<ExperimentNode<?, ?>> getAncestors() {
    return StreamUtilities.<ExperimentNode<?, ?>> iterateOptional(this, ExperimentNode::getParent);
  }

  /**
   * Get the nearest available ancestor node of the processing experiment node
   * which is of the given {@link ExperimentType experiment type}.
   * 
   * @param type
   *          the type of the ancestor we wish to inspect
   * @return the nearest ancestor of the given type, or an empty optional if no
   *         such ancestor exists
   */
  @SuppressWarnings("unchecked")
  default <U, E extends ExperimentType<U>> Optional<ExperimentNode<E, U>> getAncestor(E type) {
    return getAncestors().filter(a -> type.equals(a.getType())).findFirst().map(
        a -> (ExperimentNode<E, U>) a);
  }

  /**
   * Get the nearest available ancestor node of the processing experiment node
   * which is of one of the given {@link ExperimentType experiment types}.
   * 
   * @param types
   *          the possible types of the ancestor we wish to inspect
   * @return the nearest ancestor of the given type, or an empty optional if no
   *         such ancestor exists
   */
  @SuppressWarnings("unchecked")
  default <U, E extends ExperimentType<? extends U>> Optional<ExperimentNode<E, ? extends U>> getAncestor(
      Collection<E> types) {
    return getAncestors().filter(a -> types.contains(a.getType())).findFirst().map(
        a -> (ExperimentNode<E, ? extends U>) a);
  }

  /**
   * Get the ancestor nodes of the processing experiment node which are of one
   * of the given {@link ExperimentType experiment types}.
   * 
   * @param types
   *          the possible types of the ancestor we wish to inspect
   * @return a stream of ancestor nodes of the given type, from the nearest
   */
  @SuppressWarnings("unchecked")
  default <U, E extends ExperimentType<? extends U>> Stream<ExperimentNode<E, ? extends U>> getAncestors(
      Collection<E> types) {
    return getAncestors().filter(a -> types.contains(a.getType())).map(
        a -> (ExperimentNode<E, ? extends U>) a);
  }

  /**
   * Remove this part from its parent, or from the containing manager if it is
   * the root part.
   */
  void remove();

  /**
   * Get all child experiment parts, to be executed sequentially during this
   * parts {@link ExperimentLifecycleState#PROCESSING} state.
   * 
   * @return An ordered list of all sequential child experiment parts
   */
  Stream<ExperimentNode<?, ?>> getChildren();

  /**
   * @return All known available child experiment types
   */
  Stream<ExperimentType<?>> getAvailableChildExperimentTypes();

  /**
   * Add a child experiment node of the given type to this node.
   * 
   * @param childType
   *          The type of experiment
   * @return A new child experiment part of the given type
   */
  <U, E extends ExperimentType<U>> ExperimentNode<E, U> addChild(E childType);

  /**
   * @return the current execution lifecycle state of the experiment part
   */
  ObservableValue<ExperimentLifecycleState> lifecycleState();

  default TypeToken<ExperimentNode<T, S>> getThisTypeToken() {
    @SuppressWarnings("unchecked")
    TypeToken<T> typeType = (TypeToken<T>) forType(getType().getThisType());

    return new TypeToken<ExperimentNode<T, S>>() {}.withTypeArguments(
        new TypeArgument<T>(typeType) {},
        new TypeArgument<S>(getType().getStateType()) {});
  }

  default TypedReference<ExperimentNode<T, S>> asTypedObject() {
    return TypedReference.typedObject(getThisTypeToken(), this);
  }

  /**
   * Process this experiment node. The request will be passed down to the root
   * experiment node and processing will proceed back down the ancestor
   * hierarchy to this node. If the experiment is already in progress then
   * invocation of this method should fail.
   */
  void process();

  /**
   * @return all results associated with this node
   */
  Stream<Result<?>> getResults();

  /**
   * Clear all the results associated with this node. Take care, as this will
   * also delete any result data from disk.
   */
  void clearResults();

  /**
   * @param resultType
   *          the result type to set the result data for
   * @return the result associated with this node for the given result type
   */
  <U> Result<U> getResult(ResultType<U> resultType);
}
