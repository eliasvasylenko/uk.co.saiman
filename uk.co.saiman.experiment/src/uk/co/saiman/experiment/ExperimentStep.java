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
import static java.util.stream.Collectors.toMap;
import static uk.co.saiman.experiment.ExperimentLifecycleState.COMPLETE;
import static uk.co.saiman.experiment.ExperimentLifecycleState.DETACHED;
import static uk.co.saiman.experiment.ExperimentLifecycleState.PROCEEDING;
import static uk.co.saiman.experiment.ExperimentLifecycleState.WAITING;
import static uk.co.saiman.reflection.token.TypedReference.typedObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.data.Data;
import uk.co.saiman.data.DataException;
import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.resource.Location;
import uk.co.saiman.experiment.event.AttachStepEvent;
import uk.co.saiman.experiment.event.DisposeStepEvent;
import uk.co.saiman.experiment.event.ExperimentEvent;
import uk.co.saiman.experiment.event.ExperimentLifecycleEvent;
import uk.co.saiman.experiment.event.ExperimentVariablesEvent;
import uk.co.saiman.experiment.event.RenameStepEvent;
import uk.co.saiman.experiment.event.ReorderStepsEvent;
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

  private Experiment experiment;

  private final S variables;

  private Condition<?> requiredCondition;
  private final Map<Preparation<?>, Condition<?>> preparedConditions;

  private final Map<ResultRequirement<?>, Result<?>> requiredResults;
  private final Map<Observation<?>, Result<?>> observedResults;

  private Storage resultStore;

  public ExperimentStep(Procedure<S, ?> procedure) {
    this(procedure, null, StateMap.empty());
  }

  public ExperimentStep(Procedure<S, ?> procedure, String id, StateMap stateMap) {
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

    /*
     * TODO
     * 
     * Cut this nonsense out.
     * 
     * We don't need the requirement thing for external resources, it's just an
     * over-complication. We may not even need the condition resources to be this
     * complicated.
     */

    this.requiredCondition = null;
    this.requiredResults = new HashMap<>();
    if (procedure.requirement() instanceof ResultRequirement<?>) {
      requiredResults.put((ResultRequirement<?>) procedure.requirement(), null);
    }

    this.preparedConditions = procedure
        .preparations()
        .map(condition -> new Condition<>(this, condition))
        .collect(toMap(Condition::getPreparation, identity()));
    this.observedResults = procedure
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
    if (!Experiment.isNameValid(id)) {
      throw new ExperimentException(format("Invalid experiment name %s", id));
    }

    lockExperiment().update(lock -> {
      if (!Objects.equals(id, getId())) {
        String previousId;

        if (getContainer()
            .map(p -> p.getComponentSteps().anyMatch(s -> id.equals(s.getId())))
            .orElse(false)) {
          throw new ExperimentException(
              format(
                  "Experiment node with id %s already attached at node %s",
                  id,
                  getContainer().get()));

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

      @Override
      public <U> boolean setRequiredResult(
          ResultRequirement<U> requirement,
          Result<? extends U> result) {
        throw new UnsupportedOperationException();
      }

      @Override
      public <U> boolean addRequiredResult(
          ResultRequirement<U> requirement,
          Result<? extends U> result) {
        throw new UnsupportedOperationException();
      }

      @Override
      public <U> boolean removeRequiredResult(ResultRequirement<U> requirement, Result<?> result) {
        throw new UnsupportedOperationException();
      }

      @Override
      public <U> Stream<Result<? extends U>> clearRequiredResults(
          ResultRequirement<U> requirement) {
        throw new UnsupportedOperationException();
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
  public Optional<Resource> getDependency() {
    return getProcedure().requirement().resolveResources(this).findAny().map(Function.identity());
  }

  public Optional<ExperimentStep<?>> getContainer() {
    return getDependency().map(d -> d.getNode());
  }

  public Optional<Experiment> getExperiment() {
    return lockExperiment().get(lock -> Optional.ofNullable(experiment));
  }

  /**
   * Get all child experiment nodes, to be processed sequentially when this node
   * is processed.
   * 
   * @return An ordered list of all sequential child experiment parts
   */
  public Stream<ExperimentStep<?>> getComponentSteps() {
    return lockExperiment().get(lock -> new ArrayList<>(children)).stream();
  }

  public Optional<ExperimentStep<?>> getComponentStep(String id) {
    return lockExperiment()
        .get(lock -> getComponentSteps().filter(c -> c.getId().equals(id)).findAny());
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
      /*
       * TODO perform this check for transitive closure of experiment steps which are
       * RESULT-DEPENDENT on this step.
       */
      if (getLifecycleState() == PROCEEDING
          || getLifecycleState() == COMPLETE
          || getLifecycleState() == WAITING) {
        throw new ExperimentException(
            format(
                "Cannot detach experiment %s while in the %s state",
                getId(),
                getLifecycleState()));
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

        previousParent.queueEvents(new DisposeStepEvent(node, previousParent));
      }
      queueEvents(new AttachStepEvent(node, this));
    }
  }

  public void dispose() {
    lockExperiment().update(lock -> detachImpl());
  }

  private void detachImpl() {
    /*
     * TODO perform this check for transitive closure of experiment steps which are
     * RESULT-DEPENDENT on this step.
     */
    if (getLifecycleState() == PROCEEDING
        || getLifecycleState() == COMPLETE
        || getLifecycleState() == WAITING) {
      throw new ExperimentException(
          format(
              "Cannot detach experiment %s while in the %s state",
              getId(),
              getLifecycleState()));
    }

    if (node.parent == this) {
      children.remove(this);
      node.parent = null;
      node.setDetached();

      queueEvents(new DisposeStepEvent(node, this));
    }
  }

  void setDetached() {
    setLifecycleState(DETACHED);
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

  void takeStep() {
    procedure.proceed(createProcedureContext());
  }

  /**
   * Get the result produced by an {@link Observation} which is made by this node.
   * 
   * @return the result produced by the given observation
   */
  @Override
  @SuppressWarnings("unchecked")
  public <R> Result<R> getResult(Observation<R> observation) {
    Result<?> result = observedResults.get(observation);
    if (result == null) {
      throw new ExperimentException(
          format("Experiment step %s does not make observation of %s", this, observation));
    }
    return (Result<R>) result;
  }

  @Override
  public Stream<Result<?>> getResults() {
    return observedResults.values().stream();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R> Condition<R> getCondition(Preparation<R> preparation) {
    Condition<?> condition = preparedConditions.get(preparation);
    if (condition == null) {
      throw new ExperimentException(
          format("Experiment step %s does not make preparation of %s", this, condition));
    }
    return (Condition<R>) condition;
  }

  @Override
  public Stream<Condition<?>> getConditions() {
    return preparedConditions.values().stream();
  }

  /**
   * Get the input which satisfies a {@link ResultRequirement} which is required
   * by this node.
   * 
   * @return the input which satisfies the given dependency
   */
  @SuppressWarnings("unchecked")
  public <I> Result<I> getRequiredResult(ResultRequirement<I> requirement) {
    Result<?> input = this.requiredResults.get(requirement);
    if (input == null) {
      throw new ExperimentException(
          format("Experiment step %s does not have dependency on %s", this, requirement));
    }
    return (Result<I>) input;
  }

  public Stream<Result<?>> getRequiredResults() {
    return requiredResults.values().stream();
  }

  /**
   * Get the input which satisfies a {@link ResultRequirement} which is required
   * by this node.
   * 
   * @return the input which satisfies the given dependency
   */
  @SuppressWarnings("unchecked")
  public <I> Condition<I> getRequiredCondition(ConditionRequirement<I> requirement) {
    Condition<?> input = this.requiredCondition;
    if (input == null) {
      throw new ExperimentException(
          format("Experiment step %s does not have dependency on %s", this, requirement));
    }
    return (Condition<I>) input;
  }

  /**
   * Get the input which satisfies a {@link ResultRequirement} which is required
   * by this node.
   * 
   * @return the input which satisfies the given dependency
   */
  public Stream<Condition<?>> getRequiredConditions() {
    return Stream.ofNullable(requiredCondition);
  }

  /**
   * Clear all the results associated with this node. Take care, as this will also
   * delete any result data from disk.
   */
  public void clearResults() {
    observedResults.values().forEach(Result::unsetValue);
  }

  private ProcedureContext<S> createProcedureContext() {

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
      public <T> void prepareCondition(Preparation<T> preparation, T condition) {
        getCondition(preparation).enter();
        schedule.awaitConditionDependents(ExperimentStep.this, condition);
        getCondition(preparation).exit();
      }

      @Override
      public <U> Result<? extends U> acquireResult(ResultRequirement<U> requirement) {
        var result = getInput(requirement)
            .getResult()
            .orElseThrow(
                () -> new ExperimentException(format("Dependency %s is unfulfilled", requirement)));
        schedule.awaitResult(ExperimentStep.this, result);
        return result;
      }

      @Override
      public Hold acquireHold(Preparation condition) {
        schedule.awaitConditionDependency(ExperimentStep.this, condition);
        return getContainer().get().getState(condition).takeHold();
      }

      @Override
      public <U> U acquireCondition(ConditionRequirement<U> resource) {
        return schedule.awaitResource(ExperimentStep.this, resource);
      }

      @Override
      public <U> Stream<Result<? extends U>> acquireResults(ResultRequirement<U> requirement) {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }
}
