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
 * This file is part of uk.co.saiman.reflection.token.
 *
 * uk.co.saiman.reflection.token is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.reflection.token is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.reflection.token;

import static uk.co.saiman.reflection.token.TypedObject.typedObject;

import java.lang.reflect.Type;

import uk.co.saiman.reflection.Reified;
import uk.co.saiman.utility.Self;

/**
 * A type whose instances know their own type, including any available generic
 * information.
 * <p>
 * As with the extended {@link Self} interface, generally only the most specific
 * <em>useful</em> type will be considered.
 * 
 * @author Elias N Vasylenko
 * @param <S>
 *          The type of the instance
 */
public interface ReifiedToken<S extends ReifiedToken<S>> extends Reified, Self<S> {
	/**
	 * @return a {@link TypeToken} over the value of {@link #getThisType()}
	 */
	TypeToken<S> getThisTypeToken();

	@Override
	default Type getThisType() {
		return getThisTypeToken().getType();
	}

	/**
	 * @return this object as a {@link TypedObject}
	 */
	default TypedObject<S> asTypedObject() {
		return typedObject(getThisTypeToken(), getThis());
	}
}
