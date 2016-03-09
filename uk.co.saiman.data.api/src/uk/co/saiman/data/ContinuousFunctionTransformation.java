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

import uk.co.strangeskies.mathematics.expression.DependentExpression;

/**
 * A {@link ContinuousFunction} which is backed by a given
 * {@link ContinuousFunction} and which is derived from it by a given
 * transformation function.
 * <p>
 * Changes in the backing function will be reflected in this function and
 * forwarded to listeners, and the transformation will only be evaluated lazily,
 * as necessary.
 * 
 * @author Elias N Vasylenko
 */
public class ContinuousFunctionTransformation extends DependentExpression<ContinuousFunction, ContinuousFunction>
		implements ContinuousFunctionDecorator {
	private final Function<ContinuousFunction, ContinuousFunction> transformation;

	/**
	 * Create a mapping from a given {@link ContinuousFunction} to a
	 * {@link ContinuousFunction} by the given transformation.
	 * 
	 * @param dependency
	 *          The backing function, changes in this function will be reflected
	 *          in the instantiated function
	 * @param transformation
	 *          The transformation to apply to the backing function
	 */
	@SuppressWarnings("unchecked")
	public <C extends ContinuousFunction> ContinuousFunctionTransformation(C dependency,
			Function<C, ContinuousFunction> transformation) {
		super(dependency);

		this.transformation = (Function<ContinuousFunction, ContinuousFunction>) transformation;
	}

	@Override
	public ContinuousFunction getComponent() {
		return getValue();
	}

	@Override
	public ContinuousFunction copy() {
		ContinuousFunction component;
		synchronized (this) {
			component = getValue().copy();
		}

		return new ContinuousFunctionTransformation(component, transformation);
	}

	@Override
	protected ContinuousFunction evaluate() {
		return this;
	}
}
