/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.data.api.
 *
 * uk.co.saiman.data.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.data.api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.data;

import java.util.function.Function;

import uk.co.strangeskies.mathematics.expression.FunctionExpression;

public class ContinuousFunctionTransformation<C extends ContinuousFunction> extends FunctionExpression<ContinuousFunction, ContinuousFunction>
		implements ContinuousFunctionDecorator {
	private final Function<C, ContinuousFunction> transformation;

	@SuppressWarnings("unchecked")
	public ContinuousFunctionTransformation(C dependency, Function<C, ContinuousFunction> transformation) {
		super(dependency, c -> transformation.apply((C) c));

		this.transformation = transformation;
	}

	@Override
	public ContinuousFunction getComponent() {
		return getValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ContinuousFunction copy() {
		C component;

		try {
			getReadLock().lock();
			component = (C) getValue().copy();
		} finally {
			getReadLock().unlock();
		}

		return new ContinuousFunctionTransformation<>(component, transformation);
	}
}
