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

import java.io.IOException;
import java.util.Objects;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.NumberStringConverter;
import uk.co.saiman.chemistry.Element;

/**
 * @author Elias N Vasylenko
 */
public class ChemicalElementTile extends BorderPane {
	private Element element;

	@FXML
	private Label atomicNumberText;
	private IntegerProperty atomicNumber = new SimpleIntegerProperty();

	@FXML
	private Label symbolNameText;

	@FXML
	private Label nameText;

	public ChemicalElementTile() {
		getStyleClass().add("ChemicalElementTile");

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChemicalElementTile.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		Bindings.bindBidirectional(atomicNumberText.textProperty(), atomicNumber, new NumberStringConverter());
	}

	public void setElement(Element element) {
		if (this.element == null || element == null || !Objects.equals(this.element.getGroup(), element.getGroup())) {
			if (this.element != null) {
				pseudoClassStateChanged(PseudoClass.getPseudoClass(this.element.getGroup().name()), false);
			}
			if (element != null) {
				pseudoClassStateChanged(PseudoClass.getPseudoClass(element.getGroup().name()), true);
			}
		}

		this.element = element;

		atomicNumber.set(element.getAtomicNumber());
		symbolNameText.setText(element.getSymbol());
		nameText.setText(element.getName());
	}

	public Element getElement() {
		return element;
	}

	public void onMouseClicked(MouseEvent event) {
		System.out.println("halp");
	}
}
