package uk.co.saiman.experiment.impl;

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.experiment.ExperimentLifecycleState.DISPOSED;
import static uk.co.saiman.experiment.ExperimentLifecycleState.PREPARATION;
import static uk.co.saiman.experiment.ExperimentNodeConstraint.ASSUME_ALL_FULFILLED;
import static uk.co.saiman.experiment.ExperimentNodeConstraint.UNFULFILLED;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.data.Data;
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
import uk.co.saiman.experiment.Result;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservableValue;

public class ExperimentNodeImpl<S, T> implements ExperimentNode<S, T> {
  private final PersistedExperiment persistedExperiment;
  private final ExperimentType<S, T> type;

  private final ExperimentNodeImpl<?, ?> parent;
  private final List<ExperimentNodeImpl<?, ?>> children = new ArrayList<>();

  private final ObservableProperty<ExperimentLifecycleState> lifecycleState = ObservableProperty
      .over(PREPARATION);
  private final S state;
  private final ResultImpl<T> result = new ResultImpl<>(this);

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
          getText().exception().cannotCreateExperiment(parent),
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
    validateChild();
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
    if (index < 0 || index > children.size())
      throw new IndexOutOfBoundsException();

    this.type = type;
    this.parent = parent;

    try {
      this.persistedExperiment = parent.persistedExperiment
          .addChild(index, type.getId(), initialState);
    } catch (Exception e) {
      ExperimentException ee = new ExperimentException(
          getText().exception().cannotCreateExperiment(parent),
          e);
      getLog().log(Level.ERROR, ee);
      throw ee;
    }

    this.state = createState(null);

