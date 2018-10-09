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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import java.util.Optional;

import uk.co.saiman.experiment.state.StateMap;

public class Experiment extends ExperimentNode<ExperimentConfiguration, Void> {
  private final ResultStore store;

  private Workspace workspace;

  public Experiment(String id, ResultStore store) {
    this(id, StateMap.empty(), store);
  }

  public Experiment(String id, StateMap stateMap, ResultStore store) {
    this(FullProcedure.instance(), id, stateMap, store);
  }

  protected Experiment(FullProcedure procedure, String id, StateMap stateMap, ResultStore store) {
    super(procedure, id, stateMap);
    this.store = store;
  }

  @Override
  void fireEvent(ExperimentEvent event) {
    super.fireEvent(event);
    if (workspace != null) {
      workspace.fireEvent(event);
    }
  }

  public ResultStore getResultStore() {
    return store;
  }

  @Override
  public int getIndex() {
    return getWorkspace().get().getExperiments().collect(toList()).indexOf(this);
  }

  @Override
  public Optional<Workspace> getWorkspace() {
    return Optional.ofNullable(workspace);
  }

  void addExperimentTo(Workspace workspace) {
    lockExperiment().run(() -> {
      workspace.getExperiments().forEach(child -> {
        if (child.getId().equals(getId())) {
          throw new ExperimentException(
              format(
                  "Experiment node with id %s already attached in workspace %s",
                  getId(),
                  workspace));
        }
      });

      Workspace previousWorkspace = this.workspace;
      this.workspace = workspace;
      workspace.addExperimentImpl(this);

      if (previousWorkspace != null) {
        previousWorkspace.removeExperimentImpl(this);

        RemoveExperimentEvent removeEvent = new RemoveExperimentEvent(this, previousWorkspace);
        fireEventLocal(removeEvent);
        previousWorkspace.fireEvent(removeEvent);
      }
      fireEvent(new AddExperimentEvent(this, workspace));
    });
  }

  void removeExperimentFrom(Workspace workspace) {
    lockExperiment().run(() -> {
      if (this.workspace == workspace) {
        this.workspace = null;
      }

      RemoveExperimentEvent removeEvent = new RemoveExperimentEvent(this, workspace);
      workspace.fireEvent(removeEvent);
      fireEvent(removeEvent);
    });
  }
}
