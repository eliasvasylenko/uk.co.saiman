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
import java.util.Set;

/**
 * API for experiment management, i.e. registration and discovery of experiment
 * types, and registration of root experiment parts such that experiment part
 * trees can be built.
 * 
 * @author Elias N Vasylenko
 */
public interface ExperimentManager {
	/**
	 * Get the current processing state of the experiment manager.
	 * 
	 * @return The current state, in the form of the stack of all currently
	 *         executing experiment parts
	 */
	public List<ExperimentNode<?, ?, ?>> processingState();

	/*
	 * Root experiment types
	 */

	/**
	 * @return All known available root experiment types
	 */
	Set<ExperimentNodeType<?, Void, ?>> getAvailableRootExperimentTypes();

	/**
	 * Get all registered experiment types which can be placed at the root of a
	 * tree, i.e. those which accept {@link Void} as their input.
	 * 
	 * @return All registered root experiment parts
	 */
	Set<ExperimentNode<?, Void, ?>> getRootExperiments();

	/**
	 * Add a root experiment node of the given type to management.
	 * 
	 * @param rootType
	 *          The type of experiment
	 * @return A new root experiment part of the given type
	 */
	<C, O> ExperimentNode<C, Void, O> addRootExperiment(ExperimentNodeType<C, Void, O> rootType);

	/*
	 * Child experiment types
	 */

	/**
	 * Register an available experiment type
	 * 
	 * @param childType
	 *          A possible experiment type
	 * @return True if the type was added successfully, false otherwise
	 */
	boolean registerExperimentType(ExperimentNodeType<?, ?, ?> childType);

	/**
	 * Unregister an available experiment type
	 * 
	 * @param childType
	 *          A possible experiment type
	 * @return True if the type was removed successfully, false otherwise
	 */
	boolean unregisterExperimentType(ExperimentNodeType<?, ?, ?> childType);
}
