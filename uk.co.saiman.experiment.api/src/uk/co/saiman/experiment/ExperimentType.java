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

import static uk.co.strangeskies.reflection.TypeToken.over;

import uk.co.strangeskies.reflection.TypeParameter;
import uk.co.strangeskies.reflection.TypeToken;

/**
 * A type of experiment which may be available to be executed on a particular
 * instrument.
 * 
 * @author Elias N Vasylenko
 *
 * @param <S>
 *          the type of the data describing the experiment state, including
 *          configuration and results
 */
public interface ExperimentType<S> {
	/**
	 * @return the human readable name of the experiment type
	 */
	String getName();

	/**
	 * @param forNode
	 *          the node which the configuration is being requested for
	 * @return a new state object suitable for an instance of
	 *         {@link ExperimentNode} over this type.
	 */
	S createConfiguration(ExperimentNode<S> forNode);

	/**
	 * Update a given state object to match the given configuration. This method
	 * may simply return the {@code configuration} parameter directly, or it may
	 * return the {@code currentState} parameter after applying the configuration
	 * to it, depending on whether identity of the configuration object needs to
	 * be preserved.
	 * <p>
	 * Validation may also be performed here.
	 * 
	 * @param currentState
	 *          the current state of the experiment node to be updated
	 * @param newConfiguration
	 *          the configuration we wish to apply to the current state
	 * @return the new state object to use
	 */
	S updateConfiguration(S currentState, S newConfiguration);

	/**
	 * Execute this experiment type for a given input and configuration.
	 * 
	 * @param node
	 *          the node to be processed
	 */
	void execute(ExperimentNode<S> node);

	/**
	 * @return the exact generic type of the configuration interface
	 */
	default TypeToken<S> getStateType() {
		return over(getClass()).resolveSupertypeParameters(ExperimentType.class)
				.resolveTypeArgument(new TypeParameter<S>() {}).infer();
	}
}
