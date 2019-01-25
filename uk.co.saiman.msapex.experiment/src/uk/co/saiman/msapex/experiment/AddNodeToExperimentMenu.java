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
package uk.co.saiman.msapex.experiment;

import static uk.co.saiman.experiment.Experiment.scheduledCondition;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.fx.core.di.Service;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import uk.co.saiman.eclipse.dialog.DialogUtilities;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.experiment.ExperimentStep;
import uk.co.saiman.experiment.Procedure;
import uk.co.saiman.experiment.service.ProcedureService;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.msapex.experiment.i18n.ExperimentProperties;
import uk.co.saiman.msapex.experiment.workspace.WorkspaceExperiment;

/**
 * Track acquisition devices available through OSGi services and select which
 * device to display in the acquisition part.
 * 
 * @author Elias N Vasylenko
 */
public class AddNodeToExperimentMenu {
  @Inject
  @Service
  ProcedureService procedures;
  @Inject
  Log log;
  @Inject
  @Localize
  ExperimentProperties text;

  @AboutToShow
  void aboutToShow(List<MMenuElement> items, WorkspaceExperiment experiment) {
    procedures
        .procedures()
        .filter(p -> p.requirement().resolveCapability(scheduledCondition()).isPresent())
        .map(t -> createMenuItem(experiment, t))
        .forEach(items::add);
  }

  private MDirectMenuItem createMenuItem(
      WorkspaceExperiment experiment,
      Procedure<?> subProcedure) {
    MDirectMenuItem moduleItem = MMenuFactory.INSTANCE.createDirectMenuItem();
    moduleItem.setLabel(procedures.getId(subProcedure));
    moduleItem.setType(ItemType.PUSH);
    moduleItem.setObject(new Object() {
      @Execute
      public void execute() {
        addNode(experiment, subProcedure);
      }
    });
    return moduleItem;
  }

  private void addNode(WorkspaceExperiment experiment, Procedure<?> subProcedure) {
    try {
      experiment.experiment().attach(new ExperimentStep<>(subProcedure));

    } catch (Exception e) {
      log.log(Level.ERROR, e);

      Alert alert = new Alert(AlertType.ERROR);
      DialogUtilities.addStackTrace(alert, e);
      alert.setTitle(text.attachNodeFailedDialog().toString());
      alert.setHeaderText(text.attachNodeFailedText(experiment, subProcedure).toString());
      alert.setContentText(text.attachNodeFailedDescription().toString());
      alert.showAndWait();
    }
  }
}
