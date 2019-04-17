/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
package uk.co.saiman.chemistry;

import static uk.co.saiman.mathematics.FloatingPointUtilities.unitInTheLastPlaceAbove;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class Element implements Comparable<Element> {
  private final int atomicNumber;
  private final String name;
  private final String symbol;
  private final TreeSet<Isotope> isotopes;
  private final Group group;

  /**
   * The group, or family, of the element on the periodic table.
   * 
   * @author Elias N Vasylenko
   */
  @SuppressWarnings("javadoc")
  public enum Group {
    NONE("None"),
    ALKALI("Alkali"),
    ALKALINE("Alkaline"),
    LANTHANIDE("Lanthanide"),
    ACTINIDE("Actinide"),
    TRANSITION("Transition"),
    POOR_METAL("Poor Metal"),
    METALLOID("Metalloid"),
    NONMETAL("Nonmetal"),
    HALOGEN("Halogen"),
    NOBLE_GAS("Noble Gas");

    private final String name;

    Group(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }

    /**
     * Similar in function to {@link #valueOf(String)}, but looking up by given
     * name, i.e. the value of {@link #toString()}.
     * 
     * @param name
     *          The value of {@link #toString()} for the element group whose
     *          enum instance we wish to find.
     * @return The enum instance matching the given name.
     */
    public static Group valueOfName(String name) {
      Group[] categories = Group.values();
      for (int i = 0; i < categories.length; i++) {
        if (name.equals(categories[i].toString())) {
          return categories[i];
        }
      }
      return NONE;
    }
  }

  public Element() {
    name = "Unknown element";
    symbol = "Uk";
    atomicNumber = 0;
    group = Element.Group.NONE;
    isotopes = new TreeSet<>();
  }

  public Element(double mass) {
    this();
    isotopes.add(new Isotope((int) mass, mass, 1, this));
  }

  private Element(
      int atomicNumber,
      String name,
      String symbol,
      Collection<? extends Isotope> isotopes,
      Group group) {
    this.atomicNumber = atomicNumber;
    this.name = name;
    this.symbol = symbol;
    this.isotopes = new TreeSet<>();
    for (Isotope isotope : isotopes) {
      this.isotopes.add(isotope.withElement(this));
    }
    this.group = group;
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
    TreeSet<Isotope> isotopes = new TreeSet<>(this.isotopes);
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
    TreeSet<Isotope> isotopes = new TreeSet<>();
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

  public Group getGroup() {
    return group;
  }

  public Element withAtomicNumber(int atomicNumber) {
    return new Element(atomicNumber, name, symbol, isotopes, group);
  }

  public Element withName(String name) {
    return new Element(atomicNumber, name, symbol, isotopes, group);
  }

  public Element withSymbol(String symbol) {
    return new Element(atomicNumber, name, symbol, isotopes, group);
  }

  public Element withIsotope(int massNumber, double mass, double abundance) {
    Set<Isotope> isotopes = new HashSet<>(this.isotopes);
    isotopes.add(new Isotope(massNumber, mass, abundance, this));
    return new Element(atomicNumber, name, symbol, isotopes, group);
  }

  public Element withGroup(Group group) {
    return new Element(atomicNumber, name, symbol, isotopes, group);
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
      errorBound += unitInTheLastPlaceAbove(abundance) * 2;
    }

    /*
     * if total abundance != %0 (no meaningful values / no natural occurrences)
     * and total abundance != %100 (within maximum margin of error) then give a
     * warning
     */
    if (totalAbundance == 0
        || (totalAbundance > 100 - errorBound && totalAbundance < 100 + errorBound)) {
      return true;
    }

    return false;
  }

  @Override
  public String toString() {
    String string = "";
    String newLine = System.getProperty("line.separator");

    string += "Element: " + name + newLine;
    string += "   - atomic number: " + atomicNumber + newLine;
    string += "   - symbol: " + symbol + newLine;

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
