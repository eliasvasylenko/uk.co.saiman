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

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.collection.StreamUtilities.upcastStream;
import static uk.co.saiman.experiment.WorkspaceEventKind.ADD;
import static uk.co.saiman.experiment.WorkspaceEventKind.REMOVE;
import static uk.co.saiman.properties.PropertyLoader.getDefaultPropertyLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentRoot;
import uk.co.saiman.experiment.ResultStore;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.experiment.WorkspaceEvent;
import uk.co.saiman.experiment.WorkspaceEventKind;
import uk.co.saiman.experiment.WorkspaceEventState;
import uk.co.saiman.experiment.state.StateMap;
import uk.co.saiman.log.Log;
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

  private final ExperimentProperties text;

  private final Log log;

  private final HotObservable<WorkspaceEvent> pendingEvents = new HotObservable<>();
  private final HotObservable<WorkspaceEvent> completeEvents = new HotObservable<>();
  private final HotObservable<WorkspaceEvent> cancelledEvents = new HotObservable<>();

  public WorkspaceImpl() {
    this(Log.discardingLog());
  }

  public WorkspaceImpl(Log log) {
    this(getDefaultPropertyLoader(), log);
  }

  @Activate
  public WorkspaceImpl(@Reference PropertyLoader loader, @Reference Log log) {
    this.text = loader.getProperties(ExperimentProperties.class);
    this.log = log;
    this.experimentRootType = new ExperimentRootImpl(text);
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
    return fireEvents(REMOVE, experiment, () -> experiments.remove(experiment));
  }

  /*
   * Child experiment types
   */

  @Override
  public Experiment addExperiment(String id, ResultStore locationManager) {
    ExperimentImpl experiment = new ExperimentImpl(locationManager, id, StateMap.empty(), this);

    fireEvents(ADD, experiment, () -> experiments.add(experiment));

    return experiment;
  }

  /*
   * Events
   */

  /*
   * Fire an event. This allows multiple kinds of events to be fired together, as
   * they may represent an atomic action, in which case cancellation of either one
   * needs to also apply to the other.
   */
  protected synchronized <T> T fireEvents(
      WorkspaceEventKind kind,
      ExperimentNode<?, ?> node,
      Supplier<T> effect) {
    return fireEvents(singleton(kind), node, effect, false);
  }

  /*
   * Fire a forced event. A forced event may not be cancelled.
   */
  protected synchronized <T> T fireForcedEvents(
      WorkspaceEventKind kind,
      ExperimentNode<?, ?> node,
      Supplier<T> effect) {
    return fireEvents(singleton(kind), node, effect, true);
  }

  protected synchronized <T> T fireEvents(
      Collection<? extends WorkspaceEventKind> kinds,
      ExperimentNode<?, ?> node,
      Supplier<T> effect,
      boolean forced) {
    return fireEvents(
        node,
        effect,
        kinds.stream().map(kind -> new WorkspaceEventImpl(node, kind)).collect(toList()),
        forced);
  }

  protected synchronized <T> T fireEvents(
      ExperimentNode<?, ?> node,
      Supplier<T> effect,
      List<WorkspaceEventImpl> events,
      boolean forced) {
    if (forced) {
      events.forEach(WorkspaceEventImpl::complete);
    }
    events.forEach(pendingEvents::next);
    if (events.stream().map(WorkspaceEventImpl::complete).reduce(true, (a, b) -> a && b)) {
      T result = effect.get();
      events.forEach(completeEvents::next);
      return result;
    } else {
      events.forEach(WorkspaceEvent::cancel);
      events.forEach(cancelledEvents::next);
      throw new CancellationException();
    }
  }

  @Override
  public Observable<WorkspaceEvent> events(WorkspaceEventState state) {
    switch (state) {
    case PENDING:
      return pendingEvents;
    case COMPLETED:
      return completeEvents;
    case CANCELLED:
      return cancelledEvents;
    default:
      return Observable.empty();
    }
  }
}
