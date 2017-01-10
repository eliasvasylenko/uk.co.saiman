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

import static uk.co.strangeskies.reflection.token.TypeToken.overType;

import uk.co.strangeskies.reflection.token.TypeParameter;
import uk.co.strangeskies.reflection.token.TypeToken;

/**
 * A process to be applied to a target to create a result.
 * <p>
 * A processor instance should be completely stateless, and idempotent with the
 * same input. Because of this, they may be used asynchronously, and they may be
 * modelled as singletons where appropriate.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          The type of the processing target.
 * @param <R>
 *          The type of the processing result.
 */
public interface Processor<T, R> {
	/**
	 * @return The name of the process
	 */
	String name();

	/**
	 * Process a given target into a result.
	 * 
	 * @param target
	 *          The target to be processed
	 * @return The result of applying processing to the target
	 */
	R process(T target);

	/**
	 * @return The exact generic type of the processing target
	 */
	default TypeToken<T> getTargetType() {
		return overType(getClass())
				.resolveSupertype(Processor.class)
				.resolveTypeArgument(new TypeParameter<T>() {})
				.infer();
	}

	/**
	 * @return The exact generic type of the processing result
	 */
	default TypeToken<R> getResultType() {
		return overType(getClass())
				.resolveSupertype(Processor.class)
				.resolveTypeArgument(new TypeParameter<R>() {})
				.infer();
	}
}
