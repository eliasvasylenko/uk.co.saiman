/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.experiment.provider.
 *
 * uk.co.saiman.experiment.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.provider is distributed in the hope that it will be useful,
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

import uk.co.saiman.experiment.ExperimentManager;
import uk.co.saiman.experiment.ExperimentPart;
import uk.co.saiman.experiment.ExperimentType;

/**
 * Reference implementation of {@link ExperimentManager}.
 * 
 * @author Elias N Vasylenko
 */
@Component
public class ExperimentManagerImpl implements ExperimentManager {
	private final Set<ExperimentType<?, ?, ?>> experimentTypes;

	private final Set<ExperimentPart<?, ?, ?>> experiments;
	private final List<ExperimentPart<?, ?, ?>> processingStack;

	public ExperimentManagerImpl() {
		experimentTypes = new HashSet<>();

		experiments = new HashSet<>();
		processingStack = new ArrayList<>();
	}

	@Override
	public List<ExperimentPart<?, ?, ?>> state() {
		return processingStack;
	}

	/*
	 * Root experiment types
	 */

	@SuppressWarnings("unchecked")
	@Override
	public Set<ExperimentType<?, Void, ?>> getAvailableRootExperimentTypes() {
		return experimentTypes.stream().filter(e -> e.getInputType().isAssignableFrom(Void.class))
				.map(e -> (ExperimentType<?, Void, ?>) e).collect(Collectors.toSet());
	}

	@Override
	public Set<ExperimentPart<?, Void, ?>> getRootExperiments() {
		return null;
	}

	@Override
	public <C, O> ExperimentPart<C, Void, O> addRootExperiment(ExperimentType<C, Void, O> rootType) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * Child experiment types
	 */

	@Override
	public boolean registerExperimentType(ExperimentType<?, ?, ?> childType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unregisterExperimentType(ExperimentType<?, ?, ?> childType) {
		// TODO Auto-generated method stub
		return false;
	}
}
