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
