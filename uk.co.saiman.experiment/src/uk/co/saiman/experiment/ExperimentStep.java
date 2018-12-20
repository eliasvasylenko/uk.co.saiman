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
import static java.util.Collections.singleton;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static uk.co.saiman.experiment.ExperimentLifecycleState.DETACHED;
import static uk.co.saiman.experiment.ExperimentLifecycleState.PROCEEDING;
import static uk.co.saiman.reflection.token.TypedReference.typedObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.collection.StreamUtilities;
import uk.co.saiman.data.Data;
import uk.co.saiman.data.DataException;
import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.resource.Location;
import uk.co.saiman.experiment.event.AttachStepEvent;
import uk.co.saiman.experiment.event.DetachStepEvent;
import uk.co.saiman.experiment.event.ExperimentEvent;
import uk.co.saiman.experiment.event.ExperimentLifecycleEvent;
import uk.co.saiman.experiment.event.ExperimentVariablesEvent;
import uk.co.saiman.experiment.event.RenameStepEvent;
import uk.co.saiman.experiment.event.ReorderStepsEvent;
import uk.co.saiman.experiment.scheduling.Schedule;
import uk.co.saiman.experiment.state.StateMap;
import uk.co.saiman.experiment.storage.Storage;
import uk.co.saiman.reflection.token.TypeArgument;
import uk.co.saiman.reflection.token.TypeToken;
import uk.co.saiman.reflection.token.TypedReference;

/**
 * This class provides a common interface for manipulating, inspecting, and
 * reflecting over the constituent nodes of an experiment. Each node is
 * associated with an implementation of {@link Procedure}.
 * 
 * @author Elias N Vasylenko
 * @param <S> the type of the data describing the experiment configuration
 */
public class ExperimentStep<S> {
  private String id;
  private Procedure<S> procedure;
  private StateMap stateMap;
  private ExperimentLifecycleState lifecycleState;

  private ExperimentStep<?> parent;
  private final List<ExperimentStep<?>> children;

  private final S variables;
  private final Map<Condition, State> states;
  private final Map<Dependency<?>, Input<?>> inputs;
  private final Map<Observation<?>, Result<?>> results;
  private Storage resultStore;

  public ExperimentStep(Procedure<S> procedure) {
    this(procedure, null, StateMap.empty());
  }

  public ExperimentStep(Procedure<S> procedure, String id, StateMap stateMap) {
    this(procedure, id, stateMap, DETACHED);
  }

  protected ExperimentStep(
      Procedure<S> procedure,
      String id,
      StateMap stateMap,
      ExperimentLifecycleState lifecycleState) {
    this.id = id;
    this.procedure = procedure;
    this.stateMap = stateMap;
    this.lifecycleState = lifecycleState;

    this.parent = null;
    this.children = new ArrayList<>();

    this.states = procedure
        .conditions()
        .map(condition -> new State(this, condition))
        .collect(toMap(State::getCondition, identity()));

    this.inputs = procedure
        .dependencies()
        .map(dependency -> new Input<>(this, dependency))
        .collect(toMap(Input::getDependency, identity()));

    this.results = procedure
        .observations()
        .map(observation -> new Result<>(this, observation))
        .collect(toMap(Result::getObservation, identity()));

    this.variables = createVariables(id);
  }

  /**
   * @return The ID of the node, as configured via {@link ExperimentContext}. The
   *         ID should be unique amongst the children of a node's parent.
   */
  public String getId() {
    return id;
  }

  void setId(String id) {
    if (!ExperimentConfiguration.isNameValid(id)) {
      throw new ExperimentException(format("Invalid experiment name %s", id));
    }

    lockExperiment().update(lock -> {
      if (!Objects.equals(id, getId())) {
        String previousId;

        if (getParent()
            .map(p -> p.getChildren().anyMatch(s -> id.equals(s.getId())))
            .orElse(false)) {
          throw new ExperimentException(
              format("Experiment node with id %s already attached at node %s", id, parent));

        } else {
          try {
            if (resultStore != null) {
              resultStore = getExperiment()
                  .get()
                  .getStorageConfiguration()
                  .relocateStorage(this, resultStore);
            }
          } catch (IOException e) {
            throw new ExperimentException(format("Failed to set experiment name %s", id));
          }

          previousId = this.id;
          this.id = id;
        }

        queueEvents(new RenameStepEvent(this, previousId));
      }
    });
  }

