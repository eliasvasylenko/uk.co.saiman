/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.chemistry.msapex.
 *
 * uk.co.saiman.chemistry.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.chemistry.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.chemistry;

import javax.annotation.PostConstruct;

import org.eclipse.fx.core.di.LocalInstance;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import uk.co.saiman.chemistry.Element;
import uk.co.saiman.chemistry.PeriodicTable;
import uk.co.strangeskies.fx.FXUtilities;

/**
 * An Eclipse part for display of a periodic table.
 * 
 * @author Elias N Vasylenko
 */
public class PeriodicTablePart {
	@FXML
	private PeriodicTableController periodicTableController;

	@FXML
	private ChemicalElementPanelController chemicalElementPanelController;

	@FXML
	private ScrollPane periodicTableScrollPane;

	@PostConstruct
	void initialise(BorderPane container, @LocalInstance FXMLLoader loader) {
		container.setCenter(FXUtilities.loadIntoController(loader, this));
		periodicTableController.addObserver(this::setElement);
		periodicTableController.setTilesFocusTraversable(true);
	}

	/**
	 * @return The controller of the periodic table UI component
	 */
	public PeriodicTableController getPeriodicTableController() {
		return periodicTableController;
	}

	/**
	 * @return the current periodic table
	 */
	public PeriodicTable getPeriodicTable() {
		return periodicTableController.getPeriodicTable();
	}

	/**
	 * @param periodicTable
	 *          the new periodic table
	 */
	public void setPeriodicTable(PeriodicTable periodicTable) {
		periodicTableController.setPeriodicTable(periodicTable);
		chemicalElementPanelController.setElement(periodicTable.getElement(1));
	}

	/**
	 * Send a click event to the tile.
	 * 
	 * @param event
	 *          The mouse event to apply to this tile
	 */
	public void onMousePressed(MouseEvent event) {
		periodicTableScrollPane.requestFocus();
		event.consume();
	}

	private void setElement(Element element) {
		double hPadding = 2;
		double vPadding = 2;

		chemicalElementPanelController.setElement(element);
		ChemicalElementTile tile = periodicTableController.getElementTile(element);

		Bounds contentBounds = periodicTableScrollPane.getContent().getBoundsInLocal();
		Bounds viewportBounds = periodicTableScrollPane.getViewportBounds();

		Bounds location = tile.getBoundsInLocal();
		Node parent = tile;
		do {
			location = parent.getLocalToParentTransform().transform(location);
			parent = parent.getParent();
		} while (parent != periodicTableScrollPane.getContent());

		double scrollableDistanceH = contentBounds.getWidth() - viewportBounds.getWidth();
		double scrollableDistanceV = contentBounds.getHeight() - viewportBounds.getHeight();

		viewportBounds = new BoundingBox(scrollableDistanceH * periodicTableScrollPane.getHvalue(),
				scrollableDistanceV * periodicTableScrollPane.getVvalue(), viewportBounds.getWidth(),
				viewportBounds.getHeight());

		double scrollPosition;

		if (location.getMinX() - hPadding < viewportBounds.getMinX()) {
			scrollPosition = (location.getMinX() - hPadding);
			periodicTableScrollPane.setHvalue(scrollPosition / scrollableDistanceH);

		} else if (location.getMaxX() > viewportBounds.getMaxX()) {
			scrollPosition = (location.getMaxX() + hPadding - viewportBounds.getWidth());
			periodicTableScrollPane.setHvalue(scrollPosition / scrollableDistanceH);
		}

		if (location.getMinY() < viewportBounds.getMinY()) {
			scrollPosition = (location.getMinY() - vPadding);
			periodicTableScrollPane.setVvalue(scrollPosition / scrollableDistanceV);

		} else if (location.getMaxY() > viewportBounds.getMaxY()) {
			scrollPosition = (location.getMaxY() + vPadding - viewportBounds.getHeight());
			periodicTableScrollPane.setVvalue(scrollPosition / scrollableDistanceV);
		}
	}
}
