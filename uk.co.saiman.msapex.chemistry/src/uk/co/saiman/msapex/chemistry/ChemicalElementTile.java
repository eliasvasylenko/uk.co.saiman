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

import static uk.co.saiman.msapex.chemistry.ChemicalElementTile.Size.NORMAL;
import static uk.co.saiman.msapex.chemistry.ChemicalElementTile.Size.SMALL;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.NumberStringConverter;
import uk.co.saiman.chemistry.Element;
import uk.co.saiman.chemistry.Element.Group;
import uk.co.strangeskies.utilities.Observable;
import uk.co.strangeskies.utilities.ObservableImpl;

/**
 * A clickable UI node for displaying a chemical element. Typically for use in a
 * periodic table, as per {@link PeriodicTableController}, but also usable in
 * other contexts. Themable via css by pseudo classes for each {@link Group}.
 * 
 * @author Elias N Vasylenko
 */
public class ChemicalElementTile extends BorderPane implements Observable<Element> {
	/**
	 * The size of the tile. Smaller sizes may choose to present less information
	 * in order to take less space.
	 * 
	 * @author Elias N Vasylenko
	 */
	public enum Size {
		/**
		 * Smaller, with less information.
		 */
		SMALL,
		/**
		 * Normal size.
		 */
		NORMAL,
		/**
		 * Larger, with more information.
		 */
		LARGE
	}

	private ObservableImpl<Element> clickObservable = new ObservableImpl<>();

	private Element element;

	@FXML
	private Label atomicNumberText;
	private IntegerProperty atomicNumber = new SimpleIntegerProperty();

	@FXML
	private Label symbolNameText;

	@FXML
	private Label nameText;

	private Property<Size> sizeProperty;

	/**
	 * Create a chemical element tile with no element.
	 */
	public ChemicalElementTile() {
		this(null);
	}

	/**
	 * Create a chemical element tile for the given element.
	 * 
	 * @param element
	 *          The element for the tile to display, or null for an empty tile
	 */
	public ChemicalElementTile(Element element) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChemicalElementTile.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		Bindings.bindBidirectional(atomicNumberText.textProperty(), atomicNumber, new NumberStringConverter());

		setElement(element);

		sizeProperty = new SimpleObjectProperty<>(SMALL);
		sizeProperty.addListener(c -> {
			pseudoClassStateChanged(PseudoClass.getPseudoClass(getSize().name()), true);

			for (Size other : Size.values()) {
				if (other != getSize()) {
					pseudoClassStateChanged(PseudoClass.getPseudoClass(other.name()), false);
				}
			}
		});

		setSize(NORMAL);
	}

	@Override
	public void requestFocus() {
		if (isFocusTraversable()) {
			super.requestFocus();
		}
	}

	/**
	 * @return the current size of the tile
	 */
	public Size getSize() {
		return sizeProperty.getValue();
	}

	/**
	 * @param size
	 *          a new size for the tile
	 */
	public void setSize(Size size) {
		this.sizeProperty.setValue(size);
	}

	/**
	 * @return a property over the size of the tile
	 */
	public Property<Size> getSizeProperty() {
		return sizeProperty;
	}

	/**
	 * Change the element displayed by the tile.
	 * 
	 * @param element
	 *          The element for the tile to display, or null for an empty tile
	 */
	public void setElement(Element element) {
		if (element != null) {
			setGroup(element.getGroup());

			atomicNumber.set(element.getAtomicNumber());
			symbolNameText.setText(element.getSymbol());
			nameText.setText(element.getName());
		} else {
			setGroup(Group.NONE);

			atomicNumberText.setText("");
			symbolNameText.setText("?");
			nameText.setText("Unknown");
		}

		this.element = element;
	}

	private void setGroup(Group group) {
		if (this.element == null || !Objects.equals(this.element.getGroup(), group)) {
			if (this.element != null) {
				pseudoClassStateChanged(PseudoClass.getPseudoClass(this.element.getGroup().name()), false);
			}
			pseudoClassStateChanged(PseudoClass.getPseudoClass(group.name()), true);
		}
	}

	/**
	 * @return The element displayed by the tile
	 */
	public Element getElement() {
		return element;
	}

	/**
	 * Send a click event to the tile.
	 * 
	 * @param event
	 *          The mouse event to apply to this tile
	 */
	public void onMousePressed(MouseEvent event) {
		select();
		event.consume();
	}

	/**
	 * Select the tile, and focus if focusable
	 */
	public void select() {
		requestFocus();
		clickObservable.fire(element);
	}

	@Override
	public boolean addObserver(Consumer<? super Element> observer) {
		return clickObservable.addObserver(observer);
	}

	@Override
	public boolean removeObserver(Consumer<? super Element> observer) {
		return clickObservable.removeObserver(observer);
	}
}
