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

import static javax.xml.xpath.XPathConstants.NODESET;
import static uk.co.saiman.collection.StreamUtilities.upcastStream;
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
import org.w3c.dom.NodeList;

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
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservableValue;

/**
 * Reference implementation of {@link ExperimentNode}.
 * 
 * @author Elias N Vasylenko
 * @param <S>
 *          the type of the data describing the experiment configuration
 * @param <R>
 *          the type of the data describing the experiment result
 */
public class XmlExperimentNode<S, R> implements ExperimentNode<S, R> {
  private static final String NODE_ELEMENT = "node";
  private static final String TYPE_ATTRIBUTE = "type";
  private static final String ID_ATTRIBUTE = "id";

  private String id;
  private final ExperimentType<S, R> type;

  private final XmlWorkspace workspace;
  private final XmlExperimentNode<?, ?> parent;
  private final List<XmlExperimentNode<?, ?>> children = new ArrayList<>();

  private final XmlPersistedState persistedState = new XmlPersistedState(
      () -> getRootImpl().save());
  private final ObservableProperty<ExperimentLifecycleState> lifecycleState = ObservableProperty
      .over(PREPARATION);
  private final S state;
  private final PersistedStateResult<R> result = new PersistedStateResult<>(this);

  /**
   * Create a new root experiment.
   * 
   * @param workspace
   * @param type
   * @param id
   */
  protected XmlExperimentNode(XmlWorkspace workspace, ExperimentType<S, R> type, String id) {
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
  protected XmlExperimentNode(
      XmlExperimentNode<?, ?> parent,
      ExperimentType<S, R> type,
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
  protected XmlExperimentNode(
      XmlWorkspace workspace,
      ExperimentType<S, R> type,
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
  protected XmlExperimentNode(
      XmlExperimentNode<?, ?> parent,
      ExperimentType<S, R> type,
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
    return getParentImpl().map(p -> p.getDataPath()).orElseGet(() -> getWorkspace().getRootPath());
  }

  protected XmlPersistedState persistedState() {
    return persistedState;
  }

  private Stream<? extends XmlExperimentNode<?, ?>> getSiblings() {
    return getParentImpl()
        .map(p -> p.getChildrenImpl())
        .orElseGet(() -> upcastStream(getWorkspace().getExperimentsImpl()));
  }

  @Override
  public ExperimentType<S, R> getType() {
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
          getText().exception().experimentDoesNotExist(getId()));
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
    ExperimentNode<U, V> child = new XmlExperimentNode<>(this, childType, null);
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

  private ExecutionContext<S, R> createExecutionContext() {
    return new ExecutionContext<S, R>() {
      @Override
      public ExperimentNode<S, R> node() {
        return XmlExperimentNode.this;
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
      public Data<R> setResult(Data<? extends R> data) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public void setResultFormat(String name, DataFormat<R> format) {
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
      public XmlExperimentNode<S, R> node() {
        return XmlExperimentNode.this;
      }

      @Override
      public PersistedState persistedState() {
        return XmlExperimentNode.this.persistedState();
      }

      @Override
      public void setId(String id) {
        XmlExperimentNode.this.setId(id);
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
  public Result<R> getResult() {
    return result;
  }

  @Override
  public void clearResult() {
    result.setProblem(new NullPointerException());
  }

  protected void saveNode(Element element) {
    element.setAttribute(TYPE_ATTRIBUTE, getType().getId());
    element.setAttribute(ID_ATTRIBUTE, getId());

    persistedState().save(element);

    getChildrenImpl()
        .forEach(
            child -> child
                .saveNode(
                    (Element) element
                        .appendChild(element.getOwnerDocument().createElement(NODE_ELEMENT))));
  }

  protected void loadChildNodes(Element parentElement, XPath xPath)
      throws XPathExpressionException {
    NodeList nodes = (NodeList) xPath.evaluate(NODE_ELEMENT, parentElement, NODESET);
    for (int i = 0; i < nodes.getLength(); i++) {
      loadChildNode((Element) nodes.item(i), xPath);
    }
  }

  private void loadChildNode(Element element, XPath xPath) throws XPathExpressionException {
    String experimentTypeID = element.getAttribute(TYPE_ATTRIBUTE);

    ExperimentType<?, ?> experimentType = getAvailableChildExperimentTypes()
        .filter(e -> e.getId().equals(experimentTypeID))
        .findAny()
        .orElseGet(() -> new MissingExperimentTypeImpl<>(getText(), experimentTypeID));

    new XmlExperimentNode<>(this, experimentType, element, xPath);
  }
}
