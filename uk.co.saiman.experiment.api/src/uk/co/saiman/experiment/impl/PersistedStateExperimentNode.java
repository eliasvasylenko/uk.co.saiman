package uk.co.saiman.experiment.impl;

import static uk.co.saiman.experiment.ExperimentLifecycleState.PREPARATION;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;

import uk.co.saiman.data.Data;
import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.resource.Location;
import uk.co.saiman.experiment.ConfigurationContext;
import uk.co.saiman.experiment.ExecutionContext;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentLifecycleState;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.Result;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservableValue;

public class PersistedStateExperimentNode<S, T> implements ExperimentNode<S, T> {
  private static final String CONFIGURATION_ELEMENT = "configuration";
  private static final String NODE_ELEMENT = "node";
  private static final String TYPE_ATTRIBUTE = "type";
  private static final String ID_ATTRIBUTE = "id";

  private String id;
  private final ExperimentType<S, T> type;

  private final Workspace workspace;
  private final PersistedStateExperimentNode<?, ?> parent;
  private final List<ExperimentNode<?, ?>> children = new ArrayList<>();

  private final PersistedState persistedState;
  private final ObservableProperty<ExperimentLifecycleState> lifecycleState = ObservableProperty
      .over(PREPARATION);
  private final S state;
  private final PersistedStateResult<T> result = new PersistedStateResult<>(this);

  /**
   * Create a new root experiment.
   * 
   * @param workspace
   * @param type
   * @param id
   */
  protected PersistedStateExperimentNode(
      Workspace workspace,
      ExperimentType<S, T> type,
      String id) {
    this.type = type;
    this.workspace = workspace;
    this.parent = null;
    this.state = createState(id);
  }

  /**
   * Create a new child experiment.
   * 
   * @param parent
   * @param type
   * @param id
   */
  protected PersistedStateExperimentNode(
      PersistedStateExperimentNode<?, ?> parent,
      ExperimentType<S, T> type,
      String id) {
    this.type = type;
    this.workspace = parent.workspace;
    this.parent = parent;
    this.state = createState(id);

    validateChild();
  }

  /**
   * Load an existing root experiment.
   * 
   * @param workspace
   * @param type
   * @param id
   */
  protected PersistedStateExperimentNode(
      Workspace workspace,
      ExperimentType<S, T> type,
      Element root,
      XPath xPath) throws XPathExpressionException {
    this.type = type;
    this.workspace = workspace;
    this.parent = null;
    this.persistedState.load(root, xPath);
    this.state = createState(root.getAttribute(ID_ATTRIBUTE));

    loadChildNodes(root, xPath);
  }

  /**
   * Load an existing child experiment.
   * 
   * @param parent
   * @param type
   * @param id
   */
  protected PersistedStateExperimentNode(
      PersistedStateExperimentNode<?, ?> parent,
      ExperimentType<S, T> type,
      Element root,
      XPath xPath) throws XPathExpressionException {
    this.type = type;
    this.workspace = parent.workspace;
    this.parent = parent;
    this.persistedState.load(root, xPath);
    this.state = createState(root.getAttribute(ID_ATTRIBUTE));

    loadChildNodes(root, xPath);
    validateChild();
  }

  private void validateChild() {
    if (!type.mayComeAfter(parent)) {
      throw new ExperimentException(getText().exception().typeMayNotSucceed(type, this));
    }
    parent.getAncestorsImpl().filter(a -> !a.type.mayComeBefore(parent, type)).forEach(a -> {
      throw new ExperimentException(getText().exception().typeMayNotSucceed(type, a));
    });
    parent.children.add(this);
  }

  private S createState(String id) {
    setId(id);

    S state = type.createState(createConfigurationContext());

    if (getId() == null) {
      throw new ExperimentException(getText().exception().invalidExperimentName(null));
    }

    return state;
  }

  protected ExperimentProperties getText() {
    return workspace.getText();
  }

  @Override
  public String getId() {
    return id;
  }

  protected Stream<PersistedStateExperimentNode<?, ?>> getAncestorsImpl() {
    return getAncestors().map(a -> (PersistedStateExperimentNode<?, ?>) a);
  }

  @Override
  public S getState() {
    return state;
  }

  @Override
  public Workspace getWorkspace() {
    return workspace;
  }

  protected Path getDataPath() {
    return getWorkspace().getRootPath().resolve(getParentDataPath().resolve(id));
  }

  private Path getParentDataPath() {
    return getParentImpl().map(p -> p.getDataPath()).orElseGet(() -> getWorkspace().getRootPath());
  }

  protected XmlPersistedState persistedState() {
    return persistedState;
  }

  private Stream<? extends PersistedStateExperimentNode<?, ?>> getSiblings() {
    return getParentImpl()
        .map(p -> p.getChildrenImpl())
        .orElseGet(() -> upcastStream(getWorkspace().getExperimentsImpl()));
  }

  @Override
  public ExperimentType<S, T> getType() {
    return type;
  }

