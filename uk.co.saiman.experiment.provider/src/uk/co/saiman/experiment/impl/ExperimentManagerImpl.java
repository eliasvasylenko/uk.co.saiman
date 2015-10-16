/*
 * Copyright (C) 2015 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.instrument.provider.
 *
 * uk.co.saiman.instrument.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.ExperimentManager;
import uk.co.saiman.experiment.ExperimentPart;
import uk.co.saiman.experiment.ExperimentPart.ExperimentLifecycleState;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.instrument.Instrument;

@Component
public class ExperimentManagerImpl implements ExperimentManager {
	private Instrument instrument;

	private final Set<ExperimentType<?, ?, ?>> experimentTypes;

	private final Set<ExperimentPart<?, ?, ?>> experiments;
	private final List<ExperimentPart<?, ?, ?>> processingStack;

	public ExperimentManagerImpl() {
		experimentTypes = new HashSet<>();

		experiments = new HashSet<>();
		processingStack = new ArrayList<>();
	}

	public ExperimentManagerImpl(Instrument instrument) {
		this();
		this.instrument = instrument;
	}

	@Reference
	public void addExperimentType(ExperimentType<?, ?, ?> experimentType) {
		experimentTypes.add(experimentType);
	}

	@Reference
	public void removeExperimentType(ExperimentType<?, ?, ?> experimentType) {
		experimentTypes.remove(experimentType);
	}

	@Reference
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public List<ExperimentLifecycleState> state() {
		return processingStack.stream().map(ExperimentPart::state).collect(Collectors.toList());
	}

	@Override
	public Set<ExperimentPart<?, Instrument, ?>> getRootExperiments() {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<ExperimentType<?, Instrument, ?>> getRootExperimentTypes() {
		return experimentTypes.stream().filter(e -> e.getInputType().isAssignableFrom(Instrument.class))
				.map(e -> (ExperimentType<?, Instrument, ?>) e).collect(Collectors.toSet());
	}

	@Override
	public <C, O> ExperimentPart<C, Instrument, O> addRootExperiment(ExperimentType<C, Instrument, O> rootType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Set<ExperimentType<?, T, ?>> getChildExperimentTypes(ExperimentPart<?, ?, T> parentPart) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <C, T, O> ExperimentPart<C, T, O> addChildExperiment(ExperimentPart<?, ?, T> parentPart,
			ExperimentType<C, T, O> childType) {
		// TODO Auto-generated method stub
		return null;
	}
}
