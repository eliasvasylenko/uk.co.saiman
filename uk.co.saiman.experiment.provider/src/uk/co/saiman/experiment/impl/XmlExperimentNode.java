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

import static java.util.Objects.requireNonNull;
import static javax.xml.xpath.XPathConstants.NODESET;
import static uk.co.saiman.collection.StreamUtilities.upcastStream;

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
import org.w3c.dom.NodeList;

import uk.co.saiman.experiment.ConfigurationContext;
import uk.co.saiman.experiment.ExecutionContext;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentLifecycleState;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.PersistedState;
import uk.co.saiman.experiment.Result;
import uk.co.saiman.experiment.ResultManager;
import uk.co.saiman.experiment.ResultType;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservableValue;

/**
 * Reference implementation of {@link ExperimentNode}.
 * 
 * @author Elias N Vasylenko
 * @param <T>
 *          the type of the experiment type
 * @param <S>
 *          the type of the data describing the experiment configuration
 */
public class XmlExperimentNode<T extends ExperimentType<S>, S> implements ExperimentNode<T, S> {
  private static final String NODE_ELEMENT = "node";
  private static final String TYPE_ATTRIBUTE = "type";
  private static final String ID_ATTRIBUTE = "id";

  private final XmlWorkspace workspace;
  private final T type;
  private final XmlExperimentNode<?, ?> parent;

  private final List<XmlExperimentNode<?, ?>> children;

  private final ObservableProperty<ExperimentLifecycleState> lifecycleState;
  private final S state;

  private final XmlResult<?> result;

  private String id;
  private final XmlPersistedState persistedState;

  /**
   * Try to create a new experiment node of the given type, and with the given
   * parent.
   * 
   * @param type
   *          the type of the experiment
   * @param parent
   *          the parent of the experiment
   */
  protected XmlExperimentNode(
      T type,
      String id,
      XmlExperimentNode<?, ?> parent,
      XmlPersistedState persistedState) {
    this(type, id, parent.workspace, parent, persistedState);

    if (!type.mayComeAfter(parent)) {
      throw new ExperimentException(getText().exception().typeMayNotSucceed(type, this));
    }
    parent.getAncestorsImpl().filter(a -> !a.type.mayComeBefore(parent, type)).forEach(a -> {
      throw new ExperimentException(getText().exception().typeMayNotSucceed(type, a));
    });

    parent.children.add(this);
  }

  protected XmlExperimentNode(
      T type,
      String id,
      XmlWorkspace workspace,
      XmlPersistedState persistedState) {
    this(type, id, workspace, null, persistedState);
  }

  private XmlExperimentNode(
      T type,
      String id,
      XmlWorkspace workspace,
      XmlExperimentNode<?, ?> parent,
      XmlPersistedState persistedState) {
    this.type = type;
    this.workspace = workspace;
    this.parent = parent;
    this.children = new ArrayList<>();
    setID(id);

    result = getType().getResultType().map(r -> new XmlResult<>(this, r)).orElse(null);

    this.lifecycleState = ObservableProperty.over(ExperimentLifecycleState.PREPARATION);
    this.persistedState = persistedState;
    persistedState.observe(s -> getRootImpl().save());
    this.state = type.createState(createConfigurationContext());

    if (getId() == null) {
      throw new ExperimentException(getText().exception().invalidExperimentName(null));
    }
  }

  protected ExperimentProperties getText() {
    return workspace.getText();
  }

  @Override
  public String getId() {
    return id;
  }

  protected Stream<XmlExperimentNode<?, ?>> getAncestorsImpl() {
    return getAncestors().map(a -> (XmlExperimentNode<?, ?>) a);
  }

  @Override
  public S getState() {
    return state;
  }

  @Override
  public XmlWorkspace getWorkspace() {
    return workspace;
  }

  protected Path getDataPath() {
    return getWorkspace().getRootPath().resolve(getParentDataPath().resolve(id));
  }

  private Path getParentDataPath() {
    return getParentImpl().map(p -> p.getDataPath()).orElse(workspace.getRootPath());
  }

  protected XmlPersistedState persistedState() {
    return persistedState;
  }

  private Stream<? extends XmlExperimentNode<?, ?>> getSiblings() {
    return getParentImpl().map(p -> p.getChildrenImpl()).orElse(
        upcastStream(workspace.getExperimentsImpl()));
  }

  @Override
  public T getType() {
    return type;
  }

