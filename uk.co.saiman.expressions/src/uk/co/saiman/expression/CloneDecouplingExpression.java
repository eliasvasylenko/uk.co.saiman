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
 * This file is part of uk.co.saiman.expressions.
 *
 * uk.co.saiman.expressions is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.expressions is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.expression;

import java.lang.reflect.InvocationTargetException;

import uk.co.saiman.expression.CopyDecouplingExpression;
import uk.co.saiman.expression.Expression;

/**
 * Similar to {@link CopyDecouplingExpression} for {@link Cloneable}
 * {@link Expression} types.
 * 
 * @author Elias N Vasylenko
 * @param <T>
 *          The type of the expression.
 */
public interface CloneDecouplingExpression<T extends Cloneable> extends Expression<T> {
	@Override
	@SuppressWarnings("unchecked")
	public default T decoupleValue() {
		try {
			return (T) Object.class.getMethod("clone").invoke(getValue());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new UnsupportedOperationException();
		}
	}
}
