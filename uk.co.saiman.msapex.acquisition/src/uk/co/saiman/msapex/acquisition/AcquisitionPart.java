/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.msapex.acquisition.
 *
 * uk.co.saiman.msapex.acquisition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.acquisition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.acquisition;

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
import uk.co.saiman.data.ContinuumExpression;
import uk.co.saiman.data.SimpleRegularSampledContinuum;
import uk.co.saiman.eclipse.FXUtilities;
import uk.co.saiman.instrument.acquisition.AcquisitionModule;
import uk.co.saiman.msapex.data.ContinuumChartController;

public class AcquisitionPart {
	@Inject
	IEclipseContext context;

	@FXML
	private Pane chartPane;

	@FXML
	private Label noSelectionLabel;

	private ObservableSet<AcquisitionModule> selectedModules = FXCollections.observableSet();
	private Map<AcquisitionModule, ContinuumChartController> controllers = new HashMap<>();

	public boolean setAcquisitionModules(Collection<? extends AcquisitionModule> selectedModules) {
		return this.selectedModules.retainAll(selectedModules) | this.selectedModules.addAll(selectedModules);
	}

	public boolean addAcquisitionModule(AcquisitionModule module) {
		return selectedModules.add(module);
	}

	public boolean removeAcquisitionModule(AcquisitionModule module) {
		return selectedModules.remove(module);
	}

	public Set<AcquisitionModule> getSelectedAcquisitionModules() {
		return new HashSet<>(selectedModules);
	}

	@PostConstruct
	void initialise(BorderPane container, @LocalInstance FXMLLoader loader) {
		container.setCenter(FXUtilities.loadIntoController(loader, this));

		selectedModules.addListener((SetChangeListener.Change<? extends AcquisitionModule> change) -> {
			if (change.wasAdded()) {
				selectAcquisitionModule(loader, change.getElementAdded());
			} else if (change.wasRemoved()) {
				deselectAcquisitionModule(change.getElementRemoved());
			}
		});
	}

	private void selectAcquisitionModule(FXMLLoader loader, AcquisitionModule acquisitionModule) {
		noSelectionLabel.setVisible(false);

		/*
		 * New chart controller for module
		 */
		ContinuumChartController chartController = FXUtilities.loadController(loader, ContinuumChartController.class);
		chartController.setTitle(acquisitionModule.getName());
		controllers.put(acquisitionModule, chartController);
		chartPane.getChildren().add(chartController.getRoot());
		HBox.setHgrow(chartController.getRoot(), Priority.ALWAYS);

		/*
		 * Create continuum view of latest data from module
		 */
		ContinuumExpression latestContinuum = new ContinuumExpression(
				new SimpleRegularSampledContinuum(1, new double[] {}));
		acquisitionModule.continuumEvents().addWeakObserver(latestContinuum, l -> c -> l.setComponent(c));

		/*
		 * Add latest data to chart controller
		 */
		chartController.getContinuums().add(latestContinuum);
	}

	private void deselectAcquisitionModule(AcquisitionModule acquisitionModule) {
		ContinuumChartController controller = controllers.remove(acquisitionModule);

		chartPane.getChildren().remove(controller.getRoot());

		noSelectionLabel.setVisible(chartPane.getChildren().isEmpty());
	}

	@Focus
	void focus() {}
}
