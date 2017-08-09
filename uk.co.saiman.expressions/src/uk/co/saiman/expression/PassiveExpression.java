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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.observable.Observable.merge;

import java.util.Collection;

import uk.co.saiman.expression.Expression;
import uk.co.saiman.observable.Observable;

/**
 * An expression which is dependent upon the evaluation of a number of other
 * expressions.
 * 
 * @author Elias N Vasylenko
 * @param <T>
 *          The type of the expression.
 */
public abstract class PassiveExpression<T> implements Expression<T> {
	private final Observable<Expression<? extends T>> dependencies;
	private T value;

	public PassiveExpression(Collection<? extends Expression<?>> dependencies) {
		this.dependencies = merge(
				dependencies.stream().map(Expression::invalidations).collect(toList())).map(e -> {
					invalidate();
					return this;
				});
		this.dependencies.observe();
	}

	public PassiveExpression(Expression<?>... dependencies) {
		this(asList(dependencies));
	}

	@Override
	public Observable<Expression<? extends T>> invalidations() {
		return dependencies;
	}

	private void invalidate() {
		value = null;
	}

	@Override
	public final T getValue() {
		if (value == null) {
			value = evaluate();
		}

		return value;
	}

	/**
	 * @return The value of this {@link Expression} as derived from the dependency
	 *         {@link Expression}s.
	 */
	protected abstract T evaluate();
}
