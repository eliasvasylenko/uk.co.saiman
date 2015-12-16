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
package uk.co.saiman.chemistry.isotope;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.co.saiman.chemistry.Element;
import uk.co.saiman.chemistry.ChemicalComposition;
import uk.co.saiman.chemistry.PeriodicTable;
import uk.co.strangeskies.mathematics.Range;

public class MoleculeCompositionConstraint {
	private TreeMap<Element, Range<Integer>> elementConstraints; // possible
																																// amounts of
	// each of a set of
	// elements
	private TreeMap<Isotope, Range<Integer>> isotopeConstraints; // possible
																																// amounts of
	// each of a set of
	// specific isotopes
	private Range<Integer> unknownConstraint; // possible amount of unknown
																						// elements
	private Range<Double> averageMassConstraint; // possible range for average
																								// mass
	private Range<Double> monoisotopicMassConstraint; // possible range for
																										// monoisotopic

	// elements
	private final PeriodicTable periodicTable;

	// elements by reverse mass for speed -
	// sort elements by those more likely to fail first
	private SortedSet<Element> elementsHeaviestFirst;

	public MoleculeCompositionConstraint() {
		this.periodicTable = null; // cannot set unknown constraints with null
																// periodicTable
		initialise();
	}

	public MoleculeCompositionConstraint(PeriodicTable periodicTable) {
		this.periodicTable = periodicTable;
		initialise();
	}

	public TreeMap<Element, Range<Integer>> getElementConstraints() {
		return elementConstraints;
	}

	public TreeMap<Isotope, Range<Integer>> getIsotopeConstraints() {
		return isotopeConstraints;
	}

	public Range<Integer> getUnknownConstraint() {
		if (periodicTable == null) {
			return null;
		}
		return unknownConstraint;
	}

	public void setUnknownConstraint(Range<Integer> range) {
		if (periodicTable != null) {
			unknownConstraint = range;
		}
	}

	public Range<Double> getAverageMassConstraint() {
		return averageMassConstraint;
	}

	public void setAverageMassConstraint(Range<Double> range) {
		averageMassConstraint = range;
	}

	public Range<Double> getMonoisotopicMassConstraint() {
		return monoisotopicMassConstraint;
	}

	public void setMonoisotopicMassConstraint(Range<Double> range) {
		monoisotopicMassConstraint = range;
	}

	private void initialise() {
		elementConstraints = new TreeMap<Element, Range<Integer>>();
		isotopeConstraints = new TreeMap<Isotope, Range<Integer>>();
		unknownConstraint = Range.between(0, 0);
		averageMassConstraint = Range.<Double> between(null, null);
		monoisotopicMassConstraint = Range.<Double> between(null, null);
		elementsHeaviestFirst = new TreeSet<Element>(new Comparator<Element>() {
			@Override
			public int compare(Element first, Element second) {
				if (first.getMonoisotopicMass() == second.getMonoisotopicMass()) {
					return 0;
				}
				if (first.getMonoisotopicMass() > second.getMonoisotopicMass()) {
					return 1;
				}
				return -1;
			}
		});
		elementsHeaviestFirst.addAll(periodicTable.getElements());
	}

	public Set<ChemicalComposition> getConformingMolecules() {
		if (averageMassConstraint.getTo() != null && averageMassConstraint.getTo() == 0) {
			averageMassConstraint = Range.<Double> between(null, null);
		}
		if (monoisotopicMassConstraint.getTo() != null && monoisotopicMassConstraint.getTo() == 0) {
			monoisotopicMassConstraint = Range.<Double> between(null, null);
		}

		ChemicalComposition molecule = new ChemicalComposition();

		Iterator<Element> elementIterator = elementConstraints.keySet().iterator();
		Element element;
		while (elementIterator.hasNext()) {
			element = elementIterator.next();

			int elementMinimum = 0;
			Range<Integer> elementRange = elementConstraints.get(element);
			if (!elementRange.isFromUnbounded()) {
				elementMinimum = elementRange.getFrom();
				if (!elementRange.isFromInclusive()) {
					elementMinimum++;
				}

				if (elementMinimum > 0) {
					molecule.withElement(element, elementMinimum);
					if (averageMassConstraint.isValueBelow(molecule.getAverageMass())) {
						return new HashSet<ChemicalComposition>();
					}
					if (monoisotopicMassConstraint.isValueBelow(molecule.getMonoisotopicMass())) {
						System.out.println("min: " + elementMinimum);
						return new HashSet<ChemicalComposition>();
					}
				}
			}
		}

		Iterator<Isotope> isotopeIterator = isotopeConstraints.keySet().iterator();
		Isotope isotope;
		while (isotopeIterator.hasNext()) {
			isotope = isotopeIterator.next();

			int isotopeMinimum = 0;
			Range<Integer> isotopeRange = isotopeConstraints.get(isotope);
			if (!isotopeRange.isFromUnbounded()) {
				isotopeMinimum = isotopeRange.getFrom();
				if (!isotopeRange.isFromInclusive()) {
					isotopeMinimum++;
				}

				if (isotopeMinimum > 0) {
					molecule.withIsotope(isotope, isotopeMinimum);
				}
			}
		}

		return conformingMoleculeRecursion(molecule, 0, null, 0);
	}

