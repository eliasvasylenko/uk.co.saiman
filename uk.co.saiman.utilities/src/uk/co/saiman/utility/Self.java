/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
package uk.co.saiman.utility;

/**
 * For classes which follow the self-bounding pattern. The self-bounding pattern
 * is the use of the final derived class as a type parameter.
 * <p>
 * Generally only the most specific <em>useful</em> type will be considered for
 * parameterization, and more specific type information will be discarded.
 * 
 * @author Elias N Vasylenko
 *
 * @param <S>
 *          the final implementing class
 */
public interface Self<S extends Self<S>> extends Copyable<S> {
	/**
	 * @return A reference to the receiver, cast to it's type as denoted by the
	 *         parameterization of Self.
	 */
	@SuppressWarnings("unchecked")
	public default S getThis(Self<S>this) {
		return (S) this;
	}
}
