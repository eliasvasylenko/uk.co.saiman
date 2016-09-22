/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.simulation.
 *
 * uk.co.saiman.simulation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.simulation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.simulation.msapex.treecontributions;

import java.util.Objects;

import uk.co.strangeskies.fx.PseudoClassTreeCellContribution;
import uk.co.strangeskies.fx.TreeItemData;
import uk.co.strangeskies.fx.TreeTextContribution;

@SuppressWarnings("javadoc")
public class ChemicalContribution
		implements PseudoClassTreeCellContribution<ChemicalColor>, TreeTextContribution<ChemicalColor> {
	@Override
	public <U extends ChemicalColor> String getText(TreeItemData<U> data) {
		return data.data().getName().toString();
	}

	@Override
	public <U extends ChemicalColor> String getSupplementalText(TreeItemData<U> data) {
		return Objects.toString(data.data().getChemical());
	}
}