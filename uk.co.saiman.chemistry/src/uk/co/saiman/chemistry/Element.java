/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import uk.co.saiman.chemistry.isotope.Isotope;
import uk.co.strangeskies.mathematics.values.DoubleValue;

public class Element implements Comparable<Element> {
	private final int atomicNumber;
	private final String name;
	private final String symbol;
	private final TreeSet<Isotope> isotopes;
	private final Category category;

	public enum Category {
		NONE("None"), ALKALI("Alkali"), ALKALINE("Alkaline"), LANTHANIDE("Lanthanide"), ACTINIDE("Actinide"), TRANSITION(
				"Transition"), POOR_METAL("Poor Metal"), METALLOID("Metalloid"), NONMETAL("Nonmetal"), HALOGEN(
						"Halogen"), NOBLE_GAS("Noble Gas");

		private final String name;

		Category(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		public static Category valueOfName(String name) {
			Category[] categories = Category.values();
			for (int i = 0; i < categories.length; i++) {
				if (name.equals(categories[i].toString())) {
					return categories[i];
				}
			}
			return NONE;
		}
	}

	public Element(double mass) {
		name = "Unknown element";
		symbol = "Uk";
		atomicNumber = 0;
		category = Element.Category.NONE;
		isotopes = new TreeSet<>();
		isotopes.add(new Isotope((int) mass, mass, 1, this));
	}

	public Element(Element element) {
		this.atomicNumber = element.atomicNumber;
		this.name = element.name;
		this.symbol = element.symbol;
		this.isotopes = new TreeSet<Isotope>(element.isotopes);
		this.category = element.category;
	}

	public Element(int atomicNumber, String name, String symbol, Collection<? extends Isotope> isotopes,
			Category category) {
		this.atomicNumber = atomicNumber;
		this.name = name;
		this.symbol = symbol;
		this.isotopes = new TreeSet<Isotope>(isotopes);
		this.category = category;
	}

	public int getAtomicNumber() {
		return atomicNumber;
	}

	public double getAverageMass() {
		double mass = 0;
		Iterator<Isotope> isotopeIterator = isotopes.iterator();
		Isotope isotope;

		if (isNaturallyOccurring()) {
			while (isotopeIterator.hasNext()) {
				isotope = isotopeIterator.next();
				mass += isotope.getMass() * isotope.getAbundance();
			}
			return mass;
		}

		while (isotopeIterator.hasNext()) {
			isotope = isotopeIterator.next();
			mass += isotope.getMass();
		}
		return mass / isotopes.size();
	}

	public double getMonoisotopicMass() {
		if (isotopes.isEmpty())
			return 0;

		Iterator<Isotope> isotopeIterator = isotopes.iterator();
		Isotope isotope;
		Isotope mostAbundant = isotopeIterator.next();

		while (isotopeIterator.hasNext()) {
			isotope = isotopeIterator.next();
			if (isotope.getAbundance() > mostAbundant.getAbundance()) {
				mostAbundant = isotope;
			}
		}

		return mostAbundant.getMass();
	}

	public String getName() {
		return name;
	}

	public String getSymbol() {
		return symbol;
	}

	public TreeSet<Isotope> getIsotopes() {
		TreeSet<Isotope> isotopes = new TreeSet<Isotope>(this.isotopes);
		return isotopes;
	}

	public Isotope getIsotope(int massNumber) {
		Iterator<Isotope> isotopeIterator = isotopes.iterator();
		Isotope isotope;
		while (isotopeIterator.hasNext()) {
			isotope = isotopeIterator.next();
			if (isotope.getMassNumber() == massNumber) {
				return isotope;
			}
		}
		return null;
	}

	public double getVariance() {
		double average = getAverageMass();
		double difference;
		double variance = 0;
		double totalAbundance = 0;

		Iterator<Isotope> isotopeIterator = isotopes.iterator();
		Isotope isotope;
		while (isotopeIterator.hasNext()) {
			isotope = isotopeIterator.next();
			difference = isotope.getMass() - average;
			variance += difference * difference * isotope.getAbundance();
			totalAbundance += isotope.getAbundance();
		}

		return variance / totalAbundance;
	}

