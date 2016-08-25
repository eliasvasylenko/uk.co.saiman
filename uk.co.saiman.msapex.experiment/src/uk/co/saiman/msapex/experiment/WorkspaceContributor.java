/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.msapex.experiment.
 *
 * uk.co.saiman.msapex.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment;

import java.util.List;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentWorkspace;
import uk.co.saiman.experiment.RootExperiment;
import uk.co.strangeskies.fx.TreeCellContribution;
import uk.co.strangeskies.fx.TreeChildContribution;
import uk.co.strangeskies.reflection.TypeToken;
import uk.co.strangeskies.reflection.TypedObject;

/**
 * An implementation of {@link TreeCellContribution} which registers the
 * experiment tree pop-up menu from the experiment project model fragment.
 * 
 * @author Elias N Vasylenko
 */
@Component
public class WorkspaceContributor implements ExperimentTreeContributor {
	@Override
	public Class<WorkspaceContribution> getContribution() {
		return WorkspaceContribution.class;
	}
}

class WorkspaceContribution implements TreeChildContribution<ExperimentWorkspace> {
	@Override
	public boolean hasChildren(ExperimentWorkspace workspace) {
		return !workspace.getRootExperiments().isEmpty();
	}

	@Override
	public List<TypedObject<?>> getChildren(ExperimentWorkspace workspace) {
		return workspace.getRootExperiments().stream()
				.map(c -> new TypeToken<ExperimentNode<RootExperiment, ExperimentConfiguration>>() {}.typedObject(c))
				.collect(Collectors.toList());
	}
}
