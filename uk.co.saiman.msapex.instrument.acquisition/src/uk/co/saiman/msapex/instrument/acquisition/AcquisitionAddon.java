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
package uk.co.saiman.msapex.instrument.acquisition;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static org.eclipse.e4.ui.services.IServiceConstants.ACTIVE_SELECTION;

import java.util.HashSet;
import java.util.List;
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
import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.eclipse.adapter.AdaptNamed;
import uk.co.saiman.eclipse.service.ObservableService;
import uk.co.saiman.instrument.Device;
import uk.co.saiman.log.Log;

/**
 * Register an acquisition device selection in the application context and
 * persist the selection between runs.
 *
 * @author Elias N Vasylenko
 */
public class AcquisitionAddon {
  private static final String SELECTED_DEVICES_KEY = "selected.devices";

  @Inject
  @ObservableService
  private ObservableList<AcquisitionDevice> availableDevices;

  @Inject
  private IEclipseContext context;

  @Inject
  @Service
  @Optional
  private Log log;

  @Inject
  private MAddon addon;
  private List<String> defaultDeviceSelection;

  @PostConstruct
  void initialize(MApplication app) {
    context = app.getContext();

    String defaultDeviceSelectionString = addon.getPersistedState().get(SELECTED_DEVICES_KEY);

    defaultDeviceSelection = defaultDeviceSelectionString == null
        ? emptyList()
        : asList(defaultDeviceSelectionString.split(","));

    context.declareModifiable(AcquisitionDeviceSelection.class);

    // Track updates to camera and connection...
    context.runAndTrack(new RunAndTrack() {
      @Override
      public boolean changed(IEclipseContext context) {
        AcquisitionDeviceSelection devices = context.get(AcquisitionDeviceSelection.class);

        // Set the default camera device to the last one which was selected
        if (devices != null && devices.getSelectedDevices().findAny().isPresent()) {
          defaultDeviceSelection = devices.getSelectedDevices().map(Device::getName).collect(
              toList());

          /*
           * TODO keep items waiting in the default device selection if they are
           * not in the available devices list yet...
           */

          addon
              .getPersistedState()
              .put(SELECTED_DEVICES_KEY, defaultDeviceSelection.stream().collect(joining(",")));
        }

        return true;
      }
    });

    availableDevices.addListener((InvalidationListener) i -> updateSelectedDevices());
    updateSelectedDevices();
  }

  private synchronized void updateSelectedDevices() {
    Set<AcquisitionDevice> availableDevices = new HashSet<>(this.availableDevices);

    AcquisitionDeviceSelection selection = context.get(AcquisitionDeviceSelection.class);
    Set<AcquisitionDevice> selectedDevices = selection == null
        ? new HashSet<>()
        : selection.getSelectedDevices().collect(toCollection(HashSet::new));

    boolean added = false;
    for (AcquisitionDevice device : availableDevices) {
      if (defaultDeviceSelection.contains(device.getName())) {
        if (selectedDevices.add(device))
          added = true;
      }
    }

    if (selectedDevices.retainAll(availableDevices) || added) {
      context.modify(
          AcquisitionDeviceSelection.class,
          new AcquisitionDeviceSelection(selectedDevices));
      context.processWaiting();
    }
  }

  @Inject
  synchronized void setSelection(
      @Optional @AdaptNamed(ACTIVE_SELECTION) AcquisitionDevice device,
      EPartService partService,
      @Optional AcquisitionDeviceSelection selection) {
    if (device != null) {
      partService.showPart(AcquisitionPart.ID, PartState.ACTIVATE);
      if (selection == null || !selection.getSelectedDevices().anyMatch(device::equals))
        context.modify(
            AcquisitionDeviceSelection.class,
            new AcquisitionDeviceSelection(asList(device)));
    }
  }
}
