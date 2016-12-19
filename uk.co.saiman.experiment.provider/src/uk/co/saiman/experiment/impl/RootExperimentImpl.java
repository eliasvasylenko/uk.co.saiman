/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,-========\     ,`===\    /========== \
 *      /== \___/== \  ,`==.== \   \__/== \___\/
 *     /==_/____\__\/,`==__|== |     /==  /
 *     \========`. ,`========= |    /==  /
 *   ___`-___)== ,`== \____|== |   /==  /
 *  /== \__.-==,`==  ,`    |== '__/==  /_
 *  \======== /==  ,`      |== ========= \
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

import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentConfigurationContext;
import uk.co.saiman.experiment.ExperimentExecutionContext;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.RootExperiment;

/**
 * The root experiment type implementation for {@link ExperimentWorkspaceImpl}.
 * 
 * @author Elias N Vasylenko
 */
public class RootExperimentImpl implements RootExperiment {
	private final ExperimentWorkspaceImpl workspace;

	protected RootExperimentImpl(ExperimentWorkspaceImpl workspace) {
		this.workspace = workspace;
	}

	@Override
	public String getName() {
		return workspace.getText().experimentRoot().toString();
	}

	@Override
	public ExperimentConfiguration createState(ExperimentConfigurationContext<ExperimentConfiguration> forNode) {
		return new ExperimentConfiguration() {
			private String notes;

			@Override
			public String getName() {
				return forNode.getId();
			}

			@Override
			public void setName(String name) {
				forNode.setId(name);
			}

			@Override
			public String getNotes() {
				return notes;
			}

			@Override
			public void setNotes(String notes) {
				this.notes = notes;
			}
		};
	}

	@Override
	public void execute(ExperimentExecutionContext<ExperimentConfiguration> context) {
		// TODO create location
	}

	@Override
	public boolean mayComeAfter(ExperimentNode<?, ?> parentNode) {
		return false;
	}

	@Override
	public boolean mayComeBefore(ExperimentNode<?, ?> penultimateDescendantNode, ExperimentType<?> descendantNodeType) {
		return true;
	}
}
