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
import static uk.co.saiman.collection.StreamUtilities.upcastStream;
import static uk.co.saiman.text.properties.PropertyLoader.getDefaultProperties;

import java.io.IOException;
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

import uk.co.saiman.collection.StreamUtilities;
import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentRoot;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.experiment.WorkspaceEvent;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

/**
 * Reference implementation of {@link Workspace}.
 * 
 * @author Elias N Vasylenko
 */
public class XmlWorkspace implements Workspace {
  private final XmlWorkspaceFactory factory;
  private final Path dataRoot;

  private final Set<ExperimentType<?>> experimentTypes = new HashSet<>();

  private final ExperimentRoot experimentRootType;
  private final List<XmlExperiment> experiments = new ArrayList<>();

  private final ExperimentProperties text;

  private final Lock processingLock = new ReentrantLock();

  private final HotObservable<WorkspaceEvent> events = new HotObservable<>();

  /**
   * Try to create a new experiment workspace over the given root path
   * 
   * @param factory
   *          the factory which produces the workspace
   * @param workspaceRoot
   *          the path of the workspace data
   */
  public XmlWorkspace(XmlWorkspaceFactory factory, Path workspaceRoot) {
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
  public XmlWorkspace(XmlWorkspaceFactory factory, Path workspaceRoot, ExperimentProperties text) {
    this.factory = factory;
    this.dataRoot = workspaceRoot;
    this.text = text;

    this.experimentRootType = new ExperimentRootImpl(text);

    loadExperiments();
  }

  @Override
  public Observable<WorkspaceEvent> events() {
    return events;
  }

  private void loadExperiments() {
    PathMatcher filter = dataRoot.getFileSystem().getPathMatcher(
        "glob:**/*" + XmlExperiment.EXPERIMENT_EXTENSION);

    try (DirectoryStream<Path> stream = newDirectoryStream(
        dataRoot,
        file -> isRegularFile(file) && filter.matches(file))) {
      for (Path path : stream) {
        XmlExperiment.load(this, path);
      }
    } catch (IOException e) {
      getLog().log(Level.ERROR, e);
    }
  }

  Log getLog() {
    return factory.getLog();
  }

  ExperimentProperties getText() {
    return text;
  }

  protected Path getRootPath() {
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

  protected Stream<XmlExperiment> getExperimentsImpl() {
    return experiments.stream();
  }

  @Override
  public Experiment addExperiment(String name) {
    XmlExperiment experiment = addExperiment(name, new XmlPersistedState());
    experiment.save();
    return experiment;
  }

  protected XmlExperiment addExperiment(String name, XmlPersistedState persistedState) {
    if (!ExperimentConfiguration.isNameValid(name)) {
      throw new ExperimentException(text.exception().invalidExperimentName(name));
    }

    XmlExperiment experiment = new XmlExperiment(experimentRootType, name, this, persistedState);

    return experiment;
  }

  void addExperimentImpl(XmlExperiment experiment) {
    experiments.add(experiment);
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

  protected void process(XmlExperimentNode<?, ?> node) {
    if (processingLock.tryLock()) {
      try {
        processImpl(node);
      } finally {
        processingLock.unlock();
      }
    } else {
      throw new ExperimentException(
          text.exception().cannotProcessExperimentConcurrently(node.getExperiment()));
    }
  }

  private boolean processImpl(XmlExperimentNode<?, ?> node) {
    boolean success = StreamUtilities
        .reverse(node.getAncestorsImpl())
        .filter(XmlExperimentNode::executeImpl)
        .count() > 0;

    if (success) {
      processChildren(node);
    }

    return success;
  }

  private void processChildren(XmlExperimentNode<?, ?> node) {
    node.getChildrenImpl().filter(XmlExperimentNode::executeImpl).forEach(this::processChildren);
  }
}
