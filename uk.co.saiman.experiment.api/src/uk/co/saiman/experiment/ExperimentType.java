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
 * A type of experiment which may be available to be executed on a particular
 * instrument.
 * 
 * @author Elias N Vasylenko
 *
 * @param <C>
 *          The type of the experiment configuration interface
 * @param <I>
 *          The type of the experiment input
 * @param <O>
 *          The type of the experiment output
 */
public interface ExperimentType<C, I, O> {
	/**
	 * Verify that a given configuration is valid for this experiment type.
	 * 
	 * @param configuration
	 *          The configuration to test
	 */
	void validate(C configuration);

	/**
	 * Execute this experiment type for a given input and configuration.
	 * 
	 * @param configuration
	 *          The configuration to apply to the execution
	 * @param input
	 *          The input of the execution
	 * @return The output of the execution
	 */
	O execute(C configuration, I input);

	/**
	 * @return The exact generic type of the configuration interface
	 */
	default TypeToken<C> getConfigurationType() {
		return TypeToken.over(getClass()).resolveSupertypeParameters(Configurable.class)
				.resolveTypeArgument(new TypeParameter<C>() {}).infer();
	}

	/**
	 * @return The exact generic type of the experiment input
	 */
	default TypeToken<I> getInputType() {
		return TypeToken.over(getClass()).resolveSupertypeParameters(Configurable.class)
				.resolveTypeArgument(new TypeParameter<I>() {}).infer();
	}

	/**
	 * @return The exact generic type of the experiment output
	 */
	default TypeToken<O> getOutputType() {
		return TypeToken.over(getClass()).resolveSupertypeParameters(Configurable.class)
				.resolveTypeArgument(new TypeParameter<O>() {}).infer();
	}
}