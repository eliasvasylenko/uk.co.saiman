/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.msapex.
 *
 * uk.co.saiman.experiment.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.msapex.workspace;

import static java.lang.String.format;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.resource.PathLocation;
import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.definition.ExperimentDefinition;
import uk.co.saiman.experiment.format.JsonExperimentFormat;
import uk.co.saiman.experiment.graph.ExperimentId;
import uk.co.saiman.experiment.instruction.ExecutorService;
import uk.co.saiman.experiment.msapex.workspace.WorkspaceExperiment.Status;
import uk.co.saiman.experiment.msapex.workspace.event.AddExperimentEvent;
import uk.co.saiman.experiment.msapex.workspace.event.WorkspaceEvent;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.experiment.storage.StorageService;
import uk.co.saiman.log.Log;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

public class Workspace {
  static final String WORKSPACE = "workspace";
  static final String CONFIGURATION_PID = "uk.co.saiman.experiment.filesystem.workspace";

  private final Set<WorkspaceExperiment> experiments;

  private final PathLocation rootLocation;
  private final DataFormat<Experiment> experimentFormat;

  private final HotObservable<WorkspaceEvent> events;

  private final Log log;

  public Workspace(
      Path rootPath,
      ExecutorService conductorService,
      StorageService storageService,
      Log log) {
    this.experiments = new HashSet<>();

    this.rootLocation = new PathLocation(rootPath);
    this.experimentFormat = new JsonExperimentFormat(conductorService, storageService);

    this.events = new HotObservable<>();

    this.log = log;
  }

  public PathLocation getWorkspaceLocation() {
    return rootLocation;
  }

  public DataFormat<Experiment> getExperimentFormat() {
    return experimentFormat;
  }

  public Stream<WorkspaceExperiment> getWorkspaceExperiments() {
    return experiments.stream();
  }

  public Optional<WorkspaceExperiment> getWorkspaceExperiment(ExperimentId id) {
    return experiments.stream().filter(experiment -> experiment.id().equals(id)).findAny();
  }

  public Optional<Step> resolveStep(WorkspaceExperimentPath path) {
    // TODO Auto-generated method stub
    return null;
  }

  public Stream<Experiment> getExperiments() {
    return getWorkspaceExperiments().map(WorkspaceExperiment::experiment);
  }

  public Optional<Experiment> getExperiment(ExperimentId id) {
    return getWorkspaceExperiment(id).map(WorkspaceExperiment::experiment);
  }

  public boolean containsExperiment(Experiment experiment) {
    return getWorkspaceExperiment(experiment).isPresent();
  }

  public boolean containsStep(Step step) {
    return containsExperiment(step.getExperiment()) && !step.isDetached();
  }

  public Optional<WorkspaceExperimentPath> getPath(Step step) {
    return containsStep(step)
        ? Optional.of(WorkspaceExperimentPath.define(step.getExperiment().getId(), step.getPath()))
        : Optional.empty();
  }

  public Optional<WorkspaceExperiment> getWorkspaceExperiment(Experiment experiment) {
    return getWorkspaceExperiment(experiment.getId())
        .filter(e -> e.status() == Status.OPEN)
        .filter(e -> e.experiment() == experiment);
  }

  void nextEvent(WorkspaceEvent event) {
    events.next(event);
  }

  public Observable<WorkspaceEvent> events() {
    return events;
  }

  private WorkspaceExperiment addExperiment(
      ExperimentId name,
      Function<ExperimentId, WorkspaceExperiment> factory) {
    synchronized (experiments) {
      if (getWorkspaceExperiment(name).isPresent()) {
        throw new ExperimentException(
            format("Workspace already contains experiment named %s", name));
      }
      var experiment = factory.apply(name);

      experiments.add(experiment);
      nextEvent(new AddExperimentEvent(this, experiment));

      return experiment;
    }
  }

  public WorkspaceExperiment loadExperiment(ExperimentId name) {
    return addExperiment(name, n -> new WorkspaceExperiment(this, n));
  }

  public WorkspaceExperiment newExperiment(
      ExperimentId name,
      StorageConfiguration<?> storageConfiguration) {
    return addExperiment(
        name,
        n -> new WorkspaceExperiment(
            this,
            new Experiment(ExperimentDefinition.define(name), storageConfiguration)));
  }

  public WorkspaceExperiment addExperiment(Experiment experiment) {
    return addExperiment(experiment.getId(), n -> new WorkspaceExperiment(this, experiment));
  }

  public boolean removeExperiment(Experiment experiment) {
    var workspaceExperiment = getWorkspaceExperiment(experiment);
    workspaceExperiment.ifPresent(WorkspaceExperiment::remove);
    return workspaceExperiment.isPresent();
  }

  void removeExperiment(WorkspaceExperiment experiment) {
    synchronized (experiments) {
      experiments.remove(experiment);
    }
  }

  Log log() {
    return log;
  }
}
