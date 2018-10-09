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

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.experiment.ExperimentLifecycleState.DETACHED;
import static uk.co.saiman.experiment.ExperimentLifecycleState.PROCESSING;
import static uk.co.saiman.reflection.token.TypedReference.typedObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.collection.StreamUtilities;
import uk.co.saiman.data.Data;
import uk.co.saiman.data.DataException;
import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.resource.Location;
import uk.co.saiman.experiment.ResultStore.Storage;
import uk.co.saiman.experiment.state.StateMap;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.reflection.token.TypeArgument;
import uk.co.saiman.reflection.token.TypeToken;
import uk.co.saiman.reflection.token.TypedReference;

/**
 * This class provides a common interface for manipulating, inspecting, and
 * reflecting over the constituent nodes of an experiment. Each node is
 * associated with an implementation of {@link ExperimentProcedure}.
 * <p>
 * Instances of {@link ExperimentNode} are constructed internally by a
 * {@link Workspace workspace} according to their {@link ExperimentProcedure
 * type}.
 * 
 * @author Elias N Vasylenko
 * @param <S>
 *          the type of the data describing the experiment configuration
 * @param <T>
 *          the type of the data describing the experiment result
 */
public class ExperimentNode<S, T> {
  private String id;
  private ExperimentProcedure<S, T> procedure;
  private StateMap stateMap;
  private ExperimentLifecycleState lifecycleState;

  private ExperimentNode<?, ?> parent;
  private final List<ExperimentNode<?, ?>> children;

  private final S variables;
  private final Result<T> result;
  private Data<T> resultData;
  private boolean processedChildrenInline;
  private Storage resultStorage;

  private final HotObservable<ExperimentEvent> events = new HotObservable<>();

  public ExperimentNode(ExperimentProcedure<S, T> procedure) {
    this(procedure, null, StateMap.empty());
  }

  public ExperimentNode(ExperimentProcedure<S, T> procedure, String id, StateMap stateMap) {
    this.id = id;
    this.procedure = procedure;
    this.stateMap = stateMap;
    this.lifecycleState = DETACHED;

    this.parent = null;
    this.children = new ArrayList<>();

    this.result = new Result<>(this);
    this.variables = createVariables(id);
  }

  /**
   * @return The ID of the node, as configured via {@link ConfigurationContext}.
   *         The ID should be unique amongst the children of a node's parent.
   */
  public String getId() {
    return id;
  }

  void setId(String id) {
    if (!ExperimentConfiguration.isNameValid(id)) {
      throw new ExperimentException(format("Invalid experiment name %s", id));
    }

    lockExperiment().run(() -> {
      if (Objects.equals(id, getId())) {
        return;
      }

      if (getParent().map(p -> p.getChildren().anyMatch(s -> id.equals(s.getId()))).orElse(false)) {
        throw new ExperimentException(
            format("Experiment node with id %s already attached at node %s", id, parent));

      } else {
        try {
          if (resultStorage != null) {
            resultStorage = getExperiment()
                .get()
                .getResultStore()
                .relocateStorage(this, resultStorage);
          }
        } catch (IOException e) {
          throw new ExperimentException(format("Failed to set experiment name %s", id));
        }

        this.id = id;
      }

      fireEvent(new RenameNodeEvent(this, id));
    });
  }

  /**
   * @return the type of the experiment
   */
  public ExperimentProcedure<S, T> getProcedure() {
    return procedure;
  }

  public StateMap getStateMap() {
    return stateMap;
  }

  /**
   * @return the current state object of the experiment node
   */
  public S getVariables() {
    return variables;
  }

  private S createVariables(String id) {
    if (id != null) {
      setId(id);
    }

    S state = procedure.configureVariables(createConfigurationContext());

    if (getId() == null) {
      throw new ExperimentException("Cannot initialise experiment state with null id");
    }

    return state;
  }

