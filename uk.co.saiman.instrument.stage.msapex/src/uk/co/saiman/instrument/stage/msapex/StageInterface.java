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
 * This file is part of uk.co.saiman.instrument.stage.msapex.
 *
 * uk.co.saiman.instrument.stage.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.stage.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.stage.msapex;

import static uk.co.strangeskies.fx.FxUtilities.wrap;
import static uk.co.strangeskies.fx.FxmlLoadBuilder.buildWith;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.fx.core.di.LocalInstance;

import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import uk.co.saiman.experiment.sample.XYStageConfiguration;
import uk.co.saiman.instrument.stage.StageDevice;
import uk.co.saiman.instrument.stage.StageProperties;
import uk.co.strangeskies.eclipse.AdaptNamed;
import uk.co.strangeskies.eclipse.Localize;
import uk.co.strangeskies.eclipse.ObservableService;

/**
 * An abstraction of the user facing interface of the stage.
 * 
 * @author Elias N Vasylenko
 */
public class StageInterface {
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
      @Optional @AdaptNamed(IServiceConstants.ACTIVE_SELECTION) XYStageConfiguration configuration) {
    if (configuration != null) {
      selectStageDevice(configuration.stageDevice());
    }
  }
}
