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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.fx.core.di.Service;
import org.eclipse.fx.ui.di.FXMLLoader;
import org.eclipse.fx.ui.di.FXMLLoaderFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import uk.co.saiman.instrument.acquisition.AcquisitionModule;
import uk.co.saiman.msapex.data.DataChartController;

public class AcquisitionPart {
	@Inject
	IEclipseContext context;

	private ObservableSet<AcquisitionModule> selectedModules;
	private Map<AcquisitionModule, DataChartController> controllers;

	public boolean setAcquisitionModules(Collection<? extends AcquisitionModule> selectedModules) {
		return this.selectedModules.removeAll(selectedModules) | this.selectedModules.addAll(selectedModules);
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
	void initialise(BorderPane pane, @FXMLLoader FXMLLoaderFactory factory,
			@Service List<AcquisitionModule> acquisitionModules) {
		TilePane chartPane = new TilePane();
		Text emptyText = new Text("No acquisition modules selected");

		controllers = new HashMap<>();
		selectedModules = FXCollections.observableSet();
		selectedModules.addListener((SetChangeListener.Change<? extends AcquisitionModule> change) -> {
			DataChartController controller;

			chartPane.setPrefColumns(selectedModules.size());
			if (change.wasAdded()) {
				try {
					controller = factory.loadRequestorRelative(DataChartController.FXML)
							.<DataChartController> loadWithController().getController();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				controllers.put(change.getElementAdded(), controller);

				chartPane.getChildren().add(controller.getRootPane());

				pane.setCenter(chartPane);
			} else if (change.wasRemoved()) {
				controller = controllers.remove(change.getElementRemoved());

				chartPane.getChildren().remove(controller.getRootPane());

				if (selectedModules.isEmpty()) {
					pane.setCenter(emptyText);
				}
			}
		});

		pane.setCenter(emptyText);

		setAcquisitionModules(acquisitionModules);
	}

	@Focus
	void focus() {}
}
