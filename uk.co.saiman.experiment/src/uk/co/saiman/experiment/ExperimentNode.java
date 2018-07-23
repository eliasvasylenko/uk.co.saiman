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

import static java.util.stream.Collectors.toList;

import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.collection.StreamUtilities;
import uk.co.saiman.experiment.persistence.StateMap;
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
   * @return the ID of the type of the experiment
   */
  String getTypeId();

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
   * Remove this experiment node from its current parent and add it as a child of
   * the given parent at the given index.
   * <p>
   * This operation will {@link #clearResult() clear} any result data.
   * 
   * @param parent
   *          the parent of the new copy
   * @param index
   *          the positional index at which to add the child
   */
  void move(ExperimentNode<?, ?> parent, int index);

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
   * which is of the given {@link ExperimentType experiment type}.
   * 
   * @param type
   *          the type of the ancestor we wish to inspect
   * @return the nearest ancestor of the given type, or an empty optional if no
   *         such ancestor exists
   */
  @SuppressWarnings("unchecked")
  default <U, V> Stream<ExperimentNode<U, V>> findAncestors(ExperimentType<U, V> type) {
    return getAncestors().filter(a -> type.equals(a.getType())).map(a -> (ExperimentNode<U, V>) a);
  }

  /**
   * Remove this part from its parent, or from the containing manager if it is the
   * root part.
   */
  void remove();

  /**
   * Get all child experiment nodes, to be processed sequentially when this node
   * is processed.
   * 
   * @return An ordered list of all sequential child experiment parts
   */
  Stream<ExperimentNode<?, ?>> getChildren();

  Optional<ExperimentNode<?, ?>> getChild(String id);

  /**
   * Add an experiment node of the given type as the last child of this node.
   * 
   * @param childType
   *          the type of experiment
   * @return a new child experiment part of the given type
   */
  default <U, V> ExperimentNode<U, V> addChild(ExperimentType<U, V> childType) {
    return addChild(childType, StateMap.empty());
  }

  /**
   * Add an experiment node of the given type as the last child of this node.
   * 
   * @param childType
   *          the type of experiment
   * @return a new child experiment part of the given type
   */
  default <U, V> ExperimentNode<U, V> addChild(ExperimentType<U, V> childType, StateMap state) {
    return addChild((int) getChildren().count(), childType, state);
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
  default <U, V> ExperimentNode<U, V> addChild(int index, ExperimentType<U, V> childType) {
    return addChild(index, childType, StateMap.empty());
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
  <U, V> ExperimentNode<U, V> addChild(int index, ExperimentType<U, V> childType, StateMap state);

  /**
   * @return the current processing lifecycle state of the experiment part
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
  void process();

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