  protected Optional<XmlExperimentNode<?, ?>> getParentImpl() {
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
          getText().exception().experimentDoesNotExist(this));
      getWorkspace().getLog().log(Level.ERROR, e);
      throw e;

    }

    getRootImpl().save();
  }

  protected void setDisposed() {
    lifecycleState.set(ExperimentLifecycleState.DISPOSED);
    for (XmlExperimentNode<?, ?> child : children) {
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

  protected Stream<XmlExperimentNode<?, ?>> getChildrenImpl() {
    return children.stream();
  }

  @Override
  public Stream<ExperimentType<?>> getAvailableChildExperimentTypes() {
    return workspace.getRegisteredExperimentTypes().filter(
        type -> this.type.mayComeBefore(this, type) && type.mayComeAfter(this));
  }

  @Override
  public <U, E extends ExperimentType<U>> ExperimentNode<E, U> addChild(E childType) {
    ExperimentNode<E, U> child = loadChild(childType, null, new XmlPersistedState());
    getRootImpl().save();
    return child;
  }

  protected <U, E extends ExperimentType<U>> XmlExperimentNode<E, U> loadChild(
      E childType,
      String id,
      XmlPersistedState persistedState) {
    assertAvailable();
    return new XmlExperimentNode<>(childType, id, this, persistedState);
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
      workspace.getLog().log(
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

  private ResultManager createResultManager() {
    return new ResultManager() {
      @Override
      public <U> Result<U> get(ResultType<U> resultType) {
        return XmlExperimentNode.this.findResult(resultType);
      }

      @Override
      public <U> Result<U> set(ResultType<U> resultType, U resultData) {
        return XmlExperimentNode.this.setResult(resultType, requireNonNull(resultData));
      }

      @Override
      public <U> Result<U> unset(ResultType<U> resultType) {
        return XmlExperimentNode.this.setResult(resultType, null);
      }
    };
  }

  private ExecutionContext<S> createExecutionContext() {
    return new ExecutionContext<S>() {
      @Override
      public ResultManager results() {
        return createResultManager();
      }

      @Override
      public XmlExperimentNode<?, S> node() {
        return XmlExperimentNode.this;
      }
    };
  }

  private ConfigurationContext<S> createConfigurationContext() {
    return new ConfigurationContext<S>() {
      @Override
      public XmlExperimentNode<?, S> node() {
        return XmlExperimentNode.this;
      }

      @Override
      public ResultManager results() {
        return createResultManager();
      }

      @Override
      public PersistedState persistedState() {
        return XmlExperimentNode.this.persistedState();
      }

      @Override
      public void setId(String id) {
        XmlExperimentNode.this.setID(id);
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

  protected void setID(String id) {
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
  public Optional<Result<?>> getResult() {
    return Optional.ofNullable(result);
  }

  @Override
  public void clearResult() {
    result.setProblem(new NullPointerException());
  }

  @SuppressWarnings("unchecked")
  @Override
  public <U> Optional<Result<U>> findResult(ResultType<U> resultType) {
    return (XmlResult<U>) results.get(resultType);
  }

  protected void saveNode(Element element) {
    element.setAttribute(TYPE_ATTRIBUTE, getType().getId());
    element.setAttribute(ID_ATTRIBUTE, getId());

    persistedState().save(element);

    getChildrenImpl().forEach(
        child -> child.saveNode(
            (Element) element.appendChild(element.getOwnerDocument().createElement(NODE_ELEMENT))));
  }

  protected void loadChildNodes(Element parentElement, XPath xPath)
      throws XPathExpressionException {
    NodeList nodes = (NodeList) xPath.evaluate(NODE_ELEMENT, parentElement, NODESET);
    for (int i = 0; i < nodes.getLength(); i++) {
      loadChildNode((Element) nodes.item(i), xPath);
    }
  }

  private void loadChildNode(Element element, XPath xPath) throws XPathExpressionException {
    String experimentID = element.getAttribute(ID_ATTRIBUTE);
    String experimentTypeID = element.getAttribute(TYPE_ATTRIBUTE);

    ExperimentType<?> experimentType = getAvailableChildExperimentTypes()
        .filter(e -> e.getId().equals(experimentTypeID))
        .findAny()
        .orElseGet(() -> new MissingExperimentTypeImpl(getText(), experimentTypeID));

    XmlExperimentNode<?, ?> node = loadChild(
        experimentType,
        experimentID,
        XmlPersistedState.load(element, xPath));

    node.loadChildNodes(element, xPath);
  }
}
