/*
 * Copyright (C) 2015 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.processing.
 *
 * uk.co.saiman.processing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.processing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.processing;

import uk.co.strangeskies.reflection.TypeParameter;
import uk.co.strangeskies.reflection.TypeToken;

/**
 * A processor instance should be completely idempotent and stateless. Because
 * of this, they may be used asynchronously, and they may be modelled as
 * singletons where appropriate.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 * @param <R>
 */
public interface Processor<T, R> {
	String name();

	R process(T target);

	default TypeToken<T> getTargetType() {
		return TypeToken.over(getClass()).resolveSupertypeParameters(Processor.class)
				.resolveTypeArgument(new TypeParameter<T>() {}).infer();
	}

	default TypeToken<R> getResultType() {
		return TypeToken.over(getClass()).resolveSupertypeParameters(Processor.class)
				.resolveTypeArgument(new TypeParameter<R>() {}).infer();
	}
}
