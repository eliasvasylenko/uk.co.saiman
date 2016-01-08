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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import uk.co.saiman.utilities.Configurable;

/**
 * A node in an experiment part tree.
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
public interface ExperimentPart<C, I, O> extends Configurable<C> {
	ExperimentType<C, I, O> type();

	Optional<ExperimentPart<?, ?, ? extends I>> parent();

	void remove();

	List<ExperimentPart<?, ? super O, ?>> children();

	<T> Set<ExperimentType<?, ? super O, ?>> getChildTypes();

	<D, U> ExperimentPart<D, O, U> addChild(ExperimentType<D, ? super O, U> childType);

	ExperimentLifecycleState state();

	enum ExperimentLifecycleState {
		/**
		 * Experiment if configurable and unprocessed.
		 */
		CONFIGURATION,

		/**
		 * Experiment part is locked out of configuration and waiting in part of a
		 * processing queue.
		 */
		WAITING,

		/**
		 * Move stage into position, etc.
		 */
		PREPARATION,

		/**
		 * Optimise laser, acquire from TDC, etc.
		 */
		PROCESSING,

		/**
		 * Once transitioned to this state, data is acquired
		 */
		COMPLETION,

		/**
		 * Something went wrong...
		 */
		FAILURE
	}
}
