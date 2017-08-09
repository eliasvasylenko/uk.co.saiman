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
package uk.co.saiman.expression.buffer;

import uk.co.saiman.expression.Expression;

public interface DoubleBuffer<B, F> extends Expression<F> {
	public abstract F setFront(F front);

	public abstract B setBack(B back);

	public default void set(B value) {
		setBack(value);
		push();
	}

	public default void set(DoubleBuffer<? extends B, ? extends F> value) {
		setFront(value.getFront());
		setBack(value.getBack());
	}

	public abstract F getFront();

	public abstract B getBack();

	public abstract Expression<B> getBackExpression();

	public abstract void push();

	public abstract boolean isFlat();

	public default void invalidateBack() {
		setBack(getBack());
	}
}
