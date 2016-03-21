/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.acquisition.msapex.
 *
 * uk.co.saiman.acquisition.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.acquisition.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.acquisition.msapex;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Focus;
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
import uk.co.saiman.data.ContinuousFunctionExpression;
import uk.co.saiman.data.msapex.ContinuousFunctionChartController;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;
import uk.co.strangeskies.fx.FXUtilities;

/**
 * An Eclipse part for management and display of acquisition devices.
 * 
 * @author Elias N Vasylenko
 */
public class AcquisitionPart {
	@Inject
	IEclipseContext context;

	@FXML
	private Pane chartPane;

	@FXML
	private Label noSelectionLabel;

	private ObservableSet<AcquisitionDevice> selectedModules = FXCollections.observableSet();
	private Map<AcquisitionDevice, ContinuousFunctionChartController> controllers = new HashMap<>();

	/**
	 * Set the visible acquisition devices. All devices in the given set will be
	 * made visible, and all others will be removed.
	 * 
	 * @param selectedModules
	 *          The acquisition devices to show
	 * @return True if the device selection changes as a result of this
	 *         invocation, false otherwise
	 */
	public boolean setAcquisitionModules(Collection<? extends AcquisitionDevice> selectedModules) {
		return this.selectedModules.retainAll(selectedModules) | this.selectedModules.addAll(selectedModules);
	}

	/**
	 * Set an acquisition device to be visible. All currently visible devices will
	 * remain so.
	 * 
	 * @param module
	 *          The acquisition device to show
	 * @return True if the device selection changes as a result of this
	 *         invocation, false otherwise
	 */
	public boolean addAcquisitionModule(AcquisitionDevice module) {
		return selectedModules.add(module);
	}

	/**
	 * Set an acquisition device to not be visible. All other currently visible
	 * devices will remain so.
	 * 
	 * @param module
	 *          The acquisition device to not show
	 * @return True if the device selection changes as a result of this
	 *         invocation, false otherwise
	 */
	public boolean removeAcquisitionModule(AcquisitionDevice module) {
		return selectedModules.remove(module);
	}

	/**
	 * Get the visible acquisition devices.
	 * 
	 * @return The set of all currently visible acquisition devices
	 */
	public Set<AcquisitionDevice> getSelectedAcquisitionModules() {
		return new HashSet<>(selectedModules);
	}

	@PostConstruct
	void initialise(BorderPane container, @LocalInstance FXMLLoader loader) {
		container.setCenter(FXUtilities.loadIntoController(loader, this));

		selectedModules.addListener((SetChangeListener.Change<? extends AcquisitionDevice> change) -> {
			if (change.wasAdded()) {
				selectAcquisitionModule(loader, change.getElementAdded());
			} else if (change.wasRemoved()) {
				deselectAcquisitionModule(change.getElementRemoved());
			}
		});
	}

	private void selectAcquisitionModule(FXMLLoader loader, AcquisitionDevice acquisitionModule) {
		noSelectionLabel.setVisible(false);

		/*
		 * New chart controller for module
		 */
		ContinuousFunctionChartController chartController = FXUtilities.loadController(loader,
				ContinuousFunctionChartController.class);
		chartController.setTitle(acquisitionModule.getName());
		controllers.put(acquisitionModule, chartController);
		chartPane.getChildren().add(chartController.getRoot());
		HBox.setHgrow(chartController.getRoot(), Priority.ALWAYS);

		/*
		 * Create continuous function view of latest data from module
		 */
		ContinuousFunctionExpression latestContinuousFunction = new ContinuousFunctionExpression();
		acquisitionModule.dataEvents().addWeakObserver(latestContinuousFunction, l -> c -> l.setComponent(c));

		/*
		 * Add latest data to chart controller
		 */
		chartController.getContinuousFunctions().add(latestContinuousFunction);
	}

	private void deselectAcquisitionModule(AcquisitionDevice acquisitionModule) {
		ContinuousFunctionChartController controller = controllers.remove(acquisitionModule);

		chartPane.getChildren().remove(controller.getRoot());

		noSelectionLabel.setVisible(chartPane.getChildren().isEmpty());
	}

	@Focus
	void focus() {}
}
