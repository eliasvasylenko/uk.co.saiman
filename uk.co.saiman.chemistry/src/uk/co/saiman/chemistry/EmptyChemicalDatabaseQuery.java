/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static java.util.Collections.emptySet;

import java.util.Set;

import javax.measure.Quantity;
import javax.measure.quantity.Mass;

import uk.co.strangeskies.mathematics.Interval;

public class EmptyChemicalDatabaseQuery implements ChemicalDatabaseQuery {
	private static final ChemicalDatabaseQuery INSTANCE = new EmptyChemicalDatabaseQuery();

	private EmptyChemicalDatabaseQuery() {}

	@Override
	public ChemicalDatabaseQuery withMass(Interval<Quantity<Mass>> massRange) {
		return this;
	}

	@Override
	public ChemicalDatabaseQuery withMass(Quantity<Mass> mass, double relativeErrorMargin) {
		return this;
	}

	@Override
	public ChemicalDatabaseQuery containingElements(ChemicalComposition composition) {
		return this;
	}

	@Override
	public Set<Chemical> findChemicals() {
		return emptySet();
	}

	public static ChemicalDatabaseQuery instance() {
		return INSTANCE;
	}
}
