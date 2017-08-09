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
 * This file is part of uk.co.saiman.msapex.camera.
 *
 * uk.co.saiman.msapex.camera is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.camera is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.camera;

import static java.util.Collections.unmodifiableSet;
import static uk.co.saiman.fx.FxUtilities.wrap;
import static uk.co.saiman.fx.FxmlLoadBuilder.buildWith;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.fx.core.di.LocalInstance;

import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import uk.co.saiman.camera.CameraDevice;
import uk.co.saiman.camera.CameraProperties;
import uk.co.saiman.eclipse.Localize;
import uk.co.saiman.eclipse.ObservableService;

/**
 * An Eclipse part for management and display of acquisition devices.
 *
 * @author Elias N Vasylenko
 */
public class CameraPart {
	@Localize
	@Inject
	CameraProperties text;

	@FXML
	private Pane chartPane;
	@FXML
	private Label noSelectionLabel;

	@Inject
	@ObservableService
	ObservableSet<CameraDevice> availableDevices;

	private CameraDevice selectedDevice;

	@Inject
	@LocalInstance
	FXMLLoader loaderProvider;

	@PostConstruct
	void postConstruct(BorderPane container) {
		container.setCenter(buildWith(loaderProvider).controller(this).loadRoot());
	}

	@FXML
	void initialize() {
		noSelectionLabel.textProperty().bind(wrap(text.noCameraDevices()));
	}

	public void selectCameraDevice(CameraDevice device) {
		noSelectionLabel.setVisible(false);
		selectedDevice = device;
	}

	public void deselectCameraDevice() {
		noSelectionLabel.setVisible(chartPane.getChildren().isEmpty());
		selectedDevice = null;
	}

	public Set<CameraDevice> getAvailableCameraDevices() {
		return unmodifiableSet(availableDevices);
	}
}