  private ConfigurationContext<S> createConfigurationContext() {
    return new ConfigurationContext<S>() {
      @Override
      public ExperimentNode<S, T> node() {
        return ExperimentNode.this;
      }

      @Override
      public StateMap stateMap() {
        return stateMap;
      }

      @Override
      public void update(StateMap stateMap) {
        ExperimentNode.this.setStateMap(stateMap);
      }

      @Override
      public void setId(String id) {
        ExperimentNode.this.setId(id);
      }

      @Override
      public String getId() {
        if (id == null)
          throw new NullPointerException();
        return id;
      }

      @Override
      public String getId(Supplier<String> defaultId) {
        if (id == null) {
          setId(defaultId.get());
        }
        return getId();
      }
    };
  }

  protected void setStateMap(StateMap stateMap) {
    if (Objects.equals(stateMap, this.stateMap)) {
      return;
    }

    lockExperiment().run(() -> {
      if (Objects.equals(stateMap, this.stateMap)) {
        return;
      }

      StateMap previous = this.stateMap;
      this.stateMap = stateMap;

      fireEvent(new ExperimentVariablesEvent(this, previous));
    });
  }

  public TypeToken<ExperimentNode<S, T>> getThisTypeToken() {
    return new TypeToken<ExperimentNode<S, T>>() {}
        .withTypeArguments(
            new TypeArgument<S>(getProcedure().getVariablesType()) {},
            new TypeArgument<T>(getProcedure().getResultType()) {});
  }

  public TypedReference<ExperimentNode<S, T>> asTypedObject() {
    return typedObject(getThisTypeToken(), this);
  }

  /*
   * Experiment Lifecycle
   */

  /**
   * @return the current processing lifecycle state of the experiment part
   */
  public ExperimentLifecycleState getLifecycleState() {
    return lifecycleState;
  }

  public Observable<ExperimentEvent> events() {
    return events;
  }

  void fireEventLocal(ExperimentEvent event) {
    events.next(event);
  }

  void fireEvent(ExperimentEvent event) {
    fireEventLocal(event);
    if (parent != null) {
      parent.fireEvent(event);
    }
  }

  ExperimentLocker lockExperiment() {
    return new ExperimentLocker(this);
  }

  private static ExperimentLocker lockExperiments(ExperimentNode<?, ?>... experimentNodes) {
    return new ExperimentLocker(experimentNodes);
  }

  /*
   * Experiment Hierarchy
   */

  /**
   * @return the parent part of this experiment, if present, otherwise an empty
   *         optional
   */
  public Optional<ExperimentNode<?, ?>> getParent() {
    return Optional.ofNullable(parent);
  }

  /**
   * @return the node's index in its parent's list of children
   */
  public int getIndex() {
    return lockExperiment()
        .run(() -> getParent().get().getChildren().collect(toList()).indexOf(this));
  }

  /**
   * Get all child experiment nodes, to be processed sequentially when this node
   * is processed.
   * 
   * @return An ordered list of all sequential child experiment parts
   */
  public Stream<ExperimentNode<?, ?>> getChildren() {
    return lockExperiment().run(() -> new ArrayList<>(children)).stream();
  }

  public Optional<ExperimentNode<?, ?>> getChild(String id) {
    return lockExperiment().run(() -> getChildren().filter(c -> c.getId().equals(id)).findAny());
  }

  public void attach(ExperimentNode<?, ?> node) {
    lockExperiments(this, node).run(() -> attachImpl(node, (int) getChildren().count()));
  }

  public void attach(ExperimentNode<?, ?> node, int index) {
    lockExperiments(this, node).run(() -> attachImpl(node, index));
  }

