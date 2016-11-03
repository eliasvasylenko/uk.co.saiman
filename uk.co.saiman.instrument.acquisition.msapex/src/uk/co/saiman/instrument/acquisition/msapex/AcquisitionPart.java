/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,-========\     ,`===\    /========== \
 *      /== \___/== \  ,`==.== \   \__/== \___\/
 *     /==_/____\__\/,`==__|== |     /==  /
 *     \========`. ,`========= |    /==  /
 *   ___`-___)== ,`== \____|== |   /==  /
 *  /== \__.-==,`==  ,`    |== '__/==  /_
 *  \======== /==  ,`      |== ========= \
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

import static uk.co.strangeskies.fx.FXMLLoadBuilder.buildWith;
import static uk.co.strangeskies.fx.FXUtilities.wrap;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

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
import uk.co.strangeskies.eclipse.Localize;

/**
 * An Eclipse part for management and display of acquisition devices.
 * 
 * @author Elias N Vasylenko
 */
public class AcquisitionPart {
	@Localize
	@Inject
	AcquisitionProperties text;

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

	@Inject
	@LocalInstance
	FXMLLoader loaderProvider;

	@PostConstruct
	void postConstruct(BorderPane container) {
		container.setCenter(buildWith(loaderProvider).controller(this).loadRoot());

		selectedModules.addListener((SetChangeListener.Change<? extends AcquisitionDevice> change) -> {
			if (change.wasAdded()) {
				selectAcquisitionModule(change.getElementAdded());
			} else if (change.wasRemoved()) {
				deselectAcquisitionModule(change.getElementRemoved());
			}
		});
	}

	@FXML
	void initialize() {
		noSelectionLabel.textProperty().bind(wrap(text.noAcquisitionModules()));
	}

	private void selectAcquisitionModule(AcquisitionDevice acquisitionModule) {
		noSelectionLabel.setVisible(false);

		/*
		 * New chart controller for module
		 */
		ContinuousFunctionChartController chartController = buildWith(loaderProvider)
				.controller(ContinuousFunctionChartController.class).loadController();
		chartController.setTitle(acquisitionModule.getName());
		controllers.put(acquisitionModule, chartController);
		chartPane.getChildren().add(chartController.getRoot());
		HBox.setHgrow(chartController.getRoot(), Priority.ALWAYS);

		/*
		 * Create continuous function view of latest data from module
		 */
		ContinuousFunctionExpression<Time, Dimensionless> latestContinuousFunction = new ContinuousFunctionExpression<>(
				acquisitionModule.getSampleTimeUnits(), acquisitionModule.getSampleIntensityUnits());
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
}
