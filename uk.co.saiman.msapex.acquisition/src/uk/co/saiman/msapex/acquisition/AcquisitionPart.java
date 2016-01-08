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
import org.eclipse.fx.core.di.LocalInstance;
import org.eclipse.fx.core.di.Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import uk.co.saiman.eclipse.FXUtilities;
import uk.co.saiman.instrument.acquisition.AcquisitionModule;
import uk.co.saiman.msapex.data.DataChartController;

public class AcquisitionPart {
	@Inject
	IEclipseContext context;

	private ObservableSet<AcquisitionModule> selectedModules = FXCollections.observableSet();
	private Map<AcquisitionModule, DataChartController> controllers = new HashMap<>();
	private Map<DataChartController, Pane> roots = new HashMap<>();

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
	void initialise(BorderPane pane, @LocalInstance FXMLLoader loader,
			@Service List<AcquisitionModule> acquisitionModules) {
		loader.setLocation(FXUtilities.getResource(DataChartController.class));

		TilePane chartPane = new TilePane();
		Text emptyText = new Text("No acquisition modules selected");

		selectedModules.addListener((SetChangeListener.Change<? extends AcquisitionModule> change) -> {
			Pane root;
			DataChartController controller;

			chartPane.setPrefColumns(selectedModules.size());
			if (change.wasAdded()) {
				try {
					loader.setRoot(null);
					loader.setController(null);
					root = loader.load();
					controller = loader.getController();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				controllers.put(change.getElementAdded(), controller);
				roots.put(controller, root);

				chartPane.getChildren().add(root);

				pane.setCenter(chartPane);
			} else if (change.wasRemoved()) {
				controller = controllers.remove(change.getElementRemoved());
				root = roots.remove(controller);

				chartPane.getChildren().remove(root);

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