	public TreeSet<Isotope> getNaturallyOccuringIsotopes() {
		TreeSet<Isotope> isotopes = new TreeSet<Isotope>();
		Iterator<Isotope> isotopeIterator = this.isotopes.iterator();
		Isotope isotope;
		while (isotopeIterator.hasNext()) {
			isotope = isotopeIterator.next();
			if (isotope.getAbundance() > 0) {
				isotopes.add(isotope);
			}
		}
		return isotopes;
	}

	public Category getCategory() {
		return category;
	}

	public Element withAtomicNumber(int atomicNumber) {
		return new Element(atomicNumber, name, symbol, isotopes, category);
	}

	public Element withName(String name) {
		return new Element(atomicNumber, name, symbol, isotopes, category);
	}

	public Element withSymbol(String symbol) {
		return new Element(atomicNumber, name, symbol, isotopes, category);
	}

	public Element withIsotopes(Collection<? extends Isotope> isotopes) {
		return new Element(atomicNumber, name, symbol, isotopes, category);
	}

	public Element withIsotope(Isotope isotope) {
		Set<Isotope> isotopes = new HashSet<>(this.isotopes);
		isotopes.add(isotope);
		return withIsotopes(isotopes);
	}

	public Element withCategory(Category category) {
		return new Element(atomicNumber, name, symbol, isotopes, category);
	}

	public boolean isNaturallyOccurring() {
		// is element naturally occurring

		Iterator<Isotope> isotopeIterator = isotopes.iterator();
		while (isotopeIterator.hasNext()) {
			if (isotopeIterator.next().getAbundance() > 0) {
				return true;
			}
		}

		return false;
	}

	public boolean isAbundanceValid() {
		// check element has a valid total abundance

		// sum of abundances of isotopes for this element
		double totalAbundance = 0;
		// upper margin of error for summer abundances
		double errorBound = 0;
		// abundance of current isotope
		double abundance;

		Iterator<Isotope> isotopeIterator = isotopes.iterator();
		while (isotopeIterator.hasNext()) {
			abundance = isotopeIterator.next().getAbundance();
			// if abundance < 0 then invalid
			if (abundance < 0) {
				return false;
			}

			totalAbundance += abundance;
			/*
			 * assume worst case: max rounding error is full ULP not half ULP.
			 * 
			 * rounding error of a value can then be doubled when added to another
			 * value with double the ULP and rounded again...
			 */
			errorBound += new DoubleValue(abundance).unitInTheLastPlaceLarger().doubleValue() * 2;
		}

		/*
		 * if total abundance != %0 (no meaningful values / no natural occurrences)
		 * and total abundance != %100 (within maximum margin of error) then give a
		 * warning
		 */
		if (totalAbundance == 0 || (totalAbundance > 100 - errorBound && totalAbundance < 100 + errorBound)) {
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		String string = "";
		String newLine = System.getProperty("line.separator");

		string += "Element: " + name + newLine;
		string += "  atomic number: " + atomicNumber + newLine;
		string += "  symbol: " + symbol + newLine;

		Iterator<Isotope> isotopeIterator = isotopes.iterator();
		while (isotopeIterator.hasNext()) {
			string += isotopeIterator.next();
		}

		return string;
	}

	@Override
	public int compareTo(Element that) {
		if (this == that) {
			return 0;
		}

		return this.atomicNumber - that.atomicNumber;
	}

	@Override
	public boolean equals(Object that) {
		if (this == that)
			return true;

		if (!(that instanceof Element))
			return false;

		Element thatElement = (Element) that;

		if (this.atomicNumber != thatElement.atomicNumber)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return 23 + atomicNumber * (atomicNumber + 4);
	}
}
