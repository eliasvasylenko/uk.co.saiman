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

import static java.lang.Boolean.parseBoolean;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MAddon;
import org.eclipse.fx.core.di.Service;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import uk.co.saiman.camera.CameraConnection;
import uk.co.saiman.camera.CameraDevice;
import uk.co.saiman.eclipse.service.ObservableService;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

/**
 * Register a camera device in the application context and persist the selection
 * between runs.
 *
 * @author Elias N Vasylenko
 */
public class CameraAddon {
  private static final String SELECTED_DEVICE_KEY = "selected.device";
  private static final String CONNECTION_OPEN_KEY = "connection.open";

  @Inject
  @ObservableService
  private ObservableList<CameraDevice> availableDevices;

  @Inject
  private IEclipseContext context;

  @Optional
  @Inject
  @Service
  private Log log;

  @Inject
  private MAddon addon;
  private String defaultCamera;
  private boolean defaultConnect;

  @PostConstruct
  void initialize() {
    defaultCamera = addon.getPersistedState().get(SELECTED_DEVICE_KEY);
    defaultConnect = parseBoolean(addon.getPersistedState().get(CONNECTION_OPEN_KEY));

    context.declareModifiable(CameraDevice.class);
    context.declareModifiable(CameraConnection.class);

    // Track updates to camera and connection...
    context.runAndTrack(new RunAndTrack() {
      @Override
      public boolean changed(IEclipseContext context) {
        CameraDevice device = context.get(CameraDevice.class);
        CameraConnection connection = context.get(CameraConnection.class);

        // Set the default camera device to the last one which was selected
        if (device != null) {
          defaultCamera = device.getName();
          defaultConnect = connection != null;
          addon.getPersistedState().put(SELECTED_DEVICE_KEY, defaultCamera);
          addon.getPersistedState().put(CONNECTION_OPEN_KEY, Boolean.toString(defaultConnect));
        }

        // Make sure any open connection always belongs to the selected camera!
        if (connection != null && (device == null || connection.getDevice() != device)) {
          log.log(
              Level.ERROR,
              format(
                  "Camera connection %s inconsistent with device %s in Eclipse context",
                  connection,
                  device));
        }

        return true;
      }
    });

    availableDevices.addListener((InvalidationListener) i -> updateCameraDevice());
    updateCameraDevice();
  }

  private synchronized void updateCameraDevice() {
    List<CameraDevice> devices = new ArrayList<>(availableDevices);

    CameraDevice currentDevice = context.get(CameraDevice.class);

    if (currentDevice == null) {
      for (CameraDevice device : devices) {
        if (device.getName().equals(defaultCamera)) {
          boolean connect = defaultConnect;
          CameraSelectionHelper.selectCamera(context, device);
          if (connect)
            context.modify(CameraConnection.class, device.openConnection());
          break;
        }
      }

    } else if (!devices.contains(currentDevice)) {
      CameraSelectionHelper.selectCamera(context, null);
    }
  }
}
