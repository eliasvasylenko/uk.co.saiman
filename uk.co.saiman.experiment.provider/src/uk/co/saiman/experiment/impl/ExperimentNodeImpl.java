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
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.experiment.ExperimentLifecycleState.CONFIGURATION;
import static uk.co.saiman.experiment.ExperimentLifecycleState.DETACHED;
import static uk.co.saiman.experiment.ExperimentLifecycleState.PROCESSING;
import static uk.co.saiman.experiment.ExperimentLifecycleState.WAITING;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.data.Data;
import uk.co.saiman.data.DataException;
import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.resource.Location;
import uk.co.saiman.experiment.ConfigurationContext;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentLifecycleState;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.ProcessingContext;
import uk.co.saiman.experiment.Result;
import uk.co.saiman.experiment.ResultStore.Storage;
import uk.co.saiman.experiment.impl.WorkspaceEventImpl.AddExperimentEventImpl;
import uk.co.saiman.experiment.impl.WorkspaceEventImpl.ExperimentLifecycleEventImpl;
import uk.co.saiman.experiment.impl.WorkspaceEventImpl.ExperimentStateEventImpl;
import uk.co.saiman.experiment.impl.WorkspaceEventImpl.MoveExperimentEventImpl;
import uk.co.saiman.experiment.impl.WorkspaceEventImpl.RemoveExperimentEventImpl;
import uk.co.saiman.experiment.impl.WorkspaceEventImpl.RenameExperimentEventImpl;
import uk.co.saiman.experiment.state.StateMap;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

public class ExperimentNodeImpl<S, T> implements ExperimentNode<S, T> {
  private String id;
  private ExperimentType<S, T> type;
  private StateMap persistedState;

  private final WorkspaceImpl workspace;
  private ExperimentNodeImpl<?, ?> parent;
  private final List<ExperimentNodeImpl<?, ?>> children = new ArrayList<>();

  private ExperimentLifecycleState lifecycleState;
  private final S state;
  private final ResultImpl<T> result = new ResultImpl<>(this);
  private Data<T> resultData;
  private boolean processedChildrenInline;
  private Storage resultStorage;

  /**
   * Load an existing root experiment.
   * 
   * @param workspace
   * @param type
   * @param id
   */
  ExperimentNodeImpl(
      String id,
      ExperimentType<S, T> type,
      StateMap persistedState,
      WorkspaceImpl workspace,
      ExperimentLifecycleState lifecycleState) {
    this.id = id;
    this.type = type;
    this.persistedState = persistedState;
    this.lifecycleState = lifecycleState;

    this.workspace = workspace;
    this.parent = null;
    this.state = createState(id);
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
    return id;
  }

  @Override
  public String getTypeId() {
    return type.getId();
  }

  protected Stream<ExperimentNodeImpl<?, ?>> getAncestorsImpl() {
    return getAncestors().map(a -> (ExperimentNodeImpl<?, ?>) a);
  }

  @Override
  public S getState() {
    return state;
  }

  @Override
  public StateMap getStateMap() {
    return persistedState;
  }

  protected ExperimentProperties getText() {
    return workspace.getText();
  }

  protected Log getLog() {
    return workspace.getLog();
  }

  @Override
  public WorkspaceImpl getWorkspace() {
    return workspace;
  }

