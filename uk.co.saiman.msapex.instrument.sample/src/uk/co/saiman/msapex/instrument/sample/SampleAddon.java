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

import static java.util.Collections.singleton;
import static org.eclipse.e4.ui.services.IServiceConstants.ACTIVE_SELECTION;
import static org.eclipse.e4.ui.workbench.UIEvents.ALL_ELEMENT_ID;
import static org.eclipse.e4.ui.workbench.UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC;
import static uk.co.saiman.eclipse.model.IndexedSelectionService.startIndexedSelectionService;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.OSGiBundle;
import org.eclipse.e4.ui.model.application.MAddon;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.fx.core.di.Service;
import org.osgi.framework.BundleContext;

import uk.co.saiman.eclipse.adapter.AdaptNamed;
import uk.co.saiman.instrument.sample.SampleDevice;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.log.Log;
import uk.co.saiman.observable.Disposable;
import uk.co.saiman.osgi.ServiceIndex;

/**
 * Register a sample device selection in the application context and persist the
 * selection between runs.
 *
 * @author Elias N Vasylenko
 */
public class SampleAddon {
  private static final String SELECTED_DEVICE_KEY = "selected.device";

  private final IEclipseContext context;

  private final ServiceIndex<?, String, SampleDevice<?, ?>> deviceIndex;

  @Inject
  private EPartService partService;

  private Disposable stateObserver;

  @Inject
  public SampleAddon(
      MAddon addon,
      @Service Log log,
      IEclipseContext context,
      @OSGiBundle BundleContext bundleContext) {
    this.context = context;
    this.deviceIndex = ServiceIndex
        .open(bundleContext, SampleDevice.class, a -> (SampleDevice<?, ?>) a);

    context.declareModifiable(SampleDevice.class);
    context.declareModifiable(SampleState.class);

    startIndexedSelectionService(
        SELECTED_DEVICE_KEY,
        addon,
        context,
        log,
        deviceIndex,
        (c, selection) -> c.modify(SampleDevice.class, selection.iterator().next()),
        c -> singleton((SampleDevice<?, ?>) c.get(SampleDevice.class)));
  }

  @PreDestroy
  void close() {
    deviceIndex.close();
  }

  @Inject
  synchronized void setSelection(
      @Optional @AdaptNamed(ACTIVE_SELECTION) SampleDevice<?, ?> device) {
    var selection = context.get(SampleDevice.class);

    if (device != null) {
      if (selection == null || !device.equals(selection)) {
        selection = device;
        context.modify(SampleDevice.class, selection);
      }
      partService.showPart(SamplePart.ID, PartState.VISIBLE);
    }
  }

  @Inject
  public void deviceChange(@Optional SampleDevice<?, ?> device) {
    if (device != null) {
      stateObserver = device
          .sampleState()
          .value()
          .observe(state -> context.set(SampleState.class, state));
    } else if (stateObserver != null) {
      stateObserver.cancel();
      context.remove(SampleState.class);
    }
  }

  @Inject
  public void stateChange(EventBroker eventBroker, @Optional SampleState state) {
    eventBroker.send(REQUEST_ENABLEMENT_UPDATE_TOPIC, ALL_ELEMENT_ID);
  }
}
