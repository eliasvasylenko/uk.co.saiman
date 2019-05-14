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

import java.nio.file.Path;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import uk.co.saiman.eclipse.dialog.DialogUtilities;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.experiment.graph.ExperimentId;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.experiment.storage.Store;
import uk.co.saiman.experiment.storage.filesystem.FileSystemStore;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.msapex.experiment.i18n.ExperimentProperties;
import uk.co.saiman.msapex.experiment.workspace.Workspace;

/**
 * Track acquisition devices available through OSGi services and select which
 * device to display in the acquisition part.
 * 
 * @author Elias N Vasylenko
 */
public class AddWorkspaceExperimentHandler {
  @Inject
  Log log;

  @Inject
  @Localize
  ExperimentProperties text;

  @Inject
  Workspace workspace;
  @Inject
  FileSystemStore store;

  @Execute
  void execute() {
    new NewFileSystemExperimentDialog(workspace, text)
        .showAndWait()
        .map(ExperimentId::fromName)
        .ifPresent(name -> newExperiment(name, workspace, store));
  }

  private void newExperiment(ExperimentId name, Workspace workspace, Store<Path> store) {
    try {
      workspace.newExperiment(name, new StorageConfiguration<>(store, Path.of("")));

    } catch (Exception e) {
      log.log(Level.ERROR, e);

      Alert alert = new Alert(AlertType.ERROR);
      DialogUtilities.addStackTrace(alert, e);
      alert.setTitle(text.newExperimentFailedDialog().toString());
      alert
          .setHeaderText(
              text
                  .newExperimentFailedText(workspace, text.newWorkspaceExperiment().toString())
                  .toString());
      alert.setContentText(text.newExperimentFailedDescription().toString());
      alert.showAndWait();
    }
  }
}
