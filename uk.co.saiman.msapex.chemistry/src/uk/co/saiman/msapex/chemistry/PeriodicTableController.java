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

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import uk.co.saiman.chemistry.PeriodicTable;

/**
 * A JavaFX UI component for display of a {@link PeriodicTable}.
 * 
 * @author Elias N Vasylenko
 */
public class PeriodicTableController {
	private PeriodicTable periodicTable;

	@FXML
	private GridPane elementGrid;

	/**
	 * @param table
	 *          The periodic table to be displayed
	 */
	public void setPeriodicTable(PeriodicTable table) {
		int element = 1;
		for (Node node : elementGrid.getChildren()) {
			((ChemicalElementTile) node).setElement(table.getElement(element++));
		}
	}

	/**
	 * @return The periodic table currently displayed, or null if none is selected
	 */
	public PeriodicTable getPeriodicTable() {
		return periodicTable;
	}
}
