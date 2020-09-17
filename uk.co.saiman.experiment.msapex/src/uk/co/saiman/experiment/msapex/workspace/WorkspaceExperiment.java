/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static uk.co.saiman.experiment.msapex.workspace.event.WorkspaceEventKind.EXPERIMENT;

import uk.co.saiman.data.Data;
import uk.co.saiman.data.resource.Resource;
import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.msapex.workspace.event.CloseExperimentEvent;
import uk.co.saiman.experiment.msapex.workspace.event.OpenExperimentEvent;
import uk.co.saiman.experiment.msapex.workspace.event.RemoveExperimentEvent;
import uk.co.saiman.experiment.msapex.workspace.event.WorkspaceEvent;
import uk.co.saiman.experiment.msapex.workspace.event.WorkspaceExperimentEvent;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.observable.Disposable;

public class WorkspaceExperiment {
  public enum Status {
    CLOSED, OPEN, REMOVED
  }

  private final Workspace workspace;
  private final Data<Experiment> data;
  private ExperimentId id;

  private Experiment experiment;
  private Disposable eventsObservation;
  private Status status;

  public WorkspaceExperiment(Workspace workspace, Experiment experiment) {
    this.workspace = workspace;
    this.id = experiment.getId();
    this.data = Data
        .locate(workspace.getWorkspaceLocation(), id.name(), workspace.getExperimentFormat());

    if (data.getResource().exists()) {
      throw new ExperimentException(
          "Experiment file already exists at location " + data.getResource());
    }

    this.experiment = experiment;
    this.eventsObservation = observe();
    this.status = Status.OPEN;

    this.data.set(experiment);
    this.data.save();
  }

  public WorkspaceExperiment(Workspace workspace, Resource resource) {
    if (!resource.hasExtension(workspace.getExperimentFormat().getExtension())) {
      throw new ExperimentException("Experiment file has wrong extension " + resource);
    }

    this.workspace = workspace;
    this.id = ExperimentId
        .fromName(
            resource
                .getName()
                .substring(
                    0,
                    resource.getName().length()
                        - workspace.getExperimentFormat().getExtension().length() - 1));
    this.data = Data.locate(resource, workspace.getExperimentFormat());

    this.status = Status.CLOSED;
  }

  @Override
  public String toString() {
    return id.name();
  }

  public synchronized Experiment open() {
    switch (status) {
    case CLOSED:
      this.data.load();
      this.experiment = data.get();
      this.status = Status.OPEN;

      nextEvent(new OpenExperimentEvent(workspace, this));

      this.eventsObservation = observe();

      break;
    case REMOVED:
      throw new ExperimentException("Cannot load removed experiment %s" + id);
    default:
    }
    return experiment;
  }

  public synchronized void close() {
    switch (status) {
    case OPEN:
      this.eventsObservation.cancel();
      this.eventsObservation = null;

      this.experiment.close();
      this.experiment = null;
      this.data.unset();

      this.status = Status.CLOSED;
      nextEvent(new CloseExperimentEvent(workspace, this));

      break;
    case REMOVED:
      throw new ExperimentException("Cannot unload removed experiment %s" + id);
    default:
    }
  }

  public synchronized void save() {
    switch (status) {
    case OPEN:
      data.relocate(workspace.getWorkspaceLocation(), id.name());
      data.set(experiment);
      data.makeDirty();
      data.save();
      break;
    case REMOVED:
      throw new ExperimentException("Cannot save removed experiment %s" + id);
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
        id = experiment.getId();

        try {
          save();
        } catch (Exception e) {
          workspace.log().log(Level.ERROR, "Problem saving experiment", e);
          throw e;
        }
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

  public void rename(ExperimentId name) {
    open();
    experiment.setId(name);
  }

  public Status status() {
    return status;
  }

  public ExperimentId id() {
    return id;
  }
}
