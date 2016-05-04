package uk.co.saiman.experiment.impl;

import java.util.Objects;

import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentType;

public class RootExperimentType implements ExperimentType<ExperimentConfiguration> {
	private final ExperimentWorkspaceImpl workspace;

	public RootExperimentType(ExperimentWorkspaceImpl workspace) {
		this.workspace = workspace;
	}

	@Override
	public String getName() {
		return "Experiment root";
	}

	@Override
	public ExperimentConfiguration createConfiguration(ExperimentNode<ExperimentConfiguration> forNode) {
		return new ExperimentConfiguration() {
			private String name;
			private String notes;

			@Override
			public void setNotes(String notes) {
				this.notes = notes;
			}

			@Override
			public void setName(String name) {
				if (Objects.equals(name, this.name))
					return;

				if (name != null) {
					// TODO check name is valid (i.e. def valid in a path)

					if (workspace.getRootExperiments().stream().anyMatch(e -> name.equals(e.configuration().getName()))) {
						// TODO already exists in workspace error!
					}

					// TODO if data already exists on disk

					if (this.name != null) {
						// TODO move from old location
					}
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
	public ExperimentConfiguration updateConfiguration(ExperimentConfiguration currentState,
			ExperimentConfiguration configuration) {
		currentState.set(configuration);
		return currentState;
	}

	@Override
	public void execute(ExperimentNode<ExperimentConfiguration> node) {
		// TODO create location
	}
}
