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
import static java.util.stream.Collectors.toList;
import static org.eclipse.e4.ui.services.IServiceConstants.ACTIVE_SELECTION;

import java.util.stream.Stream;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.OSGiBundle;
import org.eclipse.e4.ui.model.application.MAddon;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.fx.core.di.Service;
import org.osgi.framework.BundleContext;

import uk.co.saiman.eclipse.adapter.AdaptNamed;
import uk.co.saiman.eclipse.model.IndexedSelectionService;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;
import uk.co.saiman.log.Log;
import uk.co.saiman.osgi.ServiceIndex;

/**
 * Register an acquisition device selection in the application context and
 * persist the selection between runs.
 *
 * @author Elias N Vasylenko
 */
public class AcquisitionAddon {
  private static final String SELECTED_DEVICES_KEY = "selected.devices";

  private final IEclipseContext context;

  private final ServiceIndex<?, String, AcquisitionDevice<?>> deviceIndex;

  @Inject
  private EPartService partService;

  @Inject
  public AcquisitionAddon(
      MAddon addon,
      @Service Log log,
      IEclipseContext context,
      @OSGiBundle BundleContext bundleContext) {
    this.context = context;
    this.deviceIndex = ServiceIndex
        .open(bundleContext, AcquisitionDevice.class, a -> (AcquisitionDevice<?>) a);

    IndexedSelectionService
        .startIndexedSelectionService(
            SELECTED_DEVICES_KEY,
            addon,
            context,
            log,
            deviceIndex,
            (c, selection) -> c
                .modify(
                    AcquisitionDeviceSelection.class,
                    new AcquisitionDeviceSelection(selection)),
            c -> Stream
                .ofNullable(c.get(AcquisitionDeviceSelection.class))
                .flatMap(AcquisitionDeviceSelection::getSelectedDevices)
                .collect(toList()));
  }

  @PreDestroy
  void close() {
    deviceIndex.close();
  }

  @Inject
  synchronized void setSelection(
      @Optional @AdaptNamed(ACTIVE_SELECTION) AcquisitionDevice<?> device) {
    var selection = context.get(AcquisitionDeviceSelection.class);

    if (device != null) {
      if (selection == null || !selection.getSelectedDevices().anyMatch(device::equals)) {
        selection = new AcquisitionDeviceSelection(asList(device));
        context.modify(AcquisitionDeviceSelection.class, selection);
      }
      partService.showPart(AcquisitionPart.ID, PartState.VISIBLE);
    }
  }
}