  private void attachImpl(ExperimentNode<?, ?> node, int index) {
    if (index > children.size()) {
      throw new ExperimentException(
          format("Experiment node index %s is out of range at node %s", index, this));
    }

    if (node.parent == this) {
      int previousIndex = node.getIndex();

      children.remove(node);
      children.add(index, node);

      if (previousIndex > index) {
        fireEvent(new ReorderExperimentEvent(this, previousIndex));
        for (int i = index; i < previousIndex; i++) {
          fireEvent(new ReorderExperimentEvent(children.get(i + 1), i));
        }
      } else if (previousIndex < index) {
        for (int i = previousIndex; i < index; i++) {
          fireEvent(new ReorderExperimentEvent(children.get(i), i + 1));
        }
        fireEvent(new ReorderExperimentEvent(this, previousIndex));
      }
    } else {
      if (lifecycleState == PROCESSING) {
        throw new ExperimentException(
            format("Cannot detach experiment %s while in the %s state", getId(), PROCESSING));
      }

      children.forEach(child -> {
        if (child.getId().equals(node.getId())) {
          throw new ExperimentException(
              format("Experiment node with id %s already attached at node %s", node.getId(), this));
        }
      });

      ExperimentNode<?, ?> previousParent = node.parent;
      node.parent = this;
      children.add(node);

      if (previousParent != null) {
        previousParent.children.remove(this);

        setDetached();

        DetachNodeEvent detachEvent = new DetachNodeEvent(node, previousParent);
        fireEventLocal(detachEvent);
        previousParent.fireEvent(detachEvent);
      }
      fireEvent(new AttachNodeEvent(node, this));
    }
  }

  public void detach(ExperimentNode<?, ?> node) {
    lockExperiment().run(() -> detachImpl(node));
  }

  private void detachImpl(ExperimentNode<?, ?> node) {
    if (lifecycleState == PROCESSING) {
      throw new ExperimentException(
          format("Cannot detach experiment %s while in the %s state", getId(), PROCESSING));
    }

    if (node.parent == this) {
      children.remove(this);
      node.parent = null;

      setDetached();

      fireEvent(new DetachNodeEvent(node, this));
    }
  }

  private void setDetached() {
    result.unsetValue();
    ExperimentLifecycleState previousLifecycleState = lifecycleState;
    if (previousLifecycleState != DETACHED) {
      lifecycleState = DETACHED;
      fireEvent(new ExperimentLifecycleEvent(this, lifecycleState, previousLifecycleState));
    }
  }

  /**
   * @return the root part of the experiment tree this part occurs in
   */
  public Optional<Experiment> getExperiment() {
    return lockExperiment().run(() -> getExperimentImpl());
  }

  public Optional<Experiment> getExperimentImpl() {
    List<ExperimentNode<?, ?>> ancestors = getAncestorsImpl();
    ExperimentNode<?, ?> root = ancestors.get(ancestors.size() - 1);
    if (root instanceof Experiment) {
      return Optional.of((Experiment) root);
    } else {
      return Optional.empty();
    }
  }

  /**
   * @return the experiment workspace containing this experiment
   */
  public Optional<Workspace> getWorkspace() {
    return lockExperiment().run(() -> getExperimentImpl().flatMap(Experiment::getWorkspace));
  }

  /**
   * @return a list of all ancestors, nearest first, inclusive of the node itself
   */
  public Stream<ExperimentNode<?, ?>> getAncestors() {
    return lockExperiment().run(() -> getAncestorsImpl()).stream();
  }

  private List<ExperimentNode<?, ?>> getAncestorsImpl() {
    // collect and re-stream, as we need to collect the list whilst locked.
    return StreamUtilities
        .<ExperimentNode<?, ?>>iterateOptional(this, ExperimentNode::getParent)
        .collect(toList());
  }

  /**
   * Get the nearest available ancestor node of the processing experiment node
   * which is of the given {@link ExperimentProcedure experiment type}.
   * 
   * @param procedure
   *          the type of the ancestor we wish to inspect
   * @return the nearest ancestor of the given type, or an empty optional if no
   *         such ancestor exists
   */
  @SuppressWarnings("unchecked")
  public <U, V> Optional<ExperimentNode<U, V>> findAncestor(ExperimentProcedure<U, V> procedure) {
    return getAncestors()
        .filter(a -> procedure.equals(a.getProcedure()))
        .findFirst()
        .map(a -> (ExperimentNode<U, V>) a);
  }

  /**
   * Get the nearest available ancestor node of the processing experiment node
   * which is of the given {@link ExperimentProcedure experiment type}.
   * 
   * @param procedure
   *          the type of the ancestor we wish to inspect
   * @return the nearest ancestor of the given type, or an empty optional if no
   *         such ancestor exists
   */
  @SuppressWarnings("unchecked")
  public <U, V> Stream<ExperimentNode<U, V>> findAncestors(ExperimentProcedure<U, V> procedure) {
    return getAncestors()
        .filter(a -> procedure.equals(a.getProcedure()))
        .map(a -> (ExperimentNode<U, V>) a);
  }