    validateChild();
  }

  protected void loadChildNodes() {
    try {
      persistedExperiment.getChildren().forEach(node -> {
        ExperimentType<?, ?> type = getPersistenceManager()
            .getExperimentTypes()
            .filter(e -> e.getId().equals(node.getTypeId()))
            .findAny()
            .orElseGet(() -> new MissingExperimentTypeImpl<>(getText(), node.getTypeId()));

        new ExperimentNodeImpl<>(this, type, node);
      });
    } catch (Exception e) {
      ExperimentException ee = new ExperimentException(
          getText().exception().cannotLoadExperiment(),
          e);
      getLog().log(Level.ERROR, ee);
      throw ee;
    }
  }

  private void validateChild() {
    validateChild(parent, type);
    parent.children.add(this);
  }

  private static void validateChild(ExperimentNodeImpl<?, ?> parent, ExperimentType<?, ?> type) {
    ExperimentNodeConstraint parentConstraint = type.mayComeAfter(parent);
    if (parentConstraint == ASSUME_ALL_FULFILLED)
      return;

    ExperimentNode<?, ?> unfulfilledChildConstraint = null;
    for (ExperimentNodeImpl<?, ?> ancestor : parent.getAncestorsImpl().collect(toList())) {

      ExperimentNodeConstraint childConstraint = ancestor.type.mayComeBefore(parent, type);
      if (childConstraint == ASSUME_ALL_FULFILLED)
        return;
      else if (childConstraint == UNFULFILLED)
        unfulfilledChildConstraint = ancestor;
    }

    if (parentConstraint == UNFULFILLED) {
      throw new ExperimentException(parent.getText().exception().typeMayNotSucceed(type, parent));

    } else if (unfulfilledChildConstraint != null) {
      throw new ExperimentException(
          parent.getText().exception().typeMayNotSucceed(type, unfulfilledChildConstraint));
    }
  }

  private boolean isChildValid(ExperimentType<?, ?> type) {
    try {
      validateChild(parent, type);
    } catch (ExperimentException e) {
      return false;
    }
    return true;
  }

  private S createState(String id) {
    setId(id);

    S state = type.createState(createConfigurationContext());

    if (getId() == null) {
      throw new ExperimentException(getText().exception().invalidExperimentName(null));
    }

    return state;
  }

  @Override
  public String getId() {
    return persistedExperiment.getId();
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

    if (parent != null) {
      if (!parent.children.remove(this)) {
        ExperimentException e = new ExperimentException(
            getText().exception().experimentDoesNotExist(getId()));
        getLog().log(Level.ERROR, e);
        throw e;
      }

      try {
        parent.persistedExperiment.removeChild(persistedExperiment);
      } catch (IOException e) {
        ExperimentException ee = new ExperimentException(
            getText().exception().cannotRemoveExperiment(this),
            e);
        getLog().log(Level.ERROR, ee);
        throw ee;
      }
    }
  }

  protected void setDisposed() {
    lifecycleState.set(DISPOSED);
    for (ExperimentNodeImpl<?, ?> child : children) {
      child.setDisposed();
    }
  }

  protected void assertAvailable() {
    if (lifecycleState.get() == DISPOSED) {
      throw new ExperimentException(getText().exception().experimentIsDisposed(this));
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
  public Stream<ExperimentType<?, ?>> getAvailableChildExperimentTypes() {
    return getWorkspace().getExperimentTypes().filter(this::isChildValid);
  }

  @Override
  public <U, V> ExperimentNode<U, V> addChild(ExperimentType<U, V> childType, int index) {
    assertAvailable();
    return new ExperimentNodeImpl<>(this, childType, index, null);
  }

  @Override
  public <U, V> ExperimentNode<U, V> addCopy(ExperimentNode<U, V> node, int index) {
    assertAvailable();

    if (node.getWorkspace() != getWorkspace())
      throw new ExperimentException(getText().exception().cannotCopyFromOutsideWorkspace());

    return new ExperimentNodeImpl<>(
        this,
        node.getType(),
        index,
        ((ExperimentNodeImpl<?, ?>) node).persistedExperiment.getPersistedState());
  }

  @Override
  public void moveToIndex(int index) {
    parent.move(getIndex(), index);
  }

  protected void move(int from, int to) {
    // TODO perform move operation!
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
  public void execute() {
    getRootImpl().execute();
  }

  protected boolean executeImpl() {
    lifecycleState.set(ExperimentLifecycleState.PROCESSING);

    try {
      validate();

      T result = getType().execute(createExecutionContext());

      /*
       * TODO set the result
       * 
       * TODO if
       */

      lifecycleState.set(ExperimentLifecycleState.COMPLETION);

      /*
       * TODO execute children if not already done
       */
      return true;
    } catch (Exception e) {
      getLog()
          .log(
              Level.ERROR,
              new ExperimentException(getText().exception().failedExperimentExecution(this), e));
      lifecycleState.set(ExperimentLifecycleState.FAILURE);
      return false;
    }
  }

  protected void processChildren() {
    getChildrenImpl()
        .filter(ExperimentNodeImpl::executeImpl)
        .forEach(ExperimentNodeImpl::processChildren);
  }

  private void validate() {
    if (persistedExperiment.getId() == null) {
      throw new ExperimentException(getText().exception().invalidExperimentName(null));
    }
  }

  private ExecutionContextImpl<S, T> createExecutionContext() {
    return new ExecutionContextImpl<S, T>() {
      @Override
      public ExperimentNode<S, T> node() {
        return ExperimentNodeImpl.this;
      }

      @Override
      public void executeChildren() {
        // TODO Auto-generated method stub

      }

      @Override
      public Location getLocation() {
        try {
          return getLocationManager().getLocation(ExperimentNodeImpl.this);
        } catch (IOException e) {
          throw new ExperimentException(
              getText().exception().cannotPrepareLocation(ExperimentNodeImpl.this),
              e);
        }
      }

      @Override
      public Data<T> setResult(Data<? extends T> data) {
        result.set(data.get());
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public void setResultFormat(String name, DataFormat<T> format) {
        // TODO Auto-generated method stub

      }

      @Override
      public void setResultFormat(String name, String extension) {
        // TODO Auto-generated method stub

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
      throw new ExperimentException(getText().exception().invalidExperimentName(id));

    } else if (getSiblings().anyMatch(s -> id.equals(s.getId()))) {
      throw new ExperimentException(getText().exception().duplicateExperimentName(id));

    } else {
      try {
        getLocationManager().updateLocation(this, id);
        persistedExperiment.setId(id);
      } catch (IOException e) {
        throw new ExperimentException(getText().exception().cannotRenameExperiment(this, id), e);
      }
    }
  }

  @Override
  public Result<T> getResult() {
    return result;
  }

  @Override
  public void clearResult() {
    result.setProblem(new NullPointerException());
  }
}
