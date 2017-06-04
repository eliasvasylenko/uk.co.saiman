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

import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.newDirectoryStream;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static javax.xml.xpath.XPathConstants.NODESET;
import static uk.co.strangeskies.collection.stream.StreamUtilities.upcastStream;
import static uk.co.strangeskies.text.properties.PropertyLoader.getDefaultProperties;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentRoot;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.ExperimentWorkspace;
import uk.co.strangeskies.collection.stream.StreamUtilities;
import uk.co.strangeskies.log.Log;

/**
 * Reference implementation of {@link ExperimentWorkspace}.
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentWorkspaceImpl implements ExperimentWorkspace {
  private static final String EXPERIMENT_EXTENSION = ".exml";

  private static final String EXPERIMENT_ELEMENT = "experiment";
  private static final String NODE_ELEMENT = "node";
  private static final String TYPE_ATTRIBUTE = "type";
  private static final String ID_ATTRIBUTE = "id";

  private final ExperimentWorkspaceFactoryImpl factory;
  private final Path dataRoot;

  private final Set<ExperimentType<?>> experimentTypes = new HashSet<>();

  private final ExperimentRoot experimentRootType = new ExperimentRootImpl(this);
  private final List<ExperimentImpl> experiments = new ArrayList<>();

  private final ExperimentProperties text;

  private final Lock processingLock = new ReentrantLock();

  /**
   * Try to create a new experiment workspace over the given root path
   * 
   * @param factory
   *          the factory which produces the workspace
   * @param workspaceRoot
   *          the path of the workspace data
   */
  public ExperimentWorkspaceImpl(ExperimentWorkspaceFactoryImpl factory, Path workspaceRoot) {
    this(factory, workspaceRoot, getDefaultProperties(ExperimentProperties.class));
  }

  /**
   * Try to create a new experiment workspace over the given root path
   * 
   * @param factory
   *          the factory which produces the workspace
   * @param workspaceRoot
   *          the path of the workspace data
   * @param text
   *          a localized text accessor implementation
   */
  public ExperimentWorkspaceImpl(
      ExperimentWorkspaceFactoryImpl factory,
      Path workspaceRoot,
      ExperimentProperties text) {
    this.factory = factory;
    this.dataRoot = workspaceRoot;
    this.text = text;

    loadExperiments();
  }

  private void loadExperiments() {
    PathMatcher filter = dataRoot.getFileSystem().getPathMatcher(
        "glob:**/*" + EXPERIMENT_EXTENSION);

    try (DirectoryStream<Path> stream = newDirectoryStream(
        dataRoot,
        file -> isRegularFile(file) && filter.matches(file))) {
      for (Path path : stream) {
        loadExperiment(path);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  Log getLog() {
    return factory.getLog();
  }

  ExperimentProperties getText() {
    return text;
  }

  @Override
  public Path getWorkspaceDataPath() {
    return dataRoot;
  }

  /*
   * Root experiment types
   */

  @Override
  public ExperimentRoot getExperimentRootType() {
    return experimentRootType;
  }

  @Override
  public Stream<Experiment> getExperiments() {
    return upcastStream(experiments.stream());
  }

  protected Stream<ExperimentImpl> getExperimentsImpl() {
    return experiments.stream();
  }

  @Override
  public Experiment addExperiment(String name) {
    return addExperiment(name, new PersistedStateImpl());
  }

  protected ExperimentImpl addExperiment(String name, PersistedStateImpl persistedState) {
    if (!ExperimentConfiguration.isNameValid(name)) {
      throw new ExperimentException(text.exception().invalidExperimentName(name));
    }

    ExperimentImpl experiment = new ExperimentImpl(experimentRootType, name, this, persistedState);
    experiments.add(experiment);
    return experiment;
  }

  protected boolean removeExperiment(Experiment experiment) {
    return experiments.remove(experiment);
  }

  /*
   * Child experiment types
   */

  @Override
  public boolean registerExperimentType(ExperimentType<?> experimentType) {
    return experimentTypes.add(experimentType);
  }

  @Override
  public boolean unregisterExperimentType(ExperimentType<?> experimentType) {
    return experimentTypes.remove(experimentType);
  }

  @Override
  public Stream<ExperimentType<?>> getRegisteredExperimentTypes() {
    return Stream.concat(factory.getRegisteredExperimentTypes(), experimentTypes.stream());
  }

  protected void process(ExperimentNodeImpl<?, ?> node) {
    if (processingLock.tryLock()) {
      try {
        processImpl(node);
      } finally {
        processingLock.unlock();
      }
    } else {
      throw new ExperimentException(
          text.exception().cannotProcessExperimentConcurrently(node.getRoot()));
    }
  }

  private boolean processImpl(ExperimentNodeImpl<?, ?> node) {
    boolean success = StreamUtilities
        .reverse(node.getAncestorsImpl())
        .filter(ExperimentNodeImpl::execute)
        .count() > 0;

    if (success) {
      processChildren(node);
    }

    return success;
  }

  private void processChildren(ExperimentNodeImpl<?, ?> node) {
    node.getChildrenImpl().filter(ExperimentNodeImpl::execute).forEach(this::processChildren);
  }

  protected Path saveExperiment(ExperimentImpl experiment) {
    Path location = getWorkspaceDataPath().resolve(experiment.getID() + EXPERIMENT_EXTENSION);

    try (OutputStream output = newOutputStream(location, CREATE, TRUNCATE_EXISTING, WRITE)) {
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      Element element = document.createElement(EXPERIMENT_ELEMENT);
      saveExperimentNode(element, experiment);
      document.appendChild(element);

      Transformer tr = TransformerFactory.newInstance().newTransformer();
      tr.setOutputProperty(OutputKeys.INDENT, "yes");
      tr.setOutputProperty(OutputKeys.METHOD, "xml");
      tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
      tr.transform(new DOMSource(document), new StreamResult(output));

      return location;
    } catch (Exception e) {
      throw new ExperimentException(text.exception().cannotPersistState(experiment), e);
    }
  }

  private void saveExperimentNode(Element element, ExperimentNodeImpl<?, ?> node) {
    element.setAttribute(TYPE_ATTRIBUTE, node.getType().getID());
    element.setAttribute(ID_ATTRIBUTE, node.getID());

    node.persistedState().save(element);

    node.getChildrenImpl().forEach(
        child -> saveExperimentNode(
            (Element) element.appendChild(element.getOwnerDocument().createElement(NODE_ELEMENT)),
            child));
  }

  protected ExperimentImpl loadExperiment(String name) {
    return loadExperiment(dataRoot.resolve(name + EXPERIMENT_EXTENSION));
  }

  private ExperimentImpl loadExperiment(Path path) {
    try (InputStream input = newInputStream(path, READ)) {
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
      XPath xPath = XPathFactory.newInstance().newXPath();

      Element root = document.getDocumentElement();
      ExperimentImpl experiment = addExperiment(
          root.getAttribute(ID_ATTRIBUTE),
          PersistedStateImpl.load(root, xPath));

      loadExperimentNodes(experiment, root, xPath);

      return experiment;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ExperimentException(text.exception().cannotLoadExperiment(path), e);
    }
  }

  private void loadExperimentNodes(
      ExperimentNodeImpl<?, ?> parentNode,
      Element parentElement,
      XPath xPath) throws XPathExpressionException {
    NodeList nodes = (NodeList) xPath.evaluate("/" + NODE_ELEMENT, parentElement, NODESET);
    for (int i = 0; i < nodes.getLength(); i++) {
      loadExperimentNode(parentNode, (Element) nodes.item(i), xPath);
    }
  }

  private void loadExperimentNode(ExperimentNodeImpl<?, ?> parentNode, Element element, XPath xPath)
      throws XPathExpressionException {
    String experimentID = element.getAttribute(ID_ATTRIBUTE);
    String experimentTypeID = element.getAttribute(TYPE_ATTRIBUTE);

    ExperimentType<?> experimentType = parentNode
        .getAvailableChildExperimentTypes()
        .filter(e -> e.getID().equals(experimentTypeID))
        .findAny()
        .orElseGet(() -> new MissingExperimentTypeImpl(this, experimentTypeID));

    ExperimentNodeImpl<?, ?> node = parentNode
        .loadChild(experimentType, experimentID, PersistedStateImpl.load(element, xPath));

    loadExperimentNodes(node, element, xPath);
  }
}
