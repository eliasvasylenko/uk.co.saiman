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

import java.util.Objects;

import uk.co.saiman.experiment.ExperimentConfiguration;
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
		return "Experiment root";
	}

	@Override
	public ExperimentConfiguration createState(ExperimentNode<?, ? extends ExperimentConfiguration> forNode) {
		return new ExperimentConfiguration() {
			private String name;
			private String notes;

			@Override
			public void setNotes(String notes) {
				this.notes = notes;
			}

			@Override
			public void setName(String name) {
				if (name == null)
					throw new IllegalArgumentException("Experiment name must be non-null");
				if (Objects.equals(name, this.name))
					return;

				// TODO check name is valid (i.e. def valid in a path)

				if (workspace.getRootExperiments().anyMatch(e -> name.equals(e.getState().getName()))) {
					// TODO already exists in workspace error!
				}

				// TODO if data already exists on disk

				if (this.name != null) {
					// TODO move from old location
				}

				this.name = name;
			}

			@Override
			public String getNotes() {
				return notes;
			}

			@Override
			public String getName() {
				return name;
			}
		};
	}

	@Override
	public void execute(ExperimentNode<?, ? extends ExperimentConfiguration> node) {
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
