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

import java.util.Set;

import uk.co.saiman.instrument.Instrument;

/**
 * @author Elias N Vasylenko
 */
public interface ExperimentManager {
	public Set<ExperimentPart<?, Instrument, ?>> getRootExperiments();

	public Set<ExperimentType<?, Instrument, ?>> getRootExperimentTypes();

	public <C, O> ExperimentPart<C, Instrument, O> addRootExperiment(ExperimentType<C, Instrument, O> rootType);

	public <T> Set<ExperimentType<?, T, ?>> getChildExperimentTypes(ExperimentPart<?, ?, T> parentPart);

	public <C, T, O> ExperimentPart<C, T, O> addChildExperiment(ExperimentPart<?, ?, T> parentPart,
			ExperimentType<C, T, O> childType);
}
