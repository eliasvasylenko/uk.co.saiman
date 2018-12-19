package uk.co.saiman.msapex.experiment.workspace;

import static uk.co.saiman.msapex.experiment.workspace.event.WorkspaceEventKind.EXPERIMENT;

import uk.co.saiman.data.Data;
import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.event.ExperimentEventKind;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.msapex.experiment.workspace.event.CloseExperimentEvent;
import uk.co.saiman.msapex.experiment.workspace.event.OpenExperimentEvent;
import uk.co.saiman.msapex.experiment.workspace.event.RemoveExperimentEvent;
import uk.co.saiman.msapex.experiment.workspace.event.WorkspaceEvent;
import uk.co.saiman.msapex.experiment.workspace.event.WorkspaceExperimentEvent;
import uk.co.saiman.observable.Disposable;

public class WorkspaceExperiment {
  public enum Status {
    CLOSED, OPEN, REMOVED
  }

  private final Workspace workspace;
  private final Data<Experiment> data;
  private String name;

  private Experiment experiment;
  private Disposable eventsObservation;
  private Status status;

  public WorkspaceExperiment(
      Workspace workspace,
      String name,
      StorageConfiguration<?> storageConfiguration) {
    this.workspace = workspace;
    this.name = name;
    this.data = locateExperiment();

    if (data.getResource().exists()) {
      throw new ExperimentException(
          "Experiment file already exists at location " + data.getResource());
    }

    this.experiment = new Experiment(name, storageConfiguration, workspace.getSchedulingStrategy());
    this.eventsObservation = observe();
    this.status = Status.OPEN;

    this.data.set(experiment);
    this.data.save();
  }

  public WorkspaceExperiment(Workspace workspace, String name) {
    this.workspace = workspace;
    this.name = name;
    this.data = locateExperiment();

    this.status = Status.CLOSED;
  }

  private Data<Experiment> locateExperiment() {
    return Data.locate(workspace.getWorkspaceLocation(), name, workspace.getExperimentFormat());
  }

  @Override
  public String toString() {
    return name;
  }

  public void open() {
    switch (status) {
    case CLOSED:
      this.data.load();
      this.experiment = data.get();

      this.status = Status.OPEN;
      nextEvent(new OpenExperimentEvent(workspace, this));

      this.eventsObservation = observe();

      break;
    case REMOVED:
      throw new ExperimentException("Cannot load removed experiment %s" + name);
    default:
    }
  }

  public void close() {
    switch (status) {
    case OPEN:
      this.eventsObservation.cancel();
      this.eventsObservation = null;

      this.experiment.dispose();
      this.experiment = null;
      this.data.unset();

      this.status = Status.CLOSED;
      nextEvent(new CloseExperimentEvent(workspace, this));

      break;
    case REMOVED:
      throw new ExperimentException("Cannot unload removed experiment %s" + name);
    default:
    }
  }

  public void save() {
    switch (status) {
    case OPEN:
      data.relocate(workspace.getWorkspaceLocation(), name);
      data.set(experiment);
      data.makeDirty();
      data.save();
      break;
    case REMOVED:
      throw new ExperimentException("Cannot save removed experiment %s" + name);
    default:
    }
  }

  private Disposable observe() {
    return experiment
        .events()
        .map(event -> new WorkspaceExperimentEvent(workspace, this, event))
        .observe(this::nextEvent);
  }

  private void nextEvent(WorkspaceEvent event) {
    if (event.kind() == EXPERIMENT) {
      if (status == Status.OPEN) {
        var experimentEvent = ((WorkspaceExperimentEvent) event).experimentEvent();
        if (experimentEvent.kind() == ExperimentEventKind.RENAME) {
          name = experiment.getId();
        }

        save();
        workspace.nextEvent(event);
      }
    } else {
      workspace.nextEvent(event);
    }
  }

  public synchronized void remove() {
    close();
    this.status = Status.REMOVED;
    workspace.removeExperiment(this);
    nextEvent(new RemoveExperimentEvent(workspace, this));
  }

  public void rename(String name) {
    open();
    experiment.getVariables().setName(name);
  }

  public Status status() {
    return status;
  }

  public String name() {
    return name;
  }

  public synchronized Experiment experiment() {
    open();
    return experiment;
  }
}
