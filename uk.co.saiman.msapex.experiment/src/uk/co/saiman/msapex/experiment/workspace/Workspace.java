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
 * This file is part of uk.co.saiman.msapex.experiment.
 *
 * uk.co.saiman.msapex.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment.workspace;

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
import uk.co.saiman.experiment.format.JsonExperimentFormat;
import uk.co.saiman.experiment.procedure.ConductorService;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.experiment.storage.StorageService;
import uk.co.saiman.msapex.experiment.workspace.WorkspaceExperiment.Status;
import uk.co.saiman.msapex.experiment.workspace.event.AddExperimentEvent;
import uk.co.saiman.msapex.experiment.workspace.event.WorkspaceEvent;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

public class Workspace {
  static final String WORKSPACE = "workspace";
  static final String CONFIGURATION_PID = "uk.co.saiman.experiment.filesystem.workspace";

  private final Set<WorkspaceExperiment> experiments;

  private final PathLocation rootLocation;
  private final DataFormat<Experiment> experimentFormat;

  private final HotObservable<WorkspaceEvent> events;

  public Workspace(
      Path rootPath,
      ConductorService conductorService,
      StorageService storageService) {
    this.experiments = new HashSet<>();

    this.rootLocation = new PathLocation(rootPath);
    this.experimentFormat = new JsonExperimentFormat(conductorService, storageService);

    this.events = new HotObservable<>();
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

  public Optional<WorkspaceExperiment> getWorkspaceExperiment(String id) {
    return experiments.stream().filter(experiment -> experiment.name().equals(id)).findAny();
  }

  public Stream<Experiment> getExperiments() {
    return getWorkspaceExperiments().map(WorkspaceExperiment::experiment);
  }

  public Optional<Experiment> getExperiment(String id) {
    return getWorkspaceExperiment(id).map(WorkspaceExperiment::experiment);
  }

  public boolean containsExperiment(Experiment experiment) {
    return getWorkspaceExperiment(experiment).isPresent();
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

  public WorkspaceExperiment addExperiment(
      String name,
      Function<String, WorkspaceExperiment> factory) {
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

  public WorkspaceExperiment loadExperiment(String name) {
    return addExperiment(name, n -> new WorkspaceExperiment(this, n));
  }

  public Experiment newExperiment(String name, StorageConfiguration<?> storageConfiguration) {
    return addExperiment(name, n -> new WorkspaceExperiment(this, n, storageConfiguration))
        .experiment();
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
}
