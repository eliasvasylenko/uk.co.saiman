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
package uk.co.saiman.chemistry;

import java.util.Objects;

public class Isotope implements Comparable<Isotope> {
	private final Element element;
	private final int massNumber;
	private final double mass;
	private final double abundance;

	protected Isotope(int massNumber, double mass, double abundance, Element element) {
		this.massNumber = massNumber;
		this.mass = mass;
		this.abundance = abundance;
		this.element = element;
	}

	protected Isotope withElement(Element element) {
		return new Isotope(massNumber, mass, abundance, element);
	}

	public Element getElement() {
		return element;
	}

	public int getMassNumber() {
		return massNumber;
	}

	public double getMass() {
		return mass;
	}

	public double getAbundance() {
		return abundance;
	}

	@Override
	public String toString() {
		String string = "";
		String newLine = System.getProperty("line.separator");

		string += "  Isotope: " + massNumber + newLine;
		string += "     - mass: " + mass + newLine;
		string += "     - abundance: " + abundance + newLine;

		return string;
	}

	@Override
	public int compareTo(Isotope that) {
		if (this == that)
			return 0;

		return this.massNumber - that.massNumber;
	}

	@Override
	public boolean equals(Object that) {
		if (this == that)
			return true;

		if (!(that instanceof Isotope))
			return false;

		Isotope thatIsotope = (Isotope) that;

		return this.massNumber == thatIsotope.massNumber && Objects.equals(this.element, thatIsotope.element);
	}

	@Override
	public int hashCode() {
		return 23 + massNumber * (massNumber + 4);
	}
}
