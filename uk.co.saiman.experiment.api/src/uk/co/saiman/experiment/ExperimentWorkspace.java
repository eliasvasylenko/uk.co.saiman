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
import java.util.Set;

/**
 * API for experiment management, i.e. registration and discovery of experiment
 * types, and registration of root experiment parts such that experiment part
 * trees can be built.
 * 
 * @author Elias N Vasylenko
 */
public interface ExperimentWorkspace {
	/**
	 * @return the data root of the workspace
	 */
	Path getWorkspaceDataRoot();

	/**
	 * Get the current processing state of the experiment manager.
	 * 
	 * @return the current state, in the form of the stack of all currently
	 *         executing experiment parts
	 */
	public List<ExperimentNode<?>> processingState();

	/*
	 * Root experiment types
	 */

	/**
	 * @return the root experiment type
	 */
	ExperimentType<ExperimentConfiguration> getRootExperimentType();

	/**
	 * Get all experiments of the {@link #getRootExperiments() root experiment
	 * type}.
	 * 
	 * @return all registered root experiment parts
	 */
	List<ExperimentNode<ExperimentConfiguration>> getRootExperiments();

	/**
	 * Add a root experiment node to management.
	 * 
	 * @param name
	 *          the name of the new experiment
	 * @return a new root experiment part of the root type
	 */
	ExperimentNode<ExperimentConfiguration> addRootExperiment(String name);

	/*
	 * Child experiment types
	 */

	/**
	 * Register an available experiment type
	 * 
	 * @param experimentType
	 *          a possible experiment type
	 * @return true if the type was added successfully, false otherwise
	 */
	boolean registerExperimentType(ExperimentType<?> experimentType);

	/**
	 * Unregister an available experiment type
	 * 
	 * @param experimentType
	 *          a possible experiment type
	 * @return true if the type was removed successfully, false otherwise
	 */
	boolean unregisterExperimentType(ExperimentType<?> experimentType);

	/**
	 * @return the set of all experiment types registered to this workspace
	 */
	Set<ExperimentType<?>> getRegisteredExperimentTypes();
}
