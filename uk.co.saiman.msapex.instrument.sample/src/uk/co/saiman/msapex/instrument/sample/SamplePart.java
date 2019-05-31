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

import static java.util.stream.Collectors.toSet;
import static uk.co.saiman.fx.FxUtilities.wrap;
import static uk.co.saiman.fx.FxmlLoadBuilder.buildWith;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.OSGiBundle;
import org.eclipse.fx.core.di.LocalInstance;
import org.osgi.framework.BundleContext;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.eclipse.utilities.EclipseContextUtilities;
import uk.co.saiman.instrument.sample.SampleDevice;
import uk.co.saiman.msapex.instrument.sample.i18n.SampleDeviceProperties;
import uk.co.saiman.osgi.ServiceIndex;
import uk.co.saiman.osgi.ServiceRecord;

/**
 * An Eclipse part for management and display of acquisition devices.
 * 
 * @author Elias N Vasylenko
 */
public class SamplePart {
  class PaneInstance {
    private final Pane pane;
    private final Object contextObject;

    public PaneInstance(SampleDevicePanel devicePane) {
      this.pane = new Pane();

      IEclipseContext context = SamplePart.this.context.createChild();
      context.set(Pane.class, pane);
      context.set(SampleDevice.class, devicePane.device());
      EclipseContextUtilities.injectSubtypes(context, SampleDevice.class);

      this.contextObject = ContextInjectionFactory
          .make(devicePane.paneModelClass(), SamplePart.this.context, context);

      context.dispose();
    }

    public void dispose() {
      ContextInjectionFactory.uninject(contextObject, context);
    }
  }

  static final String ID = "uk.co.saiman.msapex.instrument.sample.part";

  private final SampleDeviceProperties text;

  @FXML
  private Pane samplePane;
  @FXML
  private Label noSelectionLabel;
  @FXML
  private Label noPanelForSelectionLabel;

  private final ServiceIndex<?, String, SampleDevice<?, ?>> deviceIndex;
  private final ServiceIndex<?, SampleDevice<?, ?>, SampleDevicePanel> devicePanelIndex;

  private SampleDevice<?, ?> device;
  private SampleDevicePanel devicePanel;
  private final Map<SampleDevicePanel, PaneInstance> panes = new HashMap<>();

  private final IEclipseContext context;

  @Inject
  public SamplePart(
      BorderPane container,
      IEclipseContext context,
      @LocalInstance FXMLLoader loaderProvider,
      @OSGiBundle BundleContext bundleContext,
      @Localize SampleDeviceProperties text) {
    this.context = context;

    this.text = text;
    this.deviceIndex = ServiceIndex
        .open(bundleContext, SampleDevice.class, a -> (SampleDevice<?, ?>) a);
    this.devicePanelIndex = ServiceIndex
        .open(
            bundleContext,
            SampleDevicePanel.class,
            Function.identity(),
            (a, b) -> deviceIndex.findRecord(a.device()).map(ServiceRecord::serviceObject));

    devicePanelIndex.events().observe(event -> updateAvailableDevicePanels());

    container.setCenter(buildWith(loaderProvider).controller(this).loadRoot());
  }

  @PreDestroy
  void close() {
    devicePanelIndex.close();
  }

  @FXML
  void initialize() {
    noSelectionLabel.textProperty().bind(wrap(text.noSampleSources()));
    noPanelForSelectionLabel.textProperty().bind(wrap(text.noPanelForSampleSources()));
  }

  synchronized void updateAvailableDevicePanels() {
    updateSelectedDevice(device);
  }

  @Inject
  synchronized void updateSelectedDevice(@Optional SampleDevice<?, ?> device) {
    var devicePanel = device == null
        ? null
        : devicePanelIndex.get(device).map(ServiceRecord::serviceObject).orElse(null);

    var availableDevices = devicePanelIndex
        .records()
        .map(ServiceRecord::serviceObject)
        .collect(toSet());
    var entryIterator = panes.entrySet().iterator();
    while (entryIterator.hasNext()) {
      var entry = entryIterator.next();
      if (!availableDevices.contains(entry.getKey())) {
        entryIterator.remove();
        entry.getValue().dispose();
      }
    }

    if (this.device != device) {
      this.device = device;
      noSelectionLabel.setVisible(device == null);
    }

    if (this.device != device || this.devicePanel != devicePanel) {
      this.devicePanel = devicePanel;
      noPanelForSelectionLabel.setVisible(device != null && devicePanel == null);

      if (devicePanel == null) {
        samplePane.getChildren().clear();

      } else {
        PaneInstance pane = panes.computeIfAbsent(devicePanel, PaneInstance::new);
        if (!samplePane.getChildren().contains(pane.pane)) {
          samplePane.getChildren().setAll(pane.pane);
        }
      }
    }
  }
}
