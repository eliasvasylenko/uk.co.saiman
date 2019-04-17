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
 * This file is part of uk.co.saiman.msapex.instrument.sample.
 *
 * uk.co.saiman.msapex.instrument.sample is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.instrument.sample is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.instrument.sample;

import static java.util.Optional.ofNullable;
import static org.eclipse.e4.ui.services.IServiceConstants.ACTIVE_SELECTION;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MAddon;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.fx.core.di.Service;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import uk.co.saiman.eclipse.adapter.AdaptNamed;
import uk.co.saiman.eclipse.service.ObservableService;
import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.sample.SampleDevice;
import uk.co.saiman.log.Log;

/**
 * Register a sample device selection in the application context and persist the
 * selection between runs.
 *
 * @author Elias N Vasylenko
 */
public class SampleAddon {
  private static final String SELECTED_DEVICES_KEY = "selected.devices";

  @Inject
  @ObservableService
  private ObservableList<SampleDevice<?, ?>> availableDevices;

  @Inject
  private IEclipseContext context;

  @Inject
  @Service
  private Log log;

  @Inject
  private MAddon addon;
  private String defaultDeviceSelection;

  @PostConstruct
  void initialize(MApplication app) {
    context = app.getContext();

    defaultDeviceSelection = addon.getPersistedState().get(SELECTED_DEVICES_KEY);

    context.declareModifiable(SampleDevice.class);

    // Track updates to camera and connection...
    context.runAndTrack(new RunAndTrack() {
      @Override
      public boolean changed(IEclipseContext context) {
        SampleDevice<?, ?> device = context.get(SampleDevice.class);

        // Set the default camera device to the last one which was selected
        defaultDeviceSelection = ofNullable(device).map(Device::getName).orElse(null);

        /*
         * TODO keep items waiting in the default device selection if they are not in
         * the available devices list yet...
         */

        addon.getPersistedState().put(SELECTED_DEVICES_KEY, defaultDeviceSelection);

        return true;
      }
    });

    availableDevices.addListener((InvalidationListener) i -> updateSelectedDevices());
    updateSelectedDevices();
  }

  private synchronized void updateSelectedDevices() {
    Set<SampleDevice<?, ?>> availableDevices = new HashSet<>(this.availableDevices);

    SampleDevice<?, ?> selectedDevice = context.get(SampleDevice.class);
    if (selectedDevice == null) {
      availableDevices
          .stream()
          .filter(d -> d.getName().equals(defaultDeviceSelection))
          .findAny()
          .ifPresent(s -> {
            context.modify(SampleDevice.class, s);
            context.processWaiting();
          });
    }
  }

  @Inject
  synchronized void setSelection(
      @Optional @AdaptNamed(ACTIVE_SELECTION) SampleDevice<?, ?> device,
      EPartService partService,
      @Optional SampleDevice<?, ?> selection) {
    if (device != null) {
      if (selection != device) {
        context.modify(SampleDevice.class, device);
      }
      partService.showPart(SamplePart.ID, PartState.ACTIVATE);
    }
  }
}
