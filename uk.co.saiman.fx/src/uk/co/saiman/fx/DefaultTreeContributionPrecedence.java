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
 * This file is part of uk.co.saiman.fx.
 *
 * uk.co.saiman.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.fx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.fx;

import static uk.co.saiman.reflection.ConstraintFormula.Kind.LOOSE_COMPATIBILILTY;

import java.util.Comparator;

import javafx.util.Pair;

/**
 * The default {@link TreeContribution tree contribution} precedence
 * {@link ModularTreeView#setPrecedence(Comparator) comparator}.
 * 
 * @author Elias N Vasylenko
 */
public class DefaultTreeContributionPrecedence implements Comparator<Pair<TreeContribution<?>, Integer>> {
	@Override
	public int compare(Pair<TreeContribution<?>, Integer> first, Pair<TreeContribution<?>, Integer> second) {
		int precedence = first.getValue() - second.getValue();

		if (precedence == 0) {
			if (second.getKey().getDataType().satisfiesConstraintFrom(LOOSE_COMPATIBILILTY, first.getKey().getDataType())) {
				precedence = 1;

			} else if (first
					.getKey()
					.getDataType()
					.satisfiesConstraintFrom(LOOSE_COMPATIBILILTY, second.getKey().getDataType())) {
				precedence = -1;
			}
		}

		return precedence;
	}
}
