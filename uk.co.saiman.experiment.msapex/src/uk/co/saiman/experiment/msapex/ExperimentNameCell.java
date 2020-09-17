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
package uk.co.saiman.experiment.msapex;

import static uk.co.saiman.experiment.msapex.ExperimentStepCell.SUPPLEMENTAL_PSEUDO_CLASS;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Service;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import uk.co.saiman.eclipse.dialog.DialogUtilities;
import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.ui.fx.EditableCellText;
import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.event.RenameExperimentEvent;
import uk.co.saiman.experiment.msapex.i18n.ExperimentProperties;
import uk.co.saiman.experiment.msapex.workspace.WorkspaceExperiment;
import uk.co.saiman.experiment.msapex.workspace.WorkspaceExperiment.Status;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

/**
 * Contribution for root experiment nodes in the experiment tree
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentNameCell {
  @Inject
  IEclipseContext context;
  @Inject
  Log log;
  @Inject
  @Service
  ExperimentProperties text;

  @Inject
  MCell cell;

  @Inject
  EditableCellText nameEditor;

  @Inject
  WorkspaceExperiment experiment;

  @PostConstruct
  public void prepare(HBox node) {
    node.getChildren().add(nameEditor);
    HBox.setHgrow(nameEditor, Priority.SOMETIMES);

    nameEditor.setText(experiment.id().name());
    nameEditor.setTryUpdate(name -> renameExperiment(experiment, name));
    nameEditor.getLabel().pseudoClassStateChanged(SUPPLEMENTAL_PSEUDO_CLASS, true);
  }

  private boolean renameExperiment(WorkspaceExperiment experiment, String name) {
    try {
      experiment.rename(ExperimentId.fromName(name));
      return true;

    } catch (Exception e) {
      log.log(Level.ERROR, e);

      Alert alert = new Alert(AlertType.ERROR);
      DialogUtilities.addStackTrace(alert, e);
      alert.setTitle(text.renameExperimentFailedDialog().toString());
      alert.setHeaderText(text.renameExperimentFailedText(experiment).toString());
      alert.setContentText(text.renameExperimentFailedDescription().toString());
      alert.showAndWait();

      return false;
    }
  }

  @Inject
  @Optional
  public void updateName(RenameExperimentEvent event) {
    if (experiment.status() == Status.OPEN
        && Objects.equals(event.experiment(), experiment.open())) {
      nameEditor.setText(event.id().name());
    }
  }
}
