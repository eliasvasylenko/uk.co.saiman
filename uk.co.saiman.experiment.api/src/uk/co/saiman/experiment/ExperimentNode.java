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
 * @param <S>
 *          the type of the data describing the experiment configuration
 * @param <T>
 *          the type of the data describing the experiment result
 */
public interface ExperimentNode<S, T> {
  /**
   * @return The ID of the node, as configured via {@link ConfigurationContext}.
   *         The ID should be unique amongst the children of a node's parent.
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
  ExperimentType<S, T> getType();

  /**
   * @return the parent part of this experiment, if present, otherwise an empty
   *         optional
   */
  Optional<ExperimentNode<?, ?>> getParent();

  /**
   * @return the node's index in its parent's list of children
   */
  default int getIndex() {
    return getParent().get().getChildren().collect(toList()).indexOf(this);
  }

  /**
   * Move the node to the given index. A node cannot be reparented, it can only be
   * moved to a different index under its existing parent (or within workspace in
   * the case of a root node).
   * 
   * @param index
   *          the index to move the node to
   * @throws IndexOutOfBoundsException
   */
  void moveToIndex(int index);

  /**
   * @return the root part of the experiment tree this part occurs in
   */
  default Experiment getExperiment() {
    return (Experiment) findAncestor(getWorkspace().getExperimentRootType()).get();
  }

  /**
   * @return a list of all ancestors, nearest first, inclusive of the node itself
   */
  default Stream<ExperimentNode<?, ?>> getAncestors() {
    return StreamUtilities.<ExperimentNode<?, ?>>iterateOptional(this, ExperimentNode::getParent);
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
  default <U, V> Optional<ExperimentNode<U, V>> findAncestor(ExperimentType<U, V> type) {
    return getAncestors()
        .filter(a -> type.equals(a.getType()))
        .findFirst()
        .map(a -> (ExperimentNode<U, V>) a);
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
  default <U, V> Optional<ExperimentNode<? extends U, ? extends V>> findAncestor(
      Collection<? extends ExperimentType<? extends U, ? extends V>> types) {
    return getAncestors()
        .filter(a -> types.contains(a.getType()))
        .findFirst()
        .map(a -> (ExperimentNode<? extends U, ? extends V>) a);
  }

  /**
   * Get the ancestor nodes of the processing experiment node which are of one of
   * the given {@link ExperimentType experiment types}.
   * 
   * @param types
   *          the possible types of the ancestor we wish to inspect
   * @return a stream of ancestor nodes of the given type, from the nearest
   */
  @SuppressWarnings("unchecked")
  default <U, V> Stream<ExperimentNode<? extends U, ? extends V>> findAncestors(
      Collection<? extends ExperimentType<? extends U, ? extends V>> types) {
    return getAncestors()
        .filter(a -> types.contains(a.getType()))
        .map(a -> (ExperimentNode<? extends U, ? extends V>) a);
  }

  /**
   * Remove this part from its parent, or from the containing manager if it is the
   * root part.
   */
  void remove();

  /**
   * Get all child experiment parts, to be executed sequentially during this parts
   * {@link ExperimentLifecycleState#PROCESSING} state.
   * 
   * @return An ordered list of all sequential child experiment parts
   */
  Stream<ExperimentNode<?, ?>> getChildren();

  Optional<ExperimentNode<?, ?>> getChild(String id);

  /**
   * @return All known available child experiment types
   */
  Stream<ExperimentType<?, ?>> getAvailableChildExperimentTypes();

  /**
   * Add an experiment node of the given type as the last child of this node.
   * 
   * @param childType
   *          the type of experiment
   * @return a new child experiment part of the given type
   */
  default <U, V> ExperimentNode<U, V> addChild(ExperimentType<U, V> childType) {
    return addChild(childType, (int) getChildren().count());
  }

  /**
   * Add an experiment node of the given type as a child of this node.
   * 
   * @param childType
   *          the type of experiment
   * @param index
   *          the positional index at which to add the child
   * @return a new child experiment part of the given type
   */
  <U, V> ExperimentNode<U, V> addChild(ExperimentType<U, V> childType, int index);

  /**
   * Add a copy of the given experiment node as the last child of this node.
   * 
   * @param node
   *          the node to copy
   * @return a new child experiment part of the same type and with the same state
   *         as that given
   */
  default <U, V> ExperimentNode<U, V> addCopy(ExperimentNode<U, V> node) {
    return addCopy(node, (int) getChildren().count());
  }

  /**
   * Add a copy of the given experiment node as a child of this node.
   * 
   * @param node
   *          the node to copy
   * @param index
   *          the positional index at which to add the child
   * @return a new child experiment part of the same type and with the same state
   *         as that given
   */
  <U, V> ExperimentNode<U, V> addCopy(ExperimentNode<U, V> node, int index);

  /**
   * @return the current execution lifecycle state of the experiment part
   */
  ObservableValue<ExperimentLifecycleState> lifecycleState();

  default TypeToken<ExperimentNode<S, T>> getThisTypeToken() {
    return new TypeToken<ExperimentNode<S, T>>() {}
        .withTypeArguments(
            new TypeArgument<S>(getType().getStateType()) {},
            new TypeArgument<T>(getType().getResultType()) {});
  }

  default TypedReference<ExperimentNode<S, T>> asTypedObject() {
    return TypedReference.typedObject(getThisTypeToken(), this);
  }

  /**
   * Process this experiment node. The request will be passed down to the root
   * experiment node and processing will proceed back down the ancestor hierarchy
   * to this node. If the experiment is already in progress then invocation of
   * this method should fail.
   */
  void execute();

  /**
   * Get the result associated with this node.
   * 
   * @return an optional containing the result, or an empty optional if the
   *         experiment type has no result type
   */
  Result<T> getResult();

  /**
   * Clear all the results associated with this node. Take care, as this will also
   * delete any result data from disk.
   */
  void clearResult();
}
