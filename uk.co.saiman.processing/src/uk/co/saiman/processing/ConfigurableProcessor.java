/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,-========\     ,`===\    /========== \
 *      /== \___/== \  ,`==.== \   \__/== \___\/
 *     /==_/____\__\/,`==__|== |     /==  /
 *     \========`. ,`========= |    /==  /
 *   ___`-___)== ,`== \____|== |   /==  /
 *  /== \__.-==,`==  ,`    |== '__/==  /_
 *  \======== /==  ,`      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
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

import uk.co.strangeskies.reflection.token.TypeParameter;
import uk.co.strangeskies.reflection.token.TypeToken;

/**
 * Conceptually, a configurable {@link Processor}. This interface consumes a
 * given configuration to provide a {@link Processor} for that configuration.
 * 
 * @author Elias N Vasylenko
 *
 * @param <C>
 *          The configuration interface type
 * @param <T>
 *          The processing target type
 * @param <R>
 *          The processing result type
 */
public interface ConfigurableProcessor<C, T, R> {
	/**
	 * Provide a processor for the given configuration.
	 * 
	 * @param configuration
	 *          The configuration of the requested processor
	 * @return A configured processor
	 */
	Processor<T, R> configure(C configuration);

	/**
	 * @return The exact generic type of the processing target
	 */
	default TypeToken<T> getTargetType() {
		return TypeToken
				.overType(getClass())
				.resolveSupertype(ConfigurableProcessor.class)
				.resolveTypeArgument(new TypeParameter<T>() {})
				.infer();
	}

	/**
	 * @return The exact generic type of the processing result
	 */
	default TypeToken<R> getResultType() {
		return TypeToken
				.overType(getClass())
				.resolveSupertype(ConfigurableProcessor.class)
				.resolveTypeArgument(new TypeParameter<R>() {})
				.infer();
	}

	/**
	 * @return The exact generic type of the configuration interface
	 */
	default TypeToken<C> getConfigurationType() {
		return TypeToken
				.overType(getClass())
				.resolveSupertype(ConfigurableProcessor.class)
				.resolveTypeArgument(new TypeParameter<C>() {})
				.infer();
	}
}
