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
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.resource.PathLocation;
import uk.co.saiman.data.resource.Resource;
import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.definition.ExperimentDefinition;
import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.environment.service.LocalEnvironmentService;
import uk.co.saiman.experiment.executor.service.ExecutorService;
import uk.co.saiman.experiment.format.JsonExperimentFormat;
import uk.co.saiman.experiment.msapex.workspace.WorkspaceExperiment.Status;
import uk.co.saiman.experiment.msapex.workspace.event.AddExperimentEvent;
import uk.co.saiman.experiment.msapex.workspace.event.WorkspaceEvent;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.experiment.storage.service.StorageService;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

public class Workspace {
  static final String WORKSPACE = "workspace";
  static final String CONFIGURATION_PID = "uk.co.saiman.experiment.filesystem.workspace";

  private final Supplier<GlobalEnvironment> environment;
  private final LocalEnvironmentService localEnvironmentService;

  private final PathLocation rootLocation;
  private final JsonExperimentFormat experimentFormat;

  private final Set<WorkspaceExperiment> experiments;

  private final HotObservable<WorkspaceEvent> events;

  private final Log log;

  public Workspace(
      Path rootPath,
      ExecutorService conductorService,
      StorageService storageService,
      Supplier<GlobalEnvironment> environment,
      LocalEnvironmentService localEnvironmentService,
      Log log) {
    this.experiments = new HashSet<>();

    this.rootLocation = new PathLocation(rootPath);
    this.experimentFormat = new JsonExperimentFormat(
        conductorService,
        storageService,
        environment,
        localEnvironmentService,
        log);

    this.environment = environment;
    this.localEnvironmentService = localEnvironmentService;

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

  public Optional<Experiment> getExperiment(ExperimentId id) {
    return getWorkspaceExperiment(id).map(WorkspaceExperiment::open);
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
        .filter(e -> e.open() == experiment);
  }

  void nextEvent(WorkspaceEvent event) {
    events.next(event);
  }

  public Observable<WorkspaceEvent> events() {
    return events;
  }

  public void syncExperiments() {
    try {
      String extension = getExperimentFormat().getExtension();
      getWorkspaceLocation()
          .resources()
          .filter(r -> r.hasExtension(extension))
          .forEach(this::loadExperiment);

    } catch (Exception e) {
      ExperimentException ee = new ExperimentException("Problem synchronizing workspace", e);
      log.log(Level.ERROR, ee);
      throw ee;
    }
  }

  private WorkspaceExperiment addExperiment(WorkspaceExperiment experiment) {
    synchronized (experiments) {
      if (getWorkspaceExperiment(experiment.id()).isPresent()) {
        throw new ExperimentException(
            format("Workspace already contains experiment named %s", experiment.id()));
      }

      experiments.add(experiment);
      nextEvent(new AddExperimentEvent(this, experiment));

      return experiment;
    }
  }

  public WorkspaceExperiment loadExperiment(Resource resource) {
    return addExperiment(new WorkspaceExperiment(this, resource));
  }

  public WorkspaceExperiment newExperiment(
      ExperimentId name,
      StorageConfiguration<?> storageConfiguration) {
    return addExperiment(
        new Experiment(
            ExperimentDefinition.define(name),
            storageConfiguration,
            experimentFormat.getExecutorService(),
            environment,
            localEnvironmentService,
            log));
  }

  public WorkspaceExperiment addExperiment(Experiment experiment) {
    return addExperiment(new WorkspaceExperiment(this, experiment));
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
