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
 * This file is part of uk.co.saiman.maldi.stage.
 *
 * uk.co.saiman.maldi.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.stage.msapex;

import static org.eclipse.e4.ui.services.IServiceConstants.ACTIVE_SELECTION;
import static uk.co.saiman.experiment.event.ExperimentEventKind.CHANGE_VARIABLE;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;

import javafx.scene.layout.Pane;
import uk.co.saiman.eclipse.adapter.AdaptNamed;
import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.sample.XYStageExecutor;
import uk.co.saiman.maldi.stage.SamplePlateStage;
import uk.co.saiman.observable.Disposable;

public class MaldiStageDeviceModel {
  private final SamplePlateStage stage;
  private Disposable stepObserver;

  @Inject
  public MaldiStageDeviceModel(Pane pane, SamplePlateStage stage) {
    this.stage = stage;
    pane.getChildren().add(new MaldiStageDiagram(stage));
  }

  @Optional
  @Inject
  public void setStep(@AdaptNamed(ACTIVE_SELECTION) Step step) {
    System.out.println(" # step selected 2 " + step);
    if (stepObserver != null) {
      stepObserver.cancel();
    }

    /*
     * TODO So, here we want to be able to interact with the selected experiment
     * step. In particular, to display and modify the position of the step
     * configuration. But! Just because a step has the XYStageExecutor.LOCATION
     * variable on it doesn't actually mean it's a stage executor, and doesn't mean
     * it manages the same stage instance.
     * 
     * We don't want to tightly couple the device UI to a particular executor. So we
     * need to invert control.
     * 
     * TODO We could have a SampleDevicePanel service so we can fetch an instance
     * for a given device, then we need to add API to SampleDevicePanel to
     * facilitate this interaction so it can be done from the Executor side.
     * 
     * TODO one option is to just link the selected step to the requested position
     * of the device directly, this way the stage UI can interact with the step via
     * the requestedPosition as an intermediary.
     * 
     * TODO the main problem with the above is that it doesn't account for e.g.
     * setting position indirectly according to well ID and well offset. Hash out
     * the API for that before thinking further on this...
     * 
     * 
     * TODO we also want to set the UI to the currently executing plate, and
     * possibly allow manual raster control during e.g. spectrum acquisition. Maybe
     * we do need the UI to have a dependency on the executor!!
     * 
     * 
     * 
     * 
     * 
     * TODO have a drop down list with all available "modes"
     * 
     * Currently executing experiment (possibly allow other clients who have
     * acquired control to register UI customization?):
     * 
     * - lock UI plate to that of the running sample plate executor.
     * 
     * - highlight submitted sample areas, and label with ids.
     * 
     * - manual stage operation is forbidden
     * 
     * Currently selected PLATE in experiment UI:
     * 
     * - set UI plate to that of the selected step.
     * 
     * - modifications to UI plate update selected step.
     * 
     * - highlight declared sample areas, and label with ids.
     * 
     * Currently selected AREA in experiment UI:
     * 
     * - selecting stage location updates selected
     * 
     * Experiment is not executing, no related step is selected:
     * 
     */

    stepObserver = step
        .getExperiment()
        .events()
        .filter(e -> e.kind() == CHANGE_VARIABLE)
        .observe(e -> {
          updateStepLocation(step);
        });
    updateStepLocation(step);
  }

  private void updateStepLocation(Step step) {
    var location = step.getVariable(XYStageExecutor.LOCATION);
    System.out.println(" #loc " + location);
  }
}
