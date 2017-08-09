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
 * This file is part of uk.co.saiman.mathematics.
 *
 * uk.co.saiman.mathematics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.mathematics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.mathematics.operation;

import uk.co.saiman.expression.BinaryExpression;
import uk.co.saiman.expression.Expression;

public class PreMultiplication<O extends NonCommutativelyMultipliable<?, ? super T>, T>
		extends BinaryExpression<NonCommutativelyMultipliable<? extends O, ? super T>, T, O> {
	public PreMultiplication(Expression<? extends NonCommutativelyMultipliable<? extends O, ? super T>> firstOperand,
			Expression<? extends T> secondOperand) {
		/*
		 * TODO GETTING SICK OF THESE JAVAC BUGS. Yet another place I can't use ::
		 * syntax because Oracle javac is broken.
		 */
		super(firstOperand, secondOperand, (a, b) -> a.getPreMultiplied(b));
	}

	@Override
	public String toString() {
		return "(" + getFirstOperand() + " * " + getSecondOperand() + ")";
	}
}