	private Set<ChemicalComposition> conformingMoleculeRecursion(ChemicalComposition molecule, int unknown, Element previousElement,
			int previousIsotope) {
		ChemicalComposition nextMolecule;

		Set<ChemicalComposition> possibleMoleculesSet = new HashSet<ChemicalComposition>();

		if (averageMassConstraint.isValueBelow(molecule.getAverageMass())) {
			return possibleMoleculesSet;
		}
		if (monoisotopicMassConstraint.isValueBelow(molecule.getMonoisotopicMass())) {
			return possibleMoleculesSet;
		}

		if (previousElement == null) {
			previousElement = elementsHeaviestFirst.first();
		}

		if (unknown >= 0 && (!unknownConstraint.isValueBelow(unknown + 1))) {
			Iterator<Element> elementIterator = elementsHeaviestFirst.tailSet(previousElement).iterator();
			Element element;
			while (elementIterator.hasNext()) {
				element = elementIterator.next();
				if (!elementConstraints.containsKey(element)) {
					nextMolecule = molecule;
					nextMolecule.withElement(element);
					possibleMoleculesSet.addAll(conformingMoleculeRecursion(nextMolecule, unknown + 1, element, 0));
				}
			}
		}
		if (unknown == -1 || unknownConstraint.contains(unknown)) {
			if (previousIsotope == 0) {
				Iterator<Element> elementIterator;
				if (unknown == -1) {
					elementIterator = ((SortedSet<Element>) elementConstraints.keySet()).tailSet(previousElement).iterator();
				} else {
					elementIterator = ((SortedSet<Element>) elementConstraints.keySet()).iterator();
				}
				Element element;
				while (elementIterator.hasNext()) {
					element = elementIterator.next();
					nextMolecule = molecule;
					nextMolecule.withElement(element);
					if (elementConstraints.get(element).contains(nextMolecule.elementCount(element))) {
						possibleMoleculesSet.addAll(conformingMoleculeRecursion(nextMolecule, -1, element, 0));
					}
				}
			}

			Iterator<Isotope> isotopeIterator;
			if (previousIsotope == 0) {
				isotopeIterator = ((SortedSet<Isotope>) getIsotopeConstraints().keySet()).iterator();
			} else {
				isotopeIterator = ((SortedSet<Isotope>) getIsotopeConstraints().keySet())
						.tailSet(previousElement.getIsotope(previousIsotope)).iterator();
			}
			Isotope isotope;
			while (isotopeIterator.hasNext()) {
				isotope = isotopeIterator.next();
				nextMolecule = molecule;
				nextMolecule.withIsotope(isotope);
				if (getIsotopeConstraints().get(isotope).contains(nextMolecule.isotopeCount(isotope))) {
					possibleMoleculesSet
							.addAll(conformingMoleculeRecursion(nextMolecule, -1, isotope.getElement(), isotope.getMassNumber()));
				}
			}
		}

		if (averageMassConstraint.contains(molecule.getAverageMass())
				&& monoisotopicMassConstraint.contains(molecule.getMonoisotopicMass())) {
			possibleMoleculesSet.add(molecule);
		}

		return possibleMoleculesSet;
	}
}
