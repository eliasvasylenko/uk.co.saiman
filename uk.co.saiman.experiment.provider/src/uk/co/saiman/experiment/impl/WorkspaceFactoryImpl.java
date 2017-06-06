/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
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

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.experiment.WorkspaceFactory;
import uk.co.strangeskies.log.Log;
import uk.co.strangeskies.text.properties.PropertyLoader;

/**
 * Reference implementation of {@link WorkspaceFactory}.
 * 
 * @author Elias N Vasylenko
 */
@Component
public class WorkspaceFactoryImpl implements WorkspaceFactory {
	@Reference
	Log log;

	@Reference
	PropertyLoader loader;

	private final Set<ExperimentType<?>> experimentTypes = new HashSet<>();

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	protected void registerExperimentType(ExperimentType<?> experimentType) {
		experimentTypes.add(experimentType);
	}

	protected void unregisterExperimentType(ExperimentType<?> experimentType) {
		experimentTypes.remove(experimentType);
	}

	public Stream<ExperimentType<?>> getRegisteredExperimentTypes() {
		return experimentTypes.stream();
	}

	@Override
	public Workspace openWorkspace(Path location) {
		return new WorkspaceImpl(
				this,
				location,
				loader.getProperties(ExperimentProperties.class));
	}

	public Log getLog() {
		return log;
	}
}
