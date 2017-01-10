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
 * This file is part of uk.co.saiman.instrument.acquisition.msapex.
 *
 * uk.co.saiman.instrument.acquisition.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.acquisition.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.acquisition.msapex;

import static java.util.Arrays.asList;
import static uk.co.strangeskies.fx.FxUtilities.wrap;
import static uk.co.strangeskies.fx.FxmlLoadBuilder.buildWith;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.fx.core.di.LocalInstance;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.acquisition.AcquisitionProperties;
import uk.co.saiman.data.ContinuousFunctionExpression;
import uk.co.saiman.data.msapex.ContinuousFunctionChartController;
import uk.co.saiman.measurement.Units;
import uk.co.strangeskies.eclipse.AdaptNamed;
import uk.co.strangeskies.eclipse.Localize;
import uk.co.strangeskies.eclipse.ObservableService;

/**
 * An Eclipse part for management and display of acquisition devices.
 * 
 * @author Elias N Vasylenko
 */
public class AcquisitionPart {
	@Localize
	@Inject
	private AcquisitionProperties text;

	@FXML
	private Pane chartPane;
	@FXML
	private Label noSelectionLabel;

	@Inject
	@ObservableService
	private ObservableSet<AcquisitionDevice> availableDevices;

	private ObservableSet<AcquisitionDevice> selectedDevices = FXCollections.observableSet();
	private Map<AcquisitionDevice, ContinuousFunctionChartController> controllers = new HashMap<>();

	@Inject
	@LocalInstance
	private FXMLLoader loaderProvider;

	@Inject
	private MPart part;

	@Inject
	private Units units;

	/**
	 * Get the visible acquisition devices.
	 * 
	 * @return The set of all currently visible acquisition devices
	 */
	public synchronized Set<AcquisitionDevice> getSelectedAcquisitionDevices() {
		return new HashSet<>(selectedDevices);
	}

	/**
	 * Set the visible acquisition devices. All devices in the given set will be
	 * made visible, and all others will be removed.
	 * 
	 * @param selectedDevices
	 *          The acquisition devices to show
	 * @return True if the device selection changes as a result of this
	 *         invocation, false otherwise
	 */
	public synchronized boolean setSelectedAcquisitionDevices(Collection<? extends AcquisitionDevice> selectedDevices) {
		return this.selectedDevices.retainAll(selectedDevices) | this.selectedDevices.addAll(selectedDevices);
	}

	/**
	 * Set an acquisition device to be visible. All currently visible devices will
	 * remain so.
	 * 
	 * @param device
	 *          The acquisition device to show
	 * @return True if the device selection changes as a result of this
	 *         invocation, false otherwise
	 */
	public synchronized boolean selectAcquisitionDevice(AcquisitionDevice device) {
		return selectedDevices.add(device);
	}

	/**
	 * Set an acquisition device to not be visible. All other currently visible
	 * devices will remain so.
	 * 
	 * @param device
	 *          The acquisition device to not show
	 * @return True if the device selection changes as a result of this
	 *         invocation, false otherwise
	 */
	public synchronized boolean deselectAcquisitionDevice(AcquisitionDevice device) {
		return selectedDevices.remove(device);
	}

	/**
	 * @return the set of all known available acquisition devices
	 */
	public ObservableSet<AcquisitionDevice> getAvailableAcquisitionDevices() {
		return availableDevices;
	}

	@PostConstruct
	void postConstruct(BorderPane container) {
		container.setCenter(buildWith(loaderProvider).controller(this).loadRoot());

		selectedDevices.addListener((SetChangeListener.Change<? extends AcquisitionDevice> change) -> {
			if (change.wasAdded()) {
				selectAcquisitionDeviceImpl(change.getElementAdded());
			} else if (change.wasRemoved()) {
				deselectAcquisitionDeviceImpl(change.getElementRemoved());
			}
		});
	}

	@FXML
	void initialize() {
		noSelectionLabel.textProperty().bind(wrap(text.noAcquisitionDevices()));
	}

	private void selectAcquisitionDeviceImpl(AcquisitionDevice acquisitionDevice) {
		noSelectionLabel.setVisible(false);

		/*
		 * New chart controller for device
		 */
		ContinuousFunctionChartController chartController = buildWith(loaderProvider)
				.controller(ContinuousFunctionChartController.class)
				.loadController();
		chartController.setTitle(acquisitionDevice.getName());
		controllers.put(acquisitionDevice, chartController);
		chartPane.getChildren().add(chartController.getRoot());
		HBox.setHgrow(chartController.getRoot(), Priority.ALWAYS);

		/*
		 * Create continuous function view of latest data from device
		 */
		ContinuousFunctionExpression<Time, Dimensionless> latestContinuousFunction = new ContinuousFunctionExpression<>(
				units.second().get(),
				units.count().get());
		acquisitionDevice.dataEvents().addWeakObserver(latestContinuousFunction, l -> c -> l.setComponent(c));

		/*
		 * Add latest data to chart controller
		 */
		chartController.getContinuousFunctions().add(latestContinuousFunction);
	}

	private void deselectAcquisitionDeviceImpl(AcquisitionDevice acquisitionDevice) {
		ContinuousFunctionChartController controller = controllers.remove(acquisitionDevice);

		chartPane.getChildren().remove(controller.getRoot());

		noSelectionLabel.setVisible(chartPane.getChildren().isEmpty());
	}

	@Inject
	synchronized void setSelection(@Optional @AdaptNamed(IServiceConstants.ACTIVE_SELECTION) AcquisitionDevice device) {
		if (device != null) {
			part.getContext().activate();

			if (!selectedDevices.contains(device)) {
				setSelectedAcquisitionDevices(asList(device));
			}
		}
	}
}
