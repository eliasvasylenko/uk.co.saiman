/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.msapex.chemistry.
 *
 * uk.co.saiman.msapex.chemistry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.chemistry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.chemistry;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.fx.core.di.LocalInstance;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import uk.co.saiman.chemistry.PeriodicTable;
import uk.co.strangeskies.eclipse.ObservableService;
import uk.co.strangeskies.fx.FXUtilities;

public class PeriodicTableController {
	@Inject
	IEclipseContext context;

	@ObservableService
	@Inject
	ObservableList<PeriodicTable> periodicTables;

	@FXML
	private GridPane elementGrid;

	// @FXML
	// private ContinuousFunctionChartController chartPane;

	@PostConstruct
	void initialise(BorderPane container, @LocalInstance FXMLLoader loader) {
		try {
			container.setCenter(FXUtilities.loadIntoController(loader, this));

			periodicTables.addListener((ListChangeListener<? super PeriodicTable>) change -> {
				if (change.wasAdded() && change.getAddedSize() <= periodicTables.size()) {
					setPeriodicTable(change.getAddedSubList().get(0));
				}
			});

			if (periodicTables.size() > 0) {
				setPeriodicTable(periodicTables.get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setPeriodicTable(PeriodicTable table) {
		int element = 1;
		for (Node node : elementGrid.getChildren()) {
			((ChemicalElementTile) node).setElement(table.getElement(element++));
		}
	}
}
