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

import static uk.co.saiman.fx.FxUtilities.wrap;
import static uk.co.saiman.fx.FxmlLoadBuilder.buildWith;
import static uk.co.saiman.observable.Observer.onObservation;
import static uk.co.saiman.observable.Observer.singleUse;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.fx.core.di.LocalInstance;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import uk.co.saiman.camera.CameraConnection;
import uk.co.saiman.camera.CameraDevice;
import uk.co.saiman.camera.CameraImage;
import uk.co.saiman.camera.CameraProperties;
import uk.co.saiman.eclipse.Localize;
import uk.co.saiman.observable.Disposable;

/**
 * An Eclipse part for management and display of acquisition devices.
 *
 * @author Elias N Vasylenko
 */
public class CameraPart {
  @Localize
  @Inject
  CameraProperties text;

  @Inject
  IEclipseContext context;

  @FXML
  private ImageView cameraView;
  @FXML
  private Label noSelectionLabel;
  @FXML
  private Label noConnectionLabel;

  private Disposable imageStream;

  @Inject
  @LocalInstance
  FXMLLoader loaderProvider;

  @PostConstruct
  void postConstruct(
      BorderPane container,
      @Optional CameraConnection connection,
      @Optional CameraDevice device) {
    container.setCenter(buildWith(loaderProvider).controller(this).loadRoot());
    updateCameraState(device, connection);
  }

  @FXML
  void initialize() {
    noSelectionLabel.textProperty().bind(wrap(text.noCameraDevices()));
    noConnectionLabel.textProperty().bind(wrap(text.noCameraConnection()));

    Region container = ((Region) cameraView.getParent());
    cameraView.fitWidthProperty().bind(container.widthProperty());
    cameraView.fitHeightProperty().bind(container.heightProperty());
  }

  @Inject
  synchronized void updateCameraState(
      @Optional CameraDevice device,
      @Optional CameraConnection connection) {
    if (cameraView == null)
      return;

    CameraDevice selectedDevice;

    if (connection != null)
      selectedDevice = connection.getDevice();
    else
      selectedDevice = device;

    if (imageStream != null) {
      imageStream.cancel();
      imageStream = null;
    }

    if (selectedDevice == null) {
      cameraView.setVisible(false);
      noSelectionLabel.setVisible(true);
      noConnectionLabel.setVisible(false);

    } else if (connection == null || connection.isDisposed()) {
      cameraView.setVisible(false);
      noSelectionLabel.setVisible(false);
      noConnectionLabel.setVisible(true);

    } else {
      cameraView.setVisible(true);
      noSelectionLabel.setVisible(false);
      noConnectionLabel.setVisible(false);

      imageStream = connection
          .getImageStream()
          .reduceBackpressure((a, b) -> b)
          .executeOn(Platform::runLater)
          .then(onObservation(o -> o.requestNext()))
          .then(singleUse(o -> m -> o.requestNext()))
          .observe(this::setImage);
      setImage(connection.getImage());
    }
  }

  private synchronized void setImage(CameraImage cameraImage) {
    WritableImage image = new WritableImage(cameraImage.getWidth(), cameraImage.getHeight());
    for (int x = 0; x < cameraImage.getWidth(); x++) {
      for (int y = 0; y < cameraImage.getHeight(); y++) {
        image.getPixelWriter().setColor(
            x,
            y,
            new Color(
                cameraImage.getRed(x, y),
                cameraImage.getGreen(x, y),
                cameraImage.getBlue(x, y),
                1));
      }
    }
    cameraView.setImage(image);
  }
}
