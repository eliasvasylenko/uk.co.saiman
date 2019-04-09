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
 * This file is part of uk.co.saiman.msapex.instrument.acquisition.
 *
 * uk.co.saiman.msapex.instrument.acquisition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.instrument.acquisition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.instrument.vacuum;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toCollection;
import static uk.co.saiman.fx.FxUtilities.wrap;
import static uk.co.saiman.fx.FxmlLoadBuilder.buildWith;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.fx.core.di.LocalInstance;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.msapex.instrument.vacuum.i18n.VacuumProperties;
import uk.co.saiman.vacuum.VacuumDevice;

/**
 * An Eclipse part for management and display of vacuum devices.
 * 
 * @author Elias N Vasylenko
 */
public class VacuumPart {
  static final String ID = "uk.co.saiman.msapex.instrument.vacuum.part";

  @Localize
  @Inject
  private VacuumProperties text;

  @FXML
  private Pane chartPane;
  @FXML
  private Label noSelectionLabel;

  private Set<VacuumDevice<?>> currentSelection;
  private Map<VacuumDevice<?>, PressureChart> controllers = new HashMap<>();

  @Inject
  @LocalInstance
  private FXMLLoader loaderProvider;

  @Inject
  synchronized void updateSelectedDevices(@Optional VacuumDeviceSelection devices) {
    if (chartPane == null)
      return;

    Set<VacuumDevice<?>> newSelection = devices == null
        ? emptySet()
        : devices.getSelectedDevices().collect(toCollection(LinkedHashSet::new));

    for (VacuumDevice<?> oldDevice : currentSelection) {
      if (!newSelection.remove(oldDevice)) {
        deselectVacuumDevice(oldDevice);
        currentSelection.remove(oldDevice);
      }
    }
    for (VacuumDevice<?> newDevice : newSelection) {
      selectVacuumDevice(newDevice);
      currentSelection.add(newDevice);
    }
  }

  @PostConstruct
  void postConstruct(BorderPane container, @Optional VacuumDeviceSelection devices) {
    container.setCenter(buildWith(loaderProvider).controller(this).loadRoot());

    currentSelection = new HashSet<>();
    updateSelectedDevices(devices);
  }

  @FXML
  void initialize() {
    noSelectionLabel.textProperty().bind(wrap(text.noDevicesSelected()));
  }

  private void selectVacuumDevice(VacuumDevice<?> vacuumDevice) {
    noSelectionLabel.setVisible(false);

    /*
     * New chart controller for device
     */

    PressureChart chartController = new PressureChart(vacuumDevice);
    controllers.put(vacuumDevice, chartController);
    chartPane.getChildren().add(chartController.getNode());
    HBox.setHgrow(chartController.getNode(), Priority.ALWAYS);
  }

  private void deselectVacuumDevice(VacuumDevice<?> vacuumDevice) {
    PressureChart controller = controllers.remove(vacuumDevice);

    chartPane.getChildren().remove(controller.getNode());

    noSelectionLabel.setVisible(chartPane.getChildren().isEmpty());
  }
}
