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
package uk.co.saiman.chemistry.msapex;

import static javafx.scene.layout.GridPane.getColumnIndex;
import static javafx.scene.layout.GridPane.getRowIndex;
import static uk.co.saiman.chemistry.msapex.ChemicalElementTile.Size.NORMAL;
import static uk.co.saiman.chemistry.msapex.ChemicalElementTile.Size.SMALL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import uk.co.saiman.chemistry.Element;
import uk.co.saiman.chemistry.PeriodicTable;
import uk.co.saiman.chemistry.msapex.ChemicalElementTile.Size;
import uk.co.strangeskies.utilities.Observable;
import uk.co.strangeskies.utilities.ObservableImpl;

/**
 * A JavaFX UI component for display of a {@link PeriodicTable}.
 * 
 * @author Elias N Vasylenko
 */
public class PeriodicTableController implements Observable<ChemicalElementTile> {
	private ObservableImpl<ChemicalElementTile> selectionObservable = new ObservableImpl<>();

	private PeriodicTable periodicTable;

	@FXML
	private GridPane elementGrid;
	private List<ChemicalElementTile> elementTiles;

	private Property<Size> tileSizeProperty;
	private Property<Boolean> tilesFocusableProperty;

	private ChemicalElementTile selectedElementTile;

	@FXML
	void initialize() {
		elementTiles = new ArrayList<>();
		for (Node node : elementGrid.getChildren()) {
			elementTiles.add((ChemicalElementTile) node);
		}
		for (ChemicalElementTile tile : elementTiles) {
			tile.addObserver(selectionObservable::fire);
		}

		tileSizeProperty = new SimpleObjectProperty<>(SMALL);
		tilesFocusableProperty = new SimpleBooleanProperty();

		for (ChemicalElementTile tile : elementTiles) {
			tile.getSizeProperty().bind(tileSizeProperty);
			tile.focusTraversableProperty().bind(tilesFocusableProperty);
			tile.addObserver(c -> setSelectedElementTile(tile));
		}

		elementGrid.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);

		setTileSize(NORMAL);
		setTilesFocusTraversable(false);
	}

	/**
	 * Get the element tile for the given element within this periodic table.
	 * 
	 * @param element
	 *          the element whose tile we wish to determine
	 * @return the tile in the table for the given element
	 * @throws IllegalArgumentException
	 *           if the element is not in the table
	 */
	public ChemicalElementTile getElementTile(Element element) {
		for (ChemicalElementTile tile : elementTiles) {
			if (tile.getElement().equals(element)) {
				return tile;
			}
		}
		throw new IllegalArgumentException("Element " + element + " not contained within table");
	}

	/**
	 * @return a list of all element tiles in order of atomic number
	 */
	public List<ChemicalElementTile> getElementTiles() {
		return Collections.unmodifiableList(elementTiles);
	}

	/**
	 * @return Get the currently focused element in the table, or the last one to
	 *         be selected via {@link ChemicalElementTile#select()}, or clicking,
	 *         if focusing is not enabled for the table
	 */
	public Element getSelectedElement() {
		return getSelectedElementTile().getElement();
	}

	private ChemicalElementTile getSelectedElementTile() {
		return selectedElementTile;
	}

	private void setSelectedElementTile(ChemicalElementTile tile) {
		selectedElementTile = tile;
	}

	private void onKeyPressed(KeyEvent event) {
		ChemicalElementTile selectedTile = getSelectedElementTile();
		int selectedColumn = getColumnIndex(selectedTile);
		int selectedRow = getRowIndex(selectedTile);

		if (selectedTile != null) {
			ChemicalElementTile closest = null;
			int best = Integer.MAX_VALUE;

			for (ChemicalElementTile tile : elementTiles) {
				int columnDistance = getColumnIndex(tile) - selectedColumn;
				int rowDistance = getRowIndex(tile) - selectedRow;

				switch (event.getCode()) {
				case UP:
					if (columnDistance == 0 && rowDistance < 0 && -rowDistance < best) {
						closest = tile;
						best = -rowDistance;
					}
					break;
				case DOWN:
					if (columnDistance == 0 && rowDistance > 0 && rowDistance < best) {
						closest = tile;
						best = rowDistance;
					}
					break;
				case LEFT:
					if (rowDistance == 0 && columnDistance < 0 && -columnDistance < best) {
						closest = tile;
						best = -columnDistance;
					}
					break;
				case RIGHT:
					if (rowDistance == 0 && columnDistance > 0 && columnDistance < best) {
						closest = tile;
						best = columnDistance;
					}
					break;
				default:
					return;
				}
			}

			if (closest != null) {
				closest.select();
			}

			event.consume();
		}
	}

	/**
	 * @param table
	 *          The periodic table to be displayed
	 */
	public void setPeriodicTable(PeriodicTable table) {
		int element = 1;
		for (ChemicalElementTile tile : elementTiles) {
			tile.setElement(table.getElement(element++));
		}
	}

	/**
	 * @return The periodic table currently displayed, or null if none is selected
	 */
	public PeriodicTable getPeriodicTable() {
		return periodicTable;
	}

	/**
	 * @return the current size of the element tiles
	 */
	public Size getTileSize() {
		return tileSizeProperty.getValue();
	}

	/**
	 * @param size
	 *          a new size for the element tiles
	 */
	public void setTileSize(Size size) {
		this.tileSizeProperty.setValue(size);
	}

	/**
	 * @return a property over the size of the element tiles
	 */
	public Property<Size> getTileSizeProperty() {
		return tileSizeProperty;
	}

	/**
	 * @return the current focusability value of the element tiles
	 */
	public boolean getTilesFocusTraversable() {
		return tilesFocusableProperty.getValue();
	}

	/**
	 * @param focusable
	 *          the new focusability value for the element tiles
	 */
	public void setTilesFocusTraversable(boolean focusable) {
		this.tilesFocusableProperty.setValue(focusable);
	}

	/**
	 * @return a property over the focusability of the element tiles
	 */
	public Property<Boolean> getTilesFocusTraversableProperty() {
		return tilesFocusableProperty;
	}

	@Override
	public boolean addObserver(Consumer<? super ChemicalElementTile> observer) {
		return selectionObservable.addObserver(observer);
	}

	@Override
	public boolean removeExactObserver(Consumer<? super ChemicalElementTile> observer) {
		return selectionObservable.removeExactObserver(observer);
	}
}
