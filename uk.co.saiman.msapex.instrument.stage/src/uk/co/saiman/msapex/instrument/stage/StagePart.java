/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.msapex.instrument.stage.
 *
 * uk.co.saiman.msapex.instrument.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.instrument.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.instrument.stage;

import static org.eclipse.e4.ui.services.IServiceConstants.ACTIVE_SELECTION;
import static uk.co.saiman.fx.FxUtilities.wrap;
import static uk.co.saiman.fx.FxmlLoadBuilder.buildWith;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.fx.core.di.LocalInstance;

import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import uk.co.saiman.eclipse.AdaptNamed;
import uk.co.saiman.eclipse.Localize;
import uk.co.saiman.eclipse.ObservableService;
import uk.co.saiman.experiment.sample.StageConfiguration;
import uk.co.saiman.instrument.stage.StageDevice;
import uk.co.saiman.instrument.stage.StageProperties;

/**
 * An Eclipse part for management and display of acquisition devices.
 * 
 * @author Elias N Vasylenko
 */
public class StagePart {
  @Localize
  @Inject
  StageProperties text;

  @FXML
  private Pane chartPane;
  @FXML
  private Label noSelectionLabel;

  @Inject
  @ObservableService
  ObservableSet<StageDevice> availableDevices;

  private StageDevice selectedDevice;

  @PostConstruct
  void postConstruct(BorderPane container, @LocalInstance FXMLLoader loaderProvider) {
    container.setCenter(buildWith(loaderProvider).controller(this).loadRoot());
  }

  @FXML
  void initialize() {
    noSelectionLabel.textProperty().bind(wrap(text.noStageDevices()));
  }

  protected void selectStageDevice(StageDevice stageDevice) {
    noSelectionLabel.setVisible(false);
    selectedDevice = stageDevice;
  }

  protected void deselectStageDevice() {
    noSelectionLabel.setVisible(chartPane.getChildren().isEmpty());
    selectedDevice = null;
  }

  @Inject
  synchronized void setSelection(
      @Optional @AdaptNamed(ACTIVE_SELECTION) StageDevice device,
      MPart part) {
    if (device != null) {
      part.getContext().activate();

      selectStageDevice(device);
    }
  }

  @Inject
  synchronized void setSelection(
      @Optional @AdaptNamed(ACTIVE_SELECTION) StageConfiguration configuration,
      MPart part) {
    if (configuration != null) {
      setSelection(configuration.stageDevice(), part);
    }
  }
}
