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
import uk.co.saiman.experiment.StorageConfiguration;
import uk.co.saiman.experiment.filesystem.JsonExperimentFormat;
import uk.co.saiman.experiment.path.ExperimentIndex;
import uk.co.saiman.experiment.service.ProcedureService;
import uk.co.saiman.experiment.service.StorageService;
import uk.co.saiman.msapex.experiment.workspace.WorkspaceExperiment.Status;
import uk.co.saiman.msapex.experiment.workspace.event.AddExperimentEvent;
import uk.co.saiman.msapex.experiment.workspace.event.WorkspaceEvent;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

public class Workspace implements ExperimentIndex {
  static final String WORKSPACE = "workspace";
  static final String CONFIGURATION_PID = "uk.co.saiman.experiment.filesystem.workspace";

  private final Set<WorkspaceExperiment> experiments;

  private final PathLocation rootLocation;
  private final DataFormat<Experiment> experimentFormat;

  private final HotObservable<WorkspaceEvent> events;

  public Workspace(
      Path rootPath,
      ProcedureService procedureService,
      StorageService storageService) {
    this.experiments = new HashSet<>();

    this.rootLocation = new PathLocation(rootPath);
    this.experimentFormat = new JsonExperimentFormat(procedureService, storageService);

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

  @Override
  public Stream<Experiment> getExperiments() {
    return getWorkspaceExperiments().map(WorkspaceExperiment::experiment);
  }

  @Override
  public Optional<Experiment> getExperiment(String id) {
    return getWorkspaceExperiment(id).map(WorkspaceExperiment::experiment);
  }

  @Override
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
