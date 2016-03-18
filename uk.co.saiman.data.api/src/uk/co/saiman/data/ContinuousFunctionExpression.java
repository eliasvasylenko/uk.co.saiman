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

import uk.co.strangeskies.mathematics.expression.LockingExpression;

/**
 * A basic wrapper around another continuous function which reflects all changes
 * in that function, and propagates events to listeners. The function which is
 * wrapped can be changed via {@link #setComponent(ContinuousFunction)}, which
 * will notify listeners of the modification.
 * 
 * @author Elias N Vasylenko
 */
public class ContinuousFunctionExpression extends LockingExpression<ContinuousFunction, ContinuousFunction>
		implements ContinuousFunctionDecorator {
	private ContinuousFunction component;

	/**
	 * Create a default empty expression about the function
	 * {@link ContinuousFunction#EMPTY}.
	 */
	public ContinuousFunctionExpression() {
		this(ContinuousFunction.EMPTY);
	}

	/**
	 * Create an instance which is initially over the given component.
	 * 
	 * @param component
	 *          The component to wrap
	 */
	public ContinuousFunctionExpression(ContinuousFunction component) {
		setComponent(component);
	}

	@Override
	public ContinuousFunction getComponent() {
		getReadLock().lock();
		try {
			return component;
		} finally {
			getReadLock().unlock();
		}
	}

	/**
	 * Change the component to be wrapped by this function.
	 * 
	 * @param component
	 *          The continuous function we wish to wrap
	 */
	public void setComponent(ContinuousFunction component) {
		beginWrite();

		try {
			this.component = component;
			getDependencies().clear();
			getDependencies().add(component);
		} finally {
			endWrite();
		}
	}

	@Override
	public ContinuousFunctionExpression copy() {
		return new ContinuousFunctionExpression(getComponent().copy());
	}

	@Override
	protected ContinuousFunction evaluate() {
		return this;
	}
}
