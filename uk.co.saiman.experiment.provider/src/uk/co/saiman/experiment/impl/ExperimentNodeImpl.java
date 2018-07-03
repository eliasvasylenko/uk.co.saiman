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
 * This file is part of uk.co.saiman.experiment.provider.
 *
 * uk.co.saiman.experiment.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.impl;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.experiment.ExperimentLifecycleState.DISPOSED;
import static uk.co.saiman.experiment.ExperimentLifecycleState.PREPARATION;
import static uk.co.saiman.experiment.ExperimentNodeConstraint.ASSUME_ALL_FULFILLED;
import static uk.co.saiman.experiment.ExperimentNodeConstraint.UNFULFILLED;
import static uk.co.saiman.experiment.ExperimentNodeConstraint.VIOLATED;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.data.Data;
import uk.co.saiman.data.SimpleData;
import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.resource.Location;
import uk.co.saiman.experiment.ConfigurationContext;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentLifecycleState;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentNodeConstraint;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.ProcessingContext;
import uk.co.saiman.experiment.Result;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.observable.Invalidation;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservableValue;

public class ExperimentNodeImpl<S, T> implements ExperimentNode<S, T> {
  private final PersistedExperiment persistedExperiment;
  private final ExperimentType<S, T> type;

  private final ExperimentNodeImpl<?, ?> parent;
  private final Map<PersistedExperiment, ExperimentNodeImpl<?, ?>> children = new HashMap<>();

  private final ObservableProperty<ExperimentLifecycleState> lifecycleState = ObservableProperty
      .over(PREPARATION);
  private final S state;
  private final ResultImpl<T> result = new ResultImpl<>(this);
  private Data<T> resultData;
  private boolean processedChildrenInline;

  /**
   * Load an existing root experiment.
   * 
   * @param workspace
   * @param type
   * @param id
   */
  ExperimentNodeImpl(ExperimentType<S, T> type, PersistedExperiment persistedExperiment) {
    this.type = type;
    this.parent = null;
    this.persistedExperiment = persistedExperiment;
    this.state = createState(persistedExperiment.getId());
  }

  /**
   * Create a new root experiment.
   * 
   * @param workspace
   * @param type
   * @param id
   */
  ExperimentNodeImpl(ExperimentType<S, T> type, String id) {
    this.type = type;
    this.parent = null;
    try {
      this.persistedExperiment = getPersistenceManager().addExperiment(id, type.getId());
    } catch (Exception e) {
      ExperimentException ee = new ExperimentException(
          format("Failed to persist new experiment %s", parent.getId()),
          e);
      getLog().log(Level.ERROR, ee);
      throw ee;
    }
    this.state = createState(id);
  }

  /**
   * Load an existing child experiment.
   * 
   * @param parent
   * @param type
   * @param id
   */
  protected ExperimentNodeImpl(
      ExperimentNodeImpl<?, ?> parent,
      ExperimentType<S, T> type,
      PersistedExperiment persistedExperiment) {
    this.type = type;
    this.parent = parent;
    this.persistedExperiment = persistedExperiment;

    this.state = createState(persistedExperiment.getId());

    loadChildNodes();

    this.parent.children.put(persistedExperiment, this);
  }

  /**
   * Create a new child experiment.
   * 
   * @param parent
   * @param type
   * @param id
   */
  protected ExperimentNodeImpl(
      ExperimentNodeImpl<?, ?> parent,
      ExperimentType<S, T> type,
      int index,
      PersistedState initialState) {
    if (index < 0 || index > parent.children.size())
      throw new IndexOutOfBoundsException(index + " in " + parent.children.size());

    this.type = type;
    this.parent = parent;
    this.persistedExperiment = this.parent.addChildImpl(this, initialState, index);

    this.state = createState(null);
  }

  private PersistedExperiment addChildImpl(
      ExperimentNodeImpl<?, ?> child,
      PersistedState state,
      int index) {
    PersistedExperiment persistedExperiment;
    try {
      persistedExperiment = this.persistedExperiment
          .addChild(child.getId(), child.getType().getId(), state, index);
    } catch (Exception e) {
      ExperimentException ee = new ExperimentException(
          format("Failed to persist new child experiment %s", parent.getId()),
          e);
      getLog().log(Level.ERROR, ee);
      throw ee;
    }
    children.put(persistedExperiment, child);
    return persistedExperiment;
  }

  protected void loadChildNodes() {
    persistedExperiment.getChildren().forEach(node -> {
      try {
        ExperimentType<?, ?> type = getPersistenceManager()
            .getExperimentTypes()
            .filter(e -> e.getId().equals(node.getTypeId()))
            .findAny()
            .orElseGet(() -> new MissingExperimentTypeImpl<>(getText(), node.getTypeId()));

        new ExperimentNodeImpl<>(this, type, node);
      } catch (Exception e) {
        ExperimentException ee = new ExperimentException(
            format("Failed to load experiment %s of type %s", node.getId(), node.getTypeId()),
            e);
        getLog().log(Level.ERROR, ee);
        throw ee;
      }
    });
  }

