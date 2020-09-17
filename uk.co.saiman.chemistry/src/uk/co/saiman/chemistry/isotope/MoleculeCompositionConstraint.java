/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
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

import uk.co.saiman.chemistry.ChemicalComposition;
import uk.co.saiman.chemistry.Element;
import uk.co.saiman.chemistry.Isotope;
import uk.co.saiman.chemistry.PeriodicTable;
import uk.co.saiman.mathematics.Interval;

public class MoleculeCompositionConstraint {
  private TreeMap<Element, Interval<Integer>> elementConstraints; // possible
  // amounts of
  // each of a set of
  // elements
  private TreeMap<Isotope, Interval<Integer>> isotopeConstraints; // possible
  // amounts of
  // each of a set of
  // specific isotopes
  private Interval<Integer> unknownConstraint; // possible amount of unknown
  // elements
  private Interval<Double> averageMassConstraint; // possible Interval for
                                                  // average
  // mass
  private Interval<Double> monoisotopicMassConstraint; // possible Interval for
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

  public TreeMap<Element, Interval<Integer>> getElementConstraints() {
    return elementConstraints;
  }

  public TreeMap<Isotope, Interval<Integer>> getIsotopeConstraints() {
    return isotopeConstraints;
  }

  public Interval<Integer> getUnknownConstraint() {
    if (periodicTable == null) {
      return null;
    }
    return unknownConstraint;
  }

  public void setUnknownConstraint(Interval<Integer> Interval) {
    if (periodicTable != null) {
      unknownConstraint = Interval;
    }
  }

  public Interval<Double> getAverageMassConstraint() {
    return averageMassConstraint;
  }

  public void setAverageMassConstraint(Interval<Double> Interval) {
    averageMassConstraint = Interval;
  }

  public Interval<Double> getMonoisotopicMassConstraint() {
    return monoisotopicMassConstraint;
  }

  public void setMonoisotopicMassConstraint(Interval<Double> Interval) {
    monoisotopicMassConstraint = Interval;
  }

  private void initialise() {
    elementConstraints = new TreeMap<>();
    isotopeConstraints = new TreeMap<>();
    unknownConstraint = Interval.bounded(0, 0);
    averageMassConstraint = Interval.unbounded();
    monoisotopicMassConstraint = Interval.unbounded();
    elementsHeaviestFirst = new TreeSet<>(new Comparator<Element>() {
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
    if (averageMassConstraint.getRightEndpoint() != null
        && averageMassConstraint.getRightEndpoint() == 0) {
      averageMassConstraint = Interval.unbounded();
    }
    if (monoisotopicMassConstraint.getRightEndpoint() != null
        && monoisotopicMassConstraint.getRightEndpoint() == 0) {
      monoisotopicMassConstraint = Interval.unbounded();
    }

    ChemicalComposition molecule = ChemicalComposition.nothing();

    Iterator<Element> elementIterator = elementConstraints.keySet().iterator();
    Element element;
    while (elementIterator.hasNext()) {
      element = elementIterator.next();

      int elementMinimum = 0;
      Interval<Integer> elementInterval = elementConstraints.get(element);
      if (!elementInterval.isLeftUnbounded()) {
        elementMinimum = elementInterval.getLeftEndpoint();
        if (!elementInterval.isLeftClosed()) {
          elementMinimum++;
        }

        if (elementMinimum > 0) {
          molecule.withElement(element, elementMinimum);
          if (averageMassConstraint.isValueBelow(molecule.getAverageMass())) {
            return new HashSet<>();
          }
          if (monoisotopicMassConstraint.isValueBelow(molecule.getMonoisotopicMass())) {
            return new HashSet<>();
          }
        }
      }
    }

    Iterator<Isotope> isotopeIterator = isotopeConstraints.keySet().iterator();
    Isotope isotope;
    while (isotopeIterator.hasNext()) {
      isotope = isotopeIterator.next();

      int isotopeMinimum = 0;
      Interval<Integer> isotopeInterval = isotopeConstraints.get(isotope);
      if (!isotopeInterval.isLeftUnbounded()) {
        isotopeMinimum = isotopeInterval.getLeftEndpoint();
        if (!isotopeInterval.isLeftClosed()) {
          isotopeMinimum++;
        }

        if (isotopeMinimum > 0) {
          molecule.withIsotope(isotope, isotopeMinimum);
        }
      }
    }

    return conformingMoleculeRecursion(molecule, 0, null, 0);
  }

  private Set<ChemicalComposition> conformingMoleculeRecursion(
      ChemicalComposition molecule,
      int unknown,
      Element previousElement,
      int previousIsotope) {
    ChemicalComposition nextMolecule;

    Set<ChemicalComposition> possibleMoleculesSet = new HashSet<>();

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
          possibleMoleculesSet
              .addAll(conformingMoleculeRecursion(nextMolecule, unknown + 1, element, 0));
        }
      }
    }
    if (unknown == -1 || unknownConstraint.contains(unknown)) {
      if (previousIsotope == 0) {
        Iterator<Element> elementIterator;
        if (unknown == -1) {
          elementIterator = ((SortedSet<Element>) elementConstraints.keySet())
              .tailSet(previousElement)
              .iterator();
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
            .tailSet(previousElement.getIsotope(previousIsotope))
            .iterator();
      }
      Isotope isotope;
      while (isotopeIterator.hasNext()) {
        isotope = isotopeIterator.next();
        nextMolecule = molecule;
        nextMolecule.withIsotope(isotope);
        if (getIsotopeConstraints().get(isotope).contains(nextMolecule.isotopeCount(isotope))) {
          possibleMoleculesSet
              .addAll(
                  conformingMoleculeRecursion(
                      nextMolecule,
                      -1,
                      isotope.getElement(),
                      isotope.getMassNumber()));
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
