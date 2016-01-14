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
	/**
	 * Execute the experiment tree from the root of the receiving node.
	 */
	public void execute();

	/**
	 * @return The output of this experiment part if its {@link #state()} is
	 *         {@link ExperimentLifecycleState#COMPLETION}, otherwise an empty
	 *         optional
	 */
	public Optional<O> output();

	/**
	 * @return The type of the experiment
	 */
	ExperimentType<C, I, O> type();

	/**
	 * @return The parent part of this experiment, if present, otherwise an empty
	 *         optional
	 */
	Optional<ExperimentPart<?, ?, ? extends I>> parent();

	/**
	 * @return The root part of the experiment tree this part occurs in
	 */
	@SuppressWarnings("unchecked")
	default ExperimentPart<?, Void, ?> root() {
		return parent().<ExperimentPart<?, Void, ?>> map(ExperimentPart::root).orElse((ExperimentPart<?, Void, ?>) this);
	}

	/**
	 * Remove this part from its parent, or from the containing manager if it is
	 * the root part.
	 */
	void remove();

	/**
	 * Get all child experiment parts, to be executed sequentially during this
	 * parts {@link ExperimentLifecycleState#PROCESSING} state.
	 * 
	 * @return An ordered list of all sequential child experiment parts
	 */
	List<ExperimentPart<?, ? super O, ?>> children();

	/**
	 * @return All known available child experiment types
	 */
	Set<ExperimentType<?, ? super O, ?>> getAvailableChildExperimentTypes();

	/**
	 * Add a child experiment node of the given type to this node.
	 * 
	 * @param childType
	 *          The type of experiment
	 * @return A new child experiment part of the given type
	 */
	<D, U> ExperimentPart<D, O, U> addChild(ExperimentType<D, ? super O, U> childType);

	/**
	 * @return The current execution lifecycle state of the experiment part.
	 */
	ExperimentLifecycleState state();
}
