/*
 * Copyright (C) 2015 Elias N Vasylenko <eliasvasylenko@gmail.com>
 *
 * This file is part of uk.co.saiman.msapex.instrument.api.
 *
 * uk.co.saiman.msapex.instrument.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.instrument.api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with uk.co.saiman.msapex.instrument.api.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument;

import uk.co.strangeskies.reflection.TypeParameter;
import uk.co.strangeskies.reflection.TypeToken;

public interface InstrumentModule<T> {
	T get();

	void set(T configuration);

	default TypeToken<T> getStatusType() {
		return TypeToken.over(getClass()).resolveSupertypeParameters(InstrumentModule.class)
				.resolveTypeArgument(new TypeParameter<T>() {}).infer();
	}
}