  /*
   * Experiment Processing
   */

  /**
   * Process this experiment node. The request will be passed down to the root
   * experiment node and processing will proceed back down the ancestor hierarchy
   * to this node. If the experiment is already in progress then invocation of
   * this method should fail.
   */
  public void process() {
    try {
      processAncestors(getAncestors().collect(toList()));
    } catch (Exception e) {
      // TODO setLifecycleState(ExperimentLifecycleState.FAILURE, true);
      throw new ExperimentException(format("Failed to process experiment %s", getId()), e);
    }
  }

  /**
   * Get the result associated with this node.
   * 
   * @return an optional containing the result, or an empty optional if the
   *         experiment type has no result type
   */
  public Result<T> getResult() {
    return result;
  }

  /**
   * Clear all the results associated with this node. Take care, as this will also
   * delete any result data from disk.
   */
  public void clearResult() {
    result.unsetValue();
  }

  private void processAncestors(List<ExperimentNode<?, ?>> ancestors) {
    ExperimentNode<?, ?> node = ancestors.remove(ancestors.size() - 1);

    if (!ancestors.isEmpty()) {
      node.processImpl(() -> node.processAncestors(ancestors));
    } else {
      node.processImpl(node::processChildren);
    }
  }

  private void processChildren() {
    getChildren().forEach(n -> n.processImpl(n::processChildren));
  }

  private void processImpl(Runnable processChildren) {
    // TODO setLifecycleState(ExperimentLifecycleState.PROCESSING, false);

    if (parent != null) {
      if (!procedure.mayComeAfter(parent.procedure)) {
        throw new ExperimentException(
            format(
                "Experiment procedure %s does not fulfil constraint on containing procedure %s",
                procedure,
                parent));
      }
    }

    result.unsetValue();
    if (resultData != null) {
      resultData.unset();
      resultData.save();
      resultData = null;
    }
    processedChildrenInline = false;

    T result = getProcedure().process(createProcessingContext(processChildren));

    if (getProcedure().getResultType().getErasedType() != void.class) {
      this.result.setValue(result);
      if (resultData != null) {
        resultData.set(result);
      }
    }

    try {
      synchronized (this) {
        // TODO setLifecycleState(ExperimentLifecycleState.COMPLETION, false);
      }
    } catch (CancellationException e) {
      // TODO setLifecycleState(ExperimentLifecycleState.FAILURE, true);
      this.result.unsetValue();
      throw e;
    }

    if (!processedChildrenInline) {
      processChildren.run();
    }
  }

  private ProcessingContext<S, T> createProcessingContext(Runnable processChildren) {
    /*
     * TODO once processed this must become inoperable, including the proxied Data
     * from setResult. Make sure this is synchronized!
     */
    return new ProcessingContext<S, T>() {
      @Override
      public ExperimentNode<S, T> node() {
        return ExperimentNode.this;
      }

      @Override
      public void processChildren() {
        processedChildrenInline = true;
        processChildren.run();
      }

      @Override
      public Location getLocation() {
        return resultStorage.location();
      }

      @Override
      public void setPartialResult(T value) {
        setPartialResult(() -> value);
      }

      @Override
      public void setPartialResult(Supplier<T> value) {
        result.setValueSupplier(value);
      }

      @Override
      public void setResultData(Data<T> data) {
        T value = data.get();
        if (value != null)
          result.setValue(value);
        else
          result.unsetValue();

        resultData = data;
      }

      @Override
      public void setResultFormat(String name, DataFormat<T> format) {
        try {
          resultData = Data.locate(getLocation(), name, format);
        } catch (DataException e) {
          new ExperimentException("Cannot persist result at location " + getLocation(), e);
        }
      }

      @Override
      public void setResultFormat(String name, String extension) {
        DataFormat<T> format = null; // TODO find based on extension (& type)
        setResultFormat(name, format);
      }
    };
  }
}
