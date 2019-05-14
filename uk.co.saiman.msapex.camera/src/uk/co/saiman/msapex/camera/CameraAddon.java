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
import static uk.co.saiman.log.Log.Level.ERROR;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;
import org.eclipse.e4.core.di.extensions.OSGiBundle;
import org.eclipse.e4.ui.model.application.MAddon;
import org.eclipse.fx.core.di.Service;
import org.osgi.framework.BundleContext;

import uk.co.saiman.camera.CameraConnection;
import uk.co.saiman.camera.CameraDevice;
import uk.co.saiman.log.Log;
import uk.co.saiman.osgi.ServiceIndex;

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
  private IEclipseContext context;

  @Inject
  @Service
  private Log log;

  @Inject
  private MAddon addon;
  private String defaultCamera;
  private boolean defaultConnect;

  private ServiceIndex<CameraDevice, String, CameraDevice> deviceIndex;

  @PostConstruct
  void initialize(@OSGiBundle BundleContext bundleContext) {
    deviceIndex = ServiceIndex.open(bundleContext, CameraDevice.class);

    defaultCamera = addon.getPersistedState().get(SELECTED_DEVICE_KEY);
    defaultConnect = parseBoolean(addon.getPersistedState().get(CONNECTION_OPEN_KEY));

    context.declareModifiable(CameraDevice.class);
    context.declareModifiable(CameraConnection.class);

    deviceIndex.events().observe(i -> updateCameraDevice());
    updateCameraDevice();

    // Track updates to camera and connection...
    context.runAndTrack(new RunAndTrack() {
      @Override
      public boolean changed(IEclipseContext context) {
        CameraDevice device = context.get(CameraDevice.class);
        CameraConnection connection = context.get(CameraConnection.class);

        // Set the default camera device to the last one which was selected
        deviceIndex.findRecord(device).ifPresent(service -> {
          defaultCamera = service.id().orElse(null);
          addon.getPersistedState().put(SELECTED_DEVICE_KEY, defaultCamera);
        });
        defaultConnect = connection != null;
        addon.getPersistedState().put(CONNECTION_OPEN_KEY, Boolean.toString(defaultConnect));

        // Make sure any open connection always belongs to the selected camera!
        if (connection != null && (device == null || connection.getDevice() != device)) {
          log
              .log(
                  ERROR,
                  format(
                      "Camera connection %s inconsistent with device %s in Eclipse context",
                      connection,
                      device));
        }

        return true;
      }
    });
  }

  @PreDestroy
  void close() {
    deviceIndex.close();
  }

  private synchronized void updateCameraDevice() {
    CameraDevice currentDevice = context.get(CameraDevice.class);

    if (currentDevice == null) {
      deviceIndex.get(defaultCamera).ifPresent(device -> {
        boolean connect = defaultConnect;
        CameraSelectionHelper.selectCamera(context, device.serviceObject());
        if (connect) {
          context.modify(CameraConnection.class, device.serviceObject().openConnection());
        }
      });

    } else if (deviceIndex.findRecord(currentDevice).isEmpty()) {
      CameraSelectionHelper.selectCamera(context, null);
    }
  }
}
