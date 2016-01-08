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

import uk.co.saiman.instrument.Instrument;

/**
 * API for experiment management, i.e. registration and discovery of experiment
 * types, and registration of root experiment parts such that experiment part
 * trees can be built.
 * 
 * @author Elias N Vasylenko
 */
public interface ExperimentManager {
	/**
	 * Execute the experiment tree at the given root.
	 * 
	 * @param experimentRoot
	 *          The root of the experiment tree to execute
	 */
	public void execute(ExperimentPart<?, Instrument, ?> experimentRoot);

	/**
	 * Get the current processing state of the experiment manager.
	 * 
	 * @return The current state, in the form of the stack of all currently
	 *         executing experiment parts
	 */
	public List<ExperimentPart<?, ?, ?>> state();

	/*
	 * Root experiment types
	 */

	/**
	 * @return All known available root experiment types
	 */
	Set<ExperimentType<?, Instrument, ?>> getRootExperimentTypes();

	/**
	 * Register an available root experiment type
	 * 
	 * @param rootType
	 *          A possible root experiment type
	 * @return True if the type was added successfully, false otherwise
	 */
	boolean addRootExperimentType(ExperimentType<?, Instrument, ?> rootType);

	/**
	 * Unregister an available root experiment type
	 * 
	 * @param rootType
	 *          A possible root experiment type
	 * @return True if the type was removed successfully, false otherwise
	 */
	boolean removeRootExperimentType(ExperimentType<?, Instrument, ?> rootType);

	/**
	 * @return All registered root experiment parts
	 */
	Set<ExperimentPart<?, Instrument, ?>> getRootExperiments();

	/**
	 * Add a root experiment node of the given type to management.
	 * 
	 * @param rootType
	 *          The type of experiment
	 * @return A new root experiment part of the given type
	 */
	<C, O> ExperimentPart<C, Instrument, O> addRootExperiment(ExperimentType<C, Instrument, O> rootType);

	/**
	 * Remove the given root experiment node from management.
	 * 
	 * @param rootPart
	 *          The experiment part
	 * @return True if the part was removed successfully, false otherwise
	 */
	boolean removeRootExperiment(ExperimentPart<?, Instrument, ?> rootPart);

	/*
	 * Child experiment types
	 */

	boolean addExperimentType(ExperimentType<?, ?, ?> childType);

	boolean removeExperimentType(ExperimentType<?, ?, ?> childType);
}
