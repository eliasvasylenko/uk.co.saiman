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
 * This file is part of uk.co.saiman.utilities.
 *
 * uk.co.saiman.utilities is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.utilities is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.function;

import java.util.function.Function;

/**
 * Describes a function from F to T. A function should be stateless.
 * 
 * @author Elias N Vasylenko
 * 
 * @param <T>
 *          Operand type.
 * @param <R>
 *          Result type.
 */
public interface InvertibleFunction<T, R> extends Function<T, R> {
	/**
	 * This returns the mathematical inverse of the receiving function.
	 * 
	 * @return A new Invertible function performing the inverse operation.
	 */
	public InvertibleFunction<R, T> getInverse();

	/**
	 * @param <T>
	 *          The operand type of the forward function, and the result type of
	 *          its reverse.
	 * @param <R>
	 *          The result type of the forward function, and the operand type of
	 *          its reverse.
	 * @param function
	 *          The function in forward direction.
	 * @param reverse
	 *          The reverse of the function.
	 * @return An invertible function using the two given functions.
	 */
	public static <T, R> InvertibleFunction<T, R> over(
			Function<? super T, ? extends R> function,
			Function<? super R, ? extends T> reverse) {
		return new InvertibleFunction<T, R>() {
			@Override
			public R apply(T t) {
				return function.apply(t);
			}

			@Override
			public InvertibleFunction<R, T> getInverse() {
				return over(reverse, function);
			}
		};
	}

	/**
	 * @param <T>
	 *          The operand type of the first function.
	 * @param <I>
	 *          An intermediate type which the result type of the first function
	 *          can be assigned to, and which can assign to the operand type of
	 *          the second function.
	 * @param <R>
	 *          The result type of the second function.
	 * @param first
	 *          The first function to compose.
	 * @param second
	 *          The second function to compose.
	 * @return Composition of two invertible functions into a single invertible
	 *         function.
	 */
	public static <T, I, R> InvertibleFunction<T, R> compose(
			InvertibleFunction<T, I> first, InvertibleFunction<I, R> second) {
		return over(first.andThen(second),
				second.getInverse().andThen(first.getInverse()));
	}
}
