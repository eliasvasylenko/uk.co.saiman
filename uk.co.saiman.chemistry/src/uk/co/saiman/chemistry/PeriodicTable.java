/*
 * Copyright (C) 2015 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.chemistry.
 *
 * uk.co.saiman.chemistry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.chemistry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.chemistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Elias Vasylenko
 *
 */
public class PeriodicTable {
	private List<Element> elements;

	public PeriodicTable(Collection<? extends Element> elements) {
		this.elements = Collections.unmodifiableList(new ArrayList<>(elements));
	}

	public SortedSet<Element> getElements() {
		return new TreeSet<Element>(elements);
	}

	public Element getElement(int atomicNumber) {
		Iterator<Element> elementIterator = elements.iterator();
		Element element;
		while (elementIterator.hasNext()) {
			element = elementIterator.next();

			if (element.getAtomicNumber() == atomicNumber) {
				return element;
			}
		}

		return null;
	}

	public Element getFromSymbol(String symbol) {
		Iterator<Element> elementIterator = elements.iterator();
		Element element;
		while (elementIterator.hasNext()) {
			element = elementIterator.next();

			if (element.getSymbol() == symbol) {
				return element;
			}
		}

		return null;
	}

	public Element getFromName(String name) {
		Iterator<Element> elementIterator = elements.iterator();
		Element element;
		while (elementIterator.hasNext()) {
			element = elementIterator.next();

			if (element.getName() == name) {
				return element;
			}
		}

		return null;
	}

	@Override
	public String toString() {
		String string = "";
		String newLine = System.getProperty("line.separator");

		// list elements and isotopes
		Iterator<Element> elementIterator = elements.iterator();
		Element element;
		while (elementIterator.hasNext()) {
			element = elementIterator.next();

			string += element + newLine;
		}

		return string;
	}
}
