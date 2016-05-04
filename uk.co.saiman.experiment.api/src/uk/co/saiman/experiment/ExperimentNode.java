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

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import uk.co.saiman.utilities.Configurable;
import uk.co.strangeskies.reflection.TypeToken;

/**
 * A node in an experiment part tree.
 * 
 * @author Elias N Vasylenko
 *
 * @param <S>
 *          the type of the data describing the experiment configuration and
 *          results
 */
public interface ExperimentNode<S> extends Configurable<S> {
	/**
	 * @return the experiment workspace containing this experiment
	 */
	ExperimentWorkspace getExperimentWorkspace();

	/**
	 * Experiment data root directories are defined hierarchically from the
	 * {@link ExperimentWorkspace#getWorkspaceDataRoot() workspace root}.
	 * 
	 * @return the data root of the experiment
	 */
	Path getExperimentDataRoot();

	@Override
	void configure(S configuration);

	/**
	 * @return The type of the experiment
	 */
	ExperimentType<S> type();

	/**
	 * @return The parent part of this experiment, if present, otherwise an empty
	 *         optional
	 */
	Optional<ExperimentNode<?>> parent();

	/**
	 * @return The root part of the experiment tree this part occurs in
	 */
	default ExperimentNode<ExperimentConfiguration> root() {
		return ancestor(getExperimentWorkspace().getRootExperimentType()).get();
	}

	/**
	 * Get the nearest available ancestor node of the processing experiment node
	 * which is of the given {@link ExperimentType experiment type}.
	 * 
	 * @param type
	 *          the type of the ancestor we wish to inspect
	 * @return the nearest ancestor of the given type, or null if no such ancestor
	 *         exists
	 */
	default <T, E extends ExperimentType<T>> Optional<ExperimentNode<T>> ancestor(E type) {
		Optional<ExperimentNode<?>> ancestor = parent();
		do {
			if (ancestor.get().type() == type) {
				@SuppressWarnings("unchecked")
				ExperimentNode<T> node = (ExperimentNode<T>) ancestor.get();
				return Optional.of(node);
			}

			ancestor = ancestor.flatMap(ExperimentNode::parent);
		} while (ancestor.isPresent());

		return Optional.empty();
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
	List<ExperimentNode<?>> children();

	/**
	 * @return All known available child experiment types
	 */
	Set<ExperimentType<?>> getAvailableChildExperimentTypes();

	/**
	 * Add a child experiment node of the given type to this node.
	 * 
	 * @param childType
	 *          The type of experiment
	 * @return A new child experiment part of the given type
	 */
	<T> ExperimentNode<T> addChild(ExperimentType<T> childType);

	/**
	 * @return The current execution lifecycle state of the experiment part.
	 */
	ExperimentLifecycleState lifecycleState();

	@Override
	default TypeToken<S> getConfigurationType() {
		return type().getStateType();
	}
}
