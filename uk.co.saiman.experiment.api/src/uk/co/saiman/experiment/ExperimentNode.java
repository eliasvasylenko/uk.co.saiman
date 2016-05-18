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

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A node in an experiment part tree.
 * 
 * @author Elias N Vasylenko
 *
 * @param <S>
 *          the type of the data describing the experiment configuration
 */
public interface ExperimentNode<S> {
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

	/**
	 * @return the current state object of the experiment node
	 */
	S getState();

	/**
	 * @return the type of the experiment
	 */
	ExperimentType<S> getType();

	/**
	 * @return the parent part of this experiment, if present, otherwise an empty
	 *         optional
	 */
	Optional<ExperimentNode<?>> getParent();

	/**
	 * @return the node's index in its parent's list of children
	 */
	int getIndex();

	/**
	 * @return the root part of the experiment tree this part occurs in
	 */
	default ExperimentNode<? extends ExperimentConfiguration> getRoot() {
		return getAncestor(getExperimentWorkspace().getRootExperimentType()).get();
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
	default <T, E extends ExperimentType<T>> Optional<ExperimentNode<? extends T>> getAncestor(E type) {
		Optional<ExperimentNode<?>> ancestor = of(this);

		do {
			ExperimentType<?> ancestorType = ancestor.get().getType();

			if (ancestorType == type) {
				@SuppressWarnings("unchecked")
				ExperimentNode<? extends T> node = (ExperimentNode<? extends T>) ancestor.get();
				return of(node);
			}

			ancestor = ancestor.flatMap(ExperimentNode::getParent);
		} while (ancestor.isPresent());

		return empty();
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
	List<ExperimentNode<?>> getChildren();

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
	ExperimentLifecycleState getLifecycleState();
}
