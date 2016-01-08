/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.experiment.api.
 *
 * uk.co.saiman.experiment.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment;

import uk.co.saiman.utilities.Configurable;
import uk.co.strangeskies.reflection.TypeParameter;
import uk.co.strangeskies.reflection.TypeToken;

/**
 * 
 * @author Elias N Vasylenko
 *
 * @param <C>
 *          The type of configuration object.
 * @param <I>
 *          The type of the experiment's input.
 * @param <O>
 *          The type of the experiment's output.
 */
public interface ExperimentType<C, I, O> {
	public void validate(C configuration);

	public O execute(C configuration, I input);

	default TypeToken<C> getConfigurationType() {
		return TypeToken.over(getClass()).resolveSupertypeParameters(Configurable.class)
				.resolveTypeArgument(new TypeParameter<C>() {}).infer();
	}

	default TypeToken<I> getInputType() {
		return TypeToken.over(getClass()).resolveSupertypeParameters(Configurable.class)
				.resolveTypeArgument(new TypeParameter<I>() {}).infer();
	}

	default TypeToken<O> getOutputType() {
		return TypeToken.over(getClass()).resolveSupertypeParameters(Configurable.class)
				.resolveTypeArgument(new TypeParameter<O>() {}).infer();
	}
}
