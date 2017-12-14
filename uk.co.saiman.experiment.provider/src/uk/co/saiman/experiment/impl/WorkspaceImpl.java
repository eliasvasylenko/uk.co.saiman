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

import static uk.co.saiman.collection.StreamUtilities.upcastStream;
import static uk.co.saiman.text.properties.PropertyLoader.getDefaultProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import uk.co.saiman.collection.StreamUtilities;
import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentRoot;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

/**
 * Reference implementation of {@link Workspace}.
 * 
 * @author Elias N Vasylenko
 */
public class WorkspaceImpl implements Workspace {
  private final ExperimentLocationManager locationManager;
  private final ExperimentPersistenceManager persistenceManager;

  private final ExperimentRoot experimentRootType;
  private final List<ExperimentImpl> experiments = new ArrayList<>();

  private final ExperimentProperties text;
  private final Log log;

  private final Lock processingLock = new ReentrantLock();

  /**
   * Try to create a new experiment workspace over the given root path
   * 
   * @param factory
   *          the factory which produces the workspace
   * @param workspaceRoot
   *          the path of the workspace data
   */
  public WorkspaceImpl(
      ExperimentLocationManager locationManager,
      ExperimentPersistenceManager persistenceManager,
      Log log) {
    this(
        locationManager,
        persistenceManager,
        log,
        getDefaultProperties(ExperimentProperties.class));
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
  public WorkspaceImpl(
      ExperimentLocationManager locationManager,
      ExperimentPersistenceManager persistenceManager,
      Log log,
      ExperimentProperties text) {
    this.locationManager = locationManager;
    this.persistenceManager = persistenceManager;
    this.log = log;
    this.text = text;

    this.experimentRootType = new ExperimentRootImpl(text);

    loadExperiments();
  }

  protected void loadExperiments() {
    try {
      persistenceManager
          .getExperiments()
          .forEach(s -> experiments.add(new ExperimentImpl(this, s)));
    } catch (Exception e) {
      ExperimentException ee = new ExperimentException(
          getText().exception().cannotLoadExperiment(),
          e);
      getLog().log(Level.ERROR, ee);
      throw ee;
    }
  }

  protected Log getLog() {
    return log;
  }

  protected ExperimentProperties getText() {
    return text;
  }

  protected ExperimentLocationManager getLocationManager() {
    return locationManager;
  }

  protected ExperimentPersistenceManager getPersistenceManager() {
    return persistenceManager;
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

  @Override
  public Optional<Experiment> getExperiment(String id) {
    return getExperiments().filter(c -> c.getId().equals(id)).findAny();
  }

  protected Stream<ExperimentImpl> getExperimentsImpl() {
    return experiments.stream();
  }

  protected boolean removeExperiment(Experiment experiment) {
    return experiments.remove(experiment);
  }

  /*
   * Child experiment types
   */

  protected void process(ExperimentNodeImpl<?, ?> node) {
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

  private boolean processImpl(ExperimentNodeImpl<?, ?> node) {
    boolean success = StreamUtilities
        .reverse(node.getAncestorsImpl())
        .filter(ExperimentNodeImpl::executeImpl)
        .count() > 0;

    if (success) {
      processChildren(node);
    }

    return success;
  }

  private void processChildren(ExperimentNodeImpl<?, ?> node) {
    node.getChildrenImpl().filter(ExperimentNodeImpl::executeImpl).forEach(this::processChildren);
  }

  @Override
  public Experiment addExperiment(String name) {
    experiments.add(new ExperimentImpl(this, name));
    return null;
  }

  @Override
  public Stream<ExperimentType<?, ?>> getExperimentTypes() {
    return persistenceManager.getExperimentTypes();
  }
}