  protected Stream<? extends ExperimentNode<?, ?>> getSiblings() {
    return getParent().map(ExperimentNode::getChildren).orElse(Stream.empty());
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

  @Override
  public ExperimentImpl getExperiment() {
    return (ExperimentImpl) findAncestor(getWorkspace().getExperimentRootType())
        .orElseThrow(
            () -> new ExperimentException(format("Experiment node %s is detached", getId())));
  }

  @Override
  public void remove() {
    if (parent == null || !parent.children.contains(this)) {
      ExperimentException e = new ExperimentException(
          format("Failed to remove experiment %s from parent, does not exist", this));
      getLog().log(Level.ERROR, e);
      throw e;
    }

    getWorkspace().fireEvents(() -> {
      setDisposed();
      removeImpl();
      return null;
    },
        new RemoveExperimentEventImpl(this, parent),
        new ExperimentLifecycleEventImpl(this, DETACHED, lifecycleState));
  }

  @Override
  public ExperimentLifecycleState getLifecycleState() {
    return lifecycleState;
  }

  protected void removeImpl() {
    clearResultImpl();
    if (parent != null) {
      parent.children.remove(this);
      parent = null;
    }
  }

  protected void setDisposed() {
    lifecycleState = DETACHED;
    for (ExperimentNodeImpl<?, ?> child : children) {
      child.setDisposed();
    }
  }

  protected void assertAttached() {
    if (lifecycleState == DETACHED) {
      throw new ExperimentException(format("Experiment %s is detached", getId()));
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Stream<ExperimentNode<?, ?>> getChildren() {
    return (Stream<ExperimentNode<?, ?>>) (Object) getChildrenImpl();
  }

  protected Stream<ExperimentNodeImpl<?, ?>> getChildrenImpl() {
    return children.stream();
  }

  @Override
  public Optional<ExperimentNode<?, ?>> getChild(String id) {
    return getChildren().filter(c -> c.getId().equals(id)).findAny();
  }

  @Override
  public <U, V> ExperimentNode<U, V> addChild(
      int index,
      ExperimentType<U, V> childType,
      StateMap state) {
    assertAttached();
    ExperimentNodeImpl<U, V> node = new ExperimentNodeImpl<>(
        null,
        childType,
        state,
        workspace,
        DETACHED);

    workspace.fireEvents(() -> node.moveImpl(this, index), new AddExperimentEventImpl(node, this));

    return node;
  }

  @Override
  public void move(ExperimentNode<?, ?> parent, int index) {
    assertAttached();

    if (index < 0 || index > ((ExperimentNodeImpl<?, ?>) parent).children.size())
      throw new IndexOutOfBoundsException(Integer.toString(index));

    if (parent.getWorkspace() != getWorkspace()) {
      throw new ExperimentException(
          format("Cannot move experiment node %s between workspaces", getId()));
    }

    synchronized (this) {
      workspace
          .fireEvents(
              () -> moveImpl(parent, index),
              new MoveExperimentEventImpl(this, parent, this.parent));
    }
  }

  protected Void moveImpl(ExperimentNode<?, ?> parent, int index) {
    removeImpl();
    ((ExperimentNodeImpl<?, ?>) parent).children.add(index, this);
    this.parent = (ExperimentNodeImpl<?, ?>) parent;
    System.out.println(getId() + " <* " + parent);
    return null;
  }

  @Override
  public String toString() {
    return getId() + " : " + type.getName() + " [" + lifecycleState + "]";
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
      setLifecycleState(ExperimentLifecycleState.FAILURE, true);
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
    setLifecycleState(ExperimentLifecycleState.PROCESSING, false);

    if (parent != null) {
      if (!type.mayComeAfter(parent.type)) {
        throw new ExperimentException(
            format(
                "Experiment of type %s does not fulfil constraint on parent type %s",
                type,
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

    T result = getType().process(createProcessingContext(processChildren));

    if (getType().getResultType().getErasedType() != void.class) {
      this.result.setValue(result);
      if (resultData != null) {
        resultData.set(result);
      }
    }

    try {
      synchronized (this) {
        setLifecycleState(ExperimentLifecycleState.COMPLETION, false);
      }
    } catch (CancellationException e) {
      setLifecycleState(ExperimentLifecycleState.FAILURE, true);
      this.result.unsetValue();
      throw e;
    }

    if (!processedChildrenInline) {
      processChildren.run();
    }
  }

  private void setLifecycleState(ExperimentLifecycleState lifecycleState, boolean forced) {
    synchronized (this) {
      if (this.lifecycleState != lifecycleState) {
        workspace
            .fireEvents(
                () -> this.lifecycleState = lifecycleState,
                singleton(
                    new ExperimentLifecycleEventImpl(this, lifecycleState, this.lifecycleState)),
                forced);
      }
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

  private ConfigurationContext<S> createConfigurationContext() {
    return new ConfigurationContext<S>() {
      @Override
      public ExperimentNodeImpl<S, T> node() {
        return ExperimentNodeImpl.this;
      }

      @Override
      public StateMap state() {
        return persistedState;
      }

      @Override
      public void update(StateMap state) {
        ExperimentNodeImpl.this.setState(state);
      }

      @Override
      public void setId(String id) {
        ExperimentNodeImpl.this.setId(id);
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

  protected void setState(StateMap state) {
    if (Objects.equals(state, getState())) {
      return;

    } else if (!Objects.equals(ExperimentNodeImpl.this.state, state)) {
      workspace.fireEvents(() -> {
        persistedState = state;
        return null;
      }, new ExperimentStateEventImpl(this, state, persistedState));
    }
  }

  protected void setId(String id) {
    if (Objects.equals(id, getId())) {
      return;

    } else if (!ExperimentConfiguration.isNameValid(id)) {
      throw new ExperimentException(format("Invalid experiment name %s", id));

    } else if (getSiblings().anyMatch(s -> id.equals(s.getId()))) {
      throw new ExperimentException(format("Experiment name already exists %s", id));

    } else {
      workspace.fireEvents(() -> {
        try {
          if (resultStorage != null) {
            resultStorage = getExperiment()
                .getLocationManager()
                .relocateStorage(this, resultStorage);
          }
          this.id = id;
          return null;
        } catch (IOException e) {
          throw new ExperimentException(format("Failed to set experiment name %s", id));
        }
      }, new RenameExperimentEventImpl(this, id, this.id));
    }
  }

  @Override
  public Result<T> getResult() {
    return result;
  }

  @Override
  public synchronized void clearResult() {
    if (lifecycleState == PROCESSING || lifecycleState == WAITING) {
      throw invalidLifecycleStateException();
    }
    clearResultImpl();
    setLifecycleState(CONFIGURATION, false);
  }

  private ExperimentException invalidLifecycleStateException() {
    return new ExperimentException(
        format("Experiment %s is in invalid lifecycle state %s", this, lifecycleState));
  }

  protected void clearResultImpl() {
    result.unsetValue();
    if (resultStorage != null) {
      try {
        resultStorage.dispose();
        resultStorage = getExperiment().getLocationManager().locateStorage(this);
      } catch (IOException e) {
        throw new ExperimentException(format("Failed to clear experiment results %s", this), e);
      }
    }
  }
}