  private static void validateChild(ExperimentNodeImpl<?, ?> parent, ExperimentType<?, ?> type) {
    boolean assumeAllFulfilled = false;
    ExperimentNode<?, ?> unfulfilled = null;

    ExperimentNodeConstraint parentConstraint = type.mayComeAfter(parent);
    if (parentConstraint == VIOLATED) {
      throw new ExperimentException(
          format("Experiment of type %s violates constraint on parent node %s", type, parent));
    } else if (parentConstraint == ASSUME_ALL_FULFILLED) {
      assumeAllFulfilled = true;
    } else if (parentConstraint == UNFULFILLED) {
      unfulfilled = parent;
    }

    for (ExperimentNodeImpl<?, ?> ancestor : parent.getAncestorsImpl().collect(toList())) {
      ExperimentNodeConstraint ancestorConstraint = ancestor.type.mayComeBefore(parent, type);
      if (ancestorConstraint == VIOLATED) {
        throw new ExperimentException(
            format(
                "Experiment of type %s violates constraint on ancestor node %s",
                type,
                ancestor));
      } else if (ancestorConstraint == ASSUME_ALL_FULFILLED) {
        assumeAllFulfilled = true;
      } else if (ancestorConstraint == UNFULFILLED) {
        unfulfilled = ancestor;
      }
    }

    if (!assumeAllFulfilled && unfulfilled != null) {
      throw new ExperimentException(
          format(
              "Experiment of type %s does not fulfil constraint on ancestor node %s",
              type,
              unfulfilled));
    }
  }

  private boolean isChildValid(ExperimentType<?, ?> type) {
    try {
      validateChild(this, type);
    } catch (ExperimentException e) {
      return false;
    }
    return true;
  }

  private S createState(String id) {
    if (id != null) {
      setId(id);
    }

    S state = type.createState(createConfigurationContext());

    if (getId() == null) {
      throw new ExperimentException("Cannot initialise experiment state with null id");
    }

    return state;
  }

  @Override
  public String getId() {
    return persistedExperiment != null ? persistedExperiment.getId() : null;
  }

  protected Stream<ExperimentNodeImpl<?, ?>> getAncestorsImpl() {
    return getAncestors().map(a -> (ExperimentNodeImpl<?, ?>) a);
  }

  @Override
  public S getState() {
    return state;
  }

  protected ExperimentProperties getText() {
    return parent.getText();
  }

  protected Log getLog() {
    return parent.getLog();
  }

  protected ExperimentLocationManager getLocationManager() {
    return parent.getLocationManager();
  }

  protected ExperimentPersistenceManager getPersistenceManager() {
    return parent.getPersistenceManager();
  }

  @Override
  public Workspace getWorkspace() {
    return getParentImpl().get().getWorkspace();
  }

  protected Stream<? extends ExperimentNode<?, ?>> getSiblings() {
    return getParent().get().getChildren();
  }

  @Override
  public ExperimentType<S, T> getType() {
    return type;
  }

  protected Optional<ExperimentNodeImpl<?, ?>> getParentImpl() {
    return Optional.ofNullable(parent);
  }

  @Override
  public Optional<ExperimentNode<?, ?>> getParent() {
    return Optional.ofNullable(parent);
  }

  protected ExperimentImpl getRootImpl() {
    return (ExperimentImpl) ExperimentNode.super.getExperiment();
  }

  protected PersistedExperiment getPersistedExperiment() {
    return persistedExperiment;
  }

  @Override
  public void remove() {
    assertAvailable();
    setDisposed();

    removeImpl();
  }

  protected void removeImpl() {
    try {
      parent.persistedExperiment.removeChild(getId(), getType().getId());
    } catch (IOException e) {
      ExperimentException ee = new ExperimentException(
          format("Failed to persist removal of experiment %s", getId()),
          e);
      getLog().log(Level.ERROR, ee);
      throw ee;
    }

    if (parent.children.remove(persistedExperiment) == null) {
      ExperimentException e = new ExperimentException(
          format("Failed to remove experiment %s from parent, does not exist"));
      getLog().log(Level.ERROR, e);
      throw e;
    }
  }

  protected void setDisposed() {
    lifecycleState.set(DISPOSED);
    for (ExperimentNodeImpl<?, ?> child : children.values()) {
      child.setDisposed();
    }
  }

