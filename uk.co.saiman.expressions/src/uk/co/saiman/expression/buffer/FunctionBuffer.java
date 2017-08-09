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

import java.util.function.BiFunction;
import java.util.function.Function;

public class FunctionBuffer<B, F> extends AbstractFunctionBuffer<B, F> {
	public FunctionBuffer(F front, B back, BiFunction<? super F, ? super B, ? extends F> operation) {
		super(front, back, operation);
	}

	public FunctionBuffer(F front, B back, Function<? super B, ? extends F> function) {
		super(front, back, function);
	}

	public FunctionBuffer(B back, Function<? super B, ? extends F> function) {
		super(back, function);
	}

	public FunctionBuffer(FunctionBuffer<B, F> doubleBuffer) {
		super(doubleBuffer);
	}
}