  /**
   * @return the type of the experiment
   */
  public Procedure<S> getProcedure() {
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

  private ExperimentContext<S> createConfigurationContext() {
    return new ExperimentContext<S>() {
      @Override
      public ExperimentStep<S> node() {
        return ExperimentStep.this;
      }

      @Override
      public StateMap stateMap() {
        return stateMap;
      }

      @Override
      public void update(StateMap stateMap) {
        ExperimentStep.this.setStateMap(stateMap);
      }

      @Override
      public void setId(String id) {
        ExperimentStep.this.setId(id);
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

    lockExperiment().update(lock -> {
      if (!Objects.equals(stateMap, this.stateMap)) {
        StateMap previous = this.stateMap;
        this.stateMap = stateMap;

        queueEvents(new ExperimentVariablesEvent(this, previous));
      }
    });
  }

  public TypeToken<ExperimentStep<S>> getThisTypeToken() {
    return new TypeToken<ExperimentStep<S>>() {}
        .withTypeArguments(new TypeArgument<S>(getProcedure().getVariablesType()) {});
  }

  public TypedReference<ExperimentStep<S>> asTypedObject() {
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

  void setLifecycleState(ExperimentLifecycleState lifecycleState) {
    ExperimentLifecycleState previousLifecycleState = getLifecycleState();
    if (previousLifecycleState != lifecycleState) {
      this.lifecycleState = lifecycleState;
      queueEvents(new ExperimentLifecycleEvent(this, previousLifecycleState));
    }
  }

  void queueEvents(ExperimentEvent... events) {
    queueEvents(List.of(events));
  }

  void queueEvents(Collection<? extends ExperimentEvent> events) {
    getExperiment().ifPresent(e -> e.queueEvents(events));
  }

  ExperimentLocker lockExperiment() {
    return new ExperimentLocker(singleton(this));
  }

  static ExperimentLocker lockExperiments(ExperimentStep<?>... experimentNodes) {
    return lockExperiments(List.of(experimentNodes));
  }

  static ExperimentLocker lockExperiments(Collection<? extends ExperimentStep<?>> experimentNodes) {
    return new ExperimentLocker(experimentNodes);
  }

  /*
   * Experiment Hierarchy
   */

  /**
   * @return the parent part of this experiment, if present, otherwise an empty
   *         optional
   */
  public Optional<ExperimentStep<?>> getParent() {
    return Optional.ofNullable(parent);
  }

  /**
   * @return the node's index in its parent's list of children
   */
  public int getIndex() {
    return lockExperiment()
        .get(lock -> getParent().get().getChildren().collect(toList()).indexOf(this));
  }

  /**
   * Get all child experiment nodes, to be processed sequentially when this node
   * is processed.
   * 
   * @return An ordered list of all sequential child experiment parts
   */
  public Stream<ExperimentStep<?>> getChildren() {
    return lockExperiment().get(lock -> new ArrayList<>(children)).stream();
  }

  public Optional<ExperimentStep<?>> getChild(String id) {
    return lockExperiment().get(lock -> getChildren().filter(c -> c.getId().equals(id)).findAny());
  }

  public void attach(ExperimentStep<?> node) {
    lockExperiments(this, node).update(lock -> attachImpl(node, (int) getChildren().count()));
  }

  public void attach(ExperimentStep<?> node, int index) {
    lockExperiments(this, node).update(lock -> attachImpl(node, index));
  }

  private void attachImpl(ExperimentStep<?> node, int index) {
    if (index > children.size()) {
      throw new ExperimentException(
          format("Experiment node index %s is out of range at node %s", index, this));
    }

    if (node.parent == this) {
      int previousIndex = node.getIndex();

      children.remove(node);
      children.add(index, node);

      queueEvents(new ReorderStepsEvent(this, index, previousIndex));
    } else {
      if (getLifecycleState() == PROCEEDING) {
        throw new ExperimentException(
            format("Cannot detach experiment %s while in the %s state", getId(), PROCEEDING));
      }

      children.forEach(child -> {
        if (child.getId().equals(node.getId())) {
          throw new ExperimentException(
              format("Experiment node with id %s already attached at node %s", node.getId(), this));
        }
      });

      ExperimentStep<?> previousParent = node.parent;
      node.parent = this;
      children.add(node);

      if (previousParent != null) {
        previousParent.children.remove(this);

        setDetached();

        previousParent.queueEvents(new DetachStepEvent(node, previousParent));
      }
      queueEvents(new AttachStepEvent(node, this));
    }
  }

  public void detach(ExperimentStep<?> node) {
    lockExperiment().update(lock -> detachImpl(node));
  }

  private void detachImpl(ExperimentStep<?> node) {
    if (getLifecycleState() == PROCEEDING) {
      throw new ExperimentException(
          format("Cannot detach experiment %s while in the %s state", getId(), PROCEEDING));
    }

    if (node.parent == this) {
      children.remove(this);
      node.parent = null;
      node.setDetached();

      queueEvents(new DetachStepEvent(node, this));
    }
  }

  void setDetached() {
    clearResults();
    setLifecycleState(DETACHED);
  }

  /**
   * @return the root part of the experiment tree this part occurs in
   */
  public Optional<Experiment> getExperiment() {
    return lockExperiment().get(lock -> getExperimentImpl());
  }

  public Optional<Experiment> getExperimentImpl() {
    List<ExperimentStep<?>> ancestors = getAncestorsImpl();
    ExperimentStep<?> root = ancestors.get(ancestors.size() - 1);
    if (root instanceof Experiment) {
      return Optional.of((Experiment) root);
    } else {
      return Optional.empty();
    }
  }

  /**
   * @return a list of all ancestors, nearest first, inclusive of the node itself
   */
  public Stream<ExperimentStep<?>> getAncestors() {
    return lockExperiment().get(lock -> getAncestorsImpl()).stream();
  }

  private List<ExperimentStep<?>> getAncestorsImpl() {
    // collect and re-stream, as we need to collect the list whilst locked.
    return StreamUtilities
        .<ExperimentStep<?>>iterateOptional(this, ExperimentStep::getParent)
        .collect(toList());
  }

  /**
   * Get the nearest available ancestor node of the processing experiment node
   * which is of the given {@link Procedure experiment type}.
   * 
   * @param procedure the type of the ancestor we wish to inspect
   * @return the nearest ancestor of the given type, or an empty optional if no
   *         such ancestor exists
   */
  @SuppressWarnings("unchecked")
  public <U> Optional<ExperimentStep<U>> findAncestor(Procedure<U> procedure) {
    return getAncestors()
        .filter(a -> procedure.equals(a.getProcedure()))
        .findFirst()
        .map(a -> (ExperimentStep<U>) a);
  }

  /**
   * Get the nearest available ancestor node of the processing experiment node
   * which is of the given {@link Procedure experiment type}.
   * 
   * @param procedure the type of the ancestor we wish to inspect
   * @return the nearest ancestor of the given type, or an empty optional if no
   *         such ancestor exists
   */
  @SuppressWarnings("unchecked")
  public <U> Stream<ExperimentStep<U>> findAncestors(Procedure<U> procedure) {
    return getAncestors()
        .filter(a -> procedure.equals(a.getProcedure()))
        .map(a -> (ExperimentStep<U>) a);
  }

  /*
   * Experiment Processing
   */

  public void schedule() {
    lockExperiments().update(lock -> {
      // TODO
    });
  }

  public static void schedule(ExperimentStep<?>... steps) {
    schedule(List.of(steps));
  }

  public static void schedule(Collection<? extends ExperimentStep<?>> steps) {
    lockExperiments(steps).update(lock -> {
      if (lock.getExperiments().count() != 1) {
        // TODO
        throw new UnsupportedOperationException("Not yet implemented");
      } else {
        throw new ExperimentException(
            "Schedule must contain steps belonging to a single root experiment");
      }
    });
  }

  public void takeStep(Schedule schedule) {
    procedure.proceed(createProcedureContext(schedule));
  }

  /**
   * Get the result produced by an {@link Observation} which is made by this node.
   * 
   * @return the result produced by the given observation
   */
  @SuppressWarnings("unchecked")
  public <R> Result<R> getResult(Observation<R> observation) {
    Result<?> result = results.get(observation);
    if (result == null) {
      throw new ExperimentException(
          format("Experiment step %s does not make observation of %s", this, observation));
    }
    return (Result<R>) result;
  }

  public Stream<Result<?>> getResults() {
    return results.values().stream();
  }

  /**
   * Get the input which satisfies a {@link Dependency} which is required by this
   * node.
   * 
   * @return the input which satisfies the given dependency
   */
  @SuppressWarnings("unchecked")
  public <I> Input<I> getInput(Dependency<I> dependency) {
    Input<?> input = this.inputs.get(dependency);
    if (input == null) {
      throw new ExperimentException(
          format("Experiment step %s does not have dependency on %s", this, dependency));
    }
    return (Input<I>) input;
  }

  public Stream<Input<?>> getInputs() {
    return inputs.values().stream();
  }

  /**
   * Get the input which satisfies a {@link Dependency} which is required by this
   * node.
   * 
   * @return the input which satisfies the given dependency
   */
  public State getState(Condition condition) {
    State state = this.states.get(condition);
    if (state == null) {
      throw new ExperimentException(
          format("Experiment step %s does not provide condition %s", this, condition));
    }
    return state;
  }

  public Stream<State> getStates() {
    return states.values().stream();
  }

  /**
   * Clear all the results associated with this node. Take care, as this will also
   * delete any result data from disk.
   */
  public void clearResults() {
    results.values().forEach(Result::unsetValue);
  }

  private ProcedureContext<S> createProcedureContext(Schedule schedule) {

    /*
     * TODO once processed this must become inoperable, including the proxied Data
     * from setResult. Make sure this is synchronized!
     */
    return new ProcedureContext<S>() {
      @Override
      public ExperimentStep<S> node() {
        return ExperimentStep.this;
      }

      @Override
      public Location getLocation() {
        return resultStore.location();
      }

      @Override
      public <T> void setPartialResult(Observation<T> observation, Supplier<? extends T> value) {
        getResult(observation).setPartialValue(value);
      }

      @Override
      public <T> void setResultData(Observation<T> observation, Data<T> data) {
        getResult(observation).setData(data);
      }

      @Override
      public <T> void setResultFormat(
          Observation<T> observation,
          String name,
          DataFormat<T> format) {
        try {
          getResult(observation).setData(Data.locate(getLocation(), name, format));
        } catch (DataException e) {
          new ExperimentException("Cannot persist result at location " + getLocation(), e);
        }
      }

      @Override
      public void completeObservation(Observation<?> observation) {
        getResult(observation).complete();
      }

      @Override
      public void enterCondition(Condition condition) {
        getState(condition).enter();
      }

      @Override
      public void exitCondition(Condition condition) {
        schedule.awaitConditionDependents(ExperimentStep.this, condition);
        getState(condition).exit();
      }

      @Override
      public <U> Result<? extends U> acquireResult(Dependency<U> requirement) {
        var result = getInput(requirement)
            .getResult()
            .orElseThrow(
                () -> new ExperimentException(format("Dependency %s is unfulfilled", requirement)));
        schedule.awaitResult(ExperimentStep.this, result);
        return result;
      }

      @Override
      public <U extends AutoCloseable> U acquireResource(Resource<U> resource) {
        return schedule.awaitResource(ExperimentStep.this, resource);
      }

      @Override
      public Hold acquireHold(Condition condition) {
        schedule.awaitConditionDependency(ExperimentStep.this, condition);
        return getParent().get().getState(condition).takeHold();
      }
    };
  }
}