  protected void assertAvailable() {
    if (lifecycleState.get() == DISPOSED) {
      throw new ExperimentException(format("Experiment %s is disposed", getId()));
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Stream<ExperimentNode<?, ?>> getChildren() {
    return (Stream<ExperimentNode<?, ?>>) (Object) getChildrenImpl();
  }

  protected Stream<ExperimentNodeImpl<?, ?>> getChildrenImpl() {
    return persistedExperiment.getChildren().map(children::get);
  }

  @Override
  public Optional<ExperimentNode<?, ?>> getChild(String id) {
    return getChildren().filter(c -> c.getId().equals(id)).findAny();
  }

  @Override
  public Stream<ExperimentType<?, ?>> getAvailableChildExperimentTypes() {
    return getWorkspace().getExperimentTypes().filter(this::isChildValid);
  }

  @Override
  public <U, V> ExperimentNode<U, V> addChild(ExperimentType<U, V> childType, int index) {
    assertAvailable();
    return new ExperimentNodeImpl<>(this, childType, index, null);
  }

  @Override
  public ExperimentNode<S, T> copy(ExperimentNode<?, ?> parent, int index) {
    assertAvailable();

    if (parent.getWorkspace() != getWorkspace()) {
      throw new ExperimentException(
          format("Cannot copy experiment node %s between workspaces", getId()));
    }

    return new ExperimentNodeImpl<S, T>(
        ((ExperimentNodeImpl<?, ?>) parent),
        type,
        index,
        persistedExperiment.getPersistedState().copy());
  }

  @Override
  public void move(ExperimentNode<?, ?> parent, int index) {
    assertAvailable();

    if (parent.getWorkspace() != getWorkspace()) {
      throw new ExperimentException(
          format("Cannot move experiment node %s between workspaces", getId()));
    }

    removeImpl();

    ((ExperimentNodeImpl<?, ?>) parent)
        .addChildImpl(this, persistedExperiment.getPersistedState(), index);
  }

  @Override
  public ObservableValue<ExperimentLifecycleState> lifecycleState() {
    return lifecycleState;
  }

  @Override
  public String toString() {
    return getId() + " : " + type.getName() + " [" + lifecycleState.get() + "]";
  }

  @Override
  public void process() {
    try {
      processAncestors(getAncestorsImpl().collect(toList()));
    } catch (Exception e) {
      getLog()
          .log(
              Level.ERROR,
              new ExperimentException(format("Failed to process experiment %s", getId()), e));
      lifecycleState.set(ExperimentLifecycleState.FAILURE);
    }
  }

  private void processAncestors(List<ExperimentNodeImpl<?, ?>> ancestors) {
    ExperimentNodeImpl<?, ?> node = ancestors.remove(ancestors.size() - 1);

    if (!ancestors.isEmpty()) {
      node.processImpl(() -> node.processAncestors(ancestors));
    } else {
      node.processImpl(node::processChildren);
    }
  }

  private void processChildren() {
    getChildrenImpl().forEach(n -> n.processImpl(n::processChildren));
  }

  private void processImpl(Runnable processChildren) {
    lifecycleState.set(ExperimentLifecycleState.PROCESSING);

    if (parent != null) {
      validateChild(parent, type);
    }

    result.unsetValue();
    if (resultData != null) {
      resultData.unset();
      resultData.save();
      resultData = null;
    }
    processedChildrenInline = false;

    T result = getType().process(createProcessingContext(processChildren));

    if (getType().getResultType().getErasedType() != void.class) {
      this.result.setValue(result);
      if (resultData != null) {
        resultData.set(result);
      }
    }

    lifecycleState.set(ExperimentLifecycleState.COMPLETION);

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
        return ExperimentNodeImpl.this;
      }

      @Override
      public void processChildren() {
        processedChildrenInline = true;
        processChildren.run();
      }

      @Override
      public Location getLocation() {
        try {
          return getLocationManager().getLocation(ExperimentNodeImpl.this);
        } catch (IOException e) {
          throw new ExperimentException(
              format("Failed to prepare location for experiment %s", getId()),
              e);
        }
      }

      @Override
      public void setPartialResult(T value) {
        setPartialResult(() -> value);
      }

      @Override
      public void setPartialResult(Invalidation<T> value) {
        result.setInvalidation(value);
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
        resultData = new SimpleData<>(getLocation(), name, format);
      }

      @Override
      public void setResultFormat(String name, String extension) {
        DataFormat<T> format = null; // TODO find based on extension (& type)
        setResultFormat(name, format);
      }
    };
  }

  private ConfigurationContext<S> createConfigurationContext() {
    return new ConfigurationContext<S>() {
      @Override
      public ExperimentNodeImpl<S, T> node() {
        return ExperimentNodeImpl.this;
      }

      @Override
      public PersistedState persistedState() {
        return persistedExperiment.getPersistedState();
      }

      @Override
      public void setId(String id) {
        ExperimentNodeImpl.this.setId(id);
      }

      @Override
      public String getId() {
        String id = persistedExperiment.getId();
        if (id == null)
          throw new NullPointerException();
        return id;
      }

      @Override
      public String getId(Supplier<String> defaultId) {
        String id = persistedExperiment.getId();
        if (id == null)
          setId(defaultId.get());
        return id;
      }
    };
  }

  protected void setId(String id) {
    if (Objects.equals(id, persistedExperiment.getId())) {
      return;

    } else if (!ExperimentConfiguration.isNameValid(id)) {
      throw new ExperimentException(format("Invalid experiment name %s", id));

    } else if (getSiblings().anyMatch(s -> id.equals(s.getId()))) {
      throw new ExperimentException(format("Experiment name already exists %s", id));

    } else {
      try {
        getLocationManager().updateLocation(this, id);
        persistedExperiment.setId(id);
      } catch (IOException e) {
        throw new ExperimentException(format("Failed to set experiment name %s", id));
      }
    }
  }

  @Override
  public Result<T> getResult() {
    return result;
  }

  @Override
  public void clearResult() {
    result.unsetValue();
  }
}
