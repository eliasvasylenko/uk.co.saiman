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

import static uk.co.saiman.collection.StreamUtilities.upcastStream;
import static uk.co.saiman.experiment.WorkspaceEvent.workspaceEvent;
import static uk.co.saiman.experiment.WorkspaceEventKind.ADD;
import static uk.co.saiman.properties.PropertyLoader.getDefaultPropertyLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentRoot;
import uk.co.saiman.experiment.ResultStorage;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.experiment.WorkspaceEvent;
import uk.co.saiman.experiment.persistence.StateMap;
import uk.co.saiman.log.Log;
import uk.co.saiman.observable.Cancellation;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.properties.PropertyLoader;

/**
 * Reference implementation of {@link Workspace}.
 * 
 * @author Elias N Vasylenko
 */
@Component
public class WorkspaceImpl implements Workspace {
  private final ExperimentRoot experimentRootType;
  private final List<ExperimentImpl> experiments = new ArrayList<>();

  @Reference
  private PropertyLoader loader;
  private ExperimentProperties text;

  @Activate
  void activate() {
    text = loader.getProperties(ExperimentProperties.class);
  }

  @Reference
  private Log log;

  private final HotObservable<WorkspaceEvent> events = new HotObservable<>();

  /*
   * TODO Replace with constructor injection with R7
   */
  public WorkspaceImpl() {
    this.experimentRootType = new ExperimentRootImpl(text);
  }

  /**
   * Try to create a new experiment workspace
   */
  public WorkspaceImpl(Log log) {
    this(log, getDefaultPropertyLoader().getProperties(ExperimentProperties.class));
  }

  /**
   * Try to create a new experiment workspace
   * 
   * @param text
   *          a localized text accessor implementation
   */
  public WorkspaceImpl(Log log, ExperimentProperties text) {
    this.log = log;
    this.text = text;

    this.experimentRootType = new ExperimentRootImpl(text);
  }

  protected Log getLog() {
    return log;
  }

  protected ExperimentProperties getText() {
    return text;
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

  @Override
  public Experiment addExperiment(String id, ResultStorage locationManager) {
    ExperimentImpl experiment = new ExperimentImpl(locationManager, id, StateMap.empty(), this);

    Cancellation cancellation = new Cancellation();
    events.next(workspaceEvent(experiment, ADD, cancellation::cancel));
    cancellation.completeOrThrow();

    experiments.add(experiment);
    return experiment;
  }

  /*
   * Events
   */

  @Override
  public Observable<WorkspaceEvent> events() {
    return events;
  }
}