  protected Optional<PersistedStateExperimentNode<?, ?>> getParentImpl() {
    return Optional.ofNullable(parent);
  }

  @Override
  public Optional<ExperimentNode<?, ?>> getParent() {
    return Optional.ofNullable(parent);
  }

  protected XmlExperiment getRootImpl() {
    return (XmlExperiment) ExperimentNode.super.getExperiment();
  }

  @Override
  public void remove() {
    assertAvailable();
    setDisposed();

    if (parent != null && !parent.children.remove(this)) {
      ExperimentException e = new ExperimentException(
          getText().exception().experimentDoesNotExist(getId()));
      getWorkspace().getLog().log(Level.ERROR, e);
      throw e;

    }

    getRootImpl().save();
  }

  protected void setDisposed() {
    lifecycleState.set(ExperimentLifecycleState.DISPOSED);
    for (PersistedStateExperimentNode<?, ?> child : children) {
      child.setDisposed();
    }
  }

  protected void assertAvailable() {
    if (lifecycleState.get() == ExperimentLifecycleState.DISPOSED) {
      throw new ExperimentException(getText().exception().experimentIsDisposed(this));
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Stream<ExperimentNode<?, ?>> getChildren() {
    return (Stream<ExperimentNode<?, ?>>) (Object) getChildrenImpl();
  }

  protected Stream<PersistedStateExperimentNode<?, ?>> getChildrenImpl() {
    return children.stream();
  }

  @Override
  public Optional<ExperimentNode<?, ?>> getChild(String id) {
    return getChildren().filter(c -> c.getId().equals(id)).findAny();
  }

  @Override
  public Stream<ExperimentType<?, ?>> getAvailableChildExperimentTypes() {
    return workspace
        .getRegisteredExperimentTypes()
        .filter(type -> this.type.mayComeBefore(this, type) && type.mayComeAfter(this));
  }

  @Override
  public <U, V> ExperimentNode<U, V> addChild(ExperimentType<U, V> childType) {
    assertAvailable();
    ExperimentNode<U, V> child = new PersistedStateExperimentNode<>(this, childType, null);
    getRootImpl().save();
    return child;
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
    workspace.process(this);
  }

  protected boolean executeImpl() {
    lifecycleState.set(ExperimentLifecycleState.PROCESSING);

    try {
      validate();

      Files.createDirectories(getDataPath());

      getType().execute(createExecutionContext());

      lifecycleState.set(ExperimentLifecycleState.COMPLETION);
      return true;
    } catch (Exception e) {
      workspace
          .getLog()
          .log(
              Level.ERROR,
              new ExperimentException(getText().exception().failedExperimentExecution(this), e));
      lifecycleState.set(ExperimentLifecycleState.FAILURE);
      return false;
    }
  }

  private void validate() {
    if (id == null) {
      throw new ExperimentException(getText().exception().invalidExperimentName(null));
    }
  }

  private ExecutionContext<S, T> createExecutionContext() {
    return new ExecutionContext<S, T>() {
      @Override
      public ExperimentNode<S, T> node() {
        return PersistedStateExperimentNode.this;
      }

      @Override
      public void executeChildren() {
        // TODO Auto-generated method stub

      }

      @Override
      public Location getLocation() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public Data<T> setResult(Data<? extends T> data) {
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
      public PersistedStateExperimentNode<S, T> node() {
        return PersistedStateExperimentNode.this;
      }

      @Override
      public PersistedState persistedState() {
        return persistedState.forMap(CONFIGURATION_ELEMENT);
      }

      @Override
      public void setId(String id) {
        PersistedStateExperimentNode.this.setId(id);
      }

      @Override
      public String getId() {
        if (id == null)
          throw new NullPointerException();
        return id;
      }

      @Override
      public String getId(Supplier<String> defaultId) {
        if (id == null)
          setId(defaultId.get());
        return id;
      }
    };
  }

  protected void setId(String id) {
    if (Objects.equals(id, this.id)) {
      return;

    } else if (!ExperimentConfiguration.isNameValid(id)) {
      throw new ExperimentException(getText().exception().invalidExperimentName(id));

    } else if (getSiblings().anyMatch(s -> id.equals(s.getId()))) {
      throw new ExperimentException(getText().exception().duplicateExperimentName(id));

    } else {
      Path newLocation = getParentDataPath().resolve(id);

      if (this.id != null) {
        if (Files.exists(newLocation)) {
          throw new ExperimentException(getText().exception().dataAlreadyExists(newLocation));
        }

        Path oldLocation = getParentDataPath().resolve(this.id);
        try {
          Files.move(oldLocation, newLocation);
        } catch (IOException e) {
          throw new ExperimentException(getText().exception().cannotMove(oldLocation, newLocation));
        }
      } else {
        try {
          Files.createDirectories(newLocation);
        } catch (IOException e) {
          throw new ExperimentException(getText().exception().cannotCreate(newLocation), e);
        }
      }

      this.id = id;
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
