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

import uk.co.saiman.expression.Expression;
import uk.co.saiman.expression.PassiveExpression;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

/**
 * An abstract class to help designing mutable expression, implementing a simple
 * observer list, locking mechanism, and update mechanism.
 * <p>
 * This implementation is appropriate for expressions which store their own
 * mutable state which can be changed independently of dependent expressions.
 * For expressions which do not store such state it should be more efficient to
 * extend {@link PassiveExpression}.
 * 
 * @author Elias N Vasylenko
 * @param <T>
 *          The type of the value of this expression
 */
public abstract class ActiveExpression<T> implements Expression<T> {
	private HotObservable<Expression<? extends T>> observable = createObservable();

	private boolean dirty = true;

	private boolean changing;
	private int changeDepth = 0;

	@Override
	public Observable<Expression<? extends T>> invalidations() {
		return observable;
	}

	protected HotObservable<Expression<? extends T>> createObservable() {
		return new HotObservable<>();
	}

	protected void cancelChange() {
		changing = false;
	}

	protected boolean isChanging() {
		return changing;
	}

	protected void write(Runnable runnable) {
		beginWrite();

		try {
			runnable.run();
		} finally {
			endWrite();
		}
	}

	protected boolean beginWrite() {
		boolean begun = changeDepth++ == 0;

		if (begun) {
			changing = true;
		}

		return begun;
	}

	protected boolean endWrite() {
		boolean ended = --changeDepth == 0;

		if (ended && changing) {
			fireChangeImpl();
		}

		return ended;
	}

	protected void fireChange() {
		beginWrite();
		endWrite();
	}

	private boolean fireChangeImpl() {
		boolean fired = !dirty;

		if (fired) {
			dirty = true;
			observable.next(this);
		}

		return fired;
	}

	@Override
	public T getValue() {
		boolean dirty = this.dirty;

		if (this.dirty) {
			this.dirty = false;
		}

		return getValueImpl(dirty);
	}

	/**
	 * Implementing classes should compute the value of the {@link Expression} here.
	 * Read lock is guaranteed to be obtained. This method should never be invoked
	 * manually.
	 * 
	 * @param dirty
	 *          Whether the expression has been mutated since this method was last
	 *          invoked.
	 * @return The value of this {@link Expression}.
	 */
	protected abstract T getValueImpl(boolean dirty);
}
