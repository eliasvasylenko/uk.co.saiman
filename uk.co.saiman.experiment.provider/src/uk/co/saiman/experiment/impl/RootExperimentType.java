package uk.co.saiman.experiment.impl;

import java.util.Objects;

import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentType;

/**
 * The root experiment type implementation for {@link ExperimentWorkspaceImpl}.
 * 
 * @author Elias N Vasylenko
 */
public class RootExperimentType implements ExperimentType<ExperimentConfiguration> {
	private final ExperimentWorkspaceImpl workspace;

	protected RootExperimentType(ExperimentWorkspaceImpl workspace) {
		this.workspace = workspace;
	}

	@Override
	public String getName() {
		return "Experiment root";
	}

	@Override
	public ExperimentConfiguration createState(ExperimentNode<? extends ExperimentConfiguration> forNode) {
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

				if (workspace.getRootExperiments().stream().anyMatch(e -> name.equals(e.getState().getName()))) {
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
	public void execute(ExperimentNode<? extends ExperimentConfiguration> node) {
		// TODO create location
	}

	@Override
	public boolean mayComeAfter(ExperimentNode<?> parentNode) {
		return false;
	}

	@Override
	public boolean mayComeBefore(ExperimentNode<?> penultimateDescendantNode, ExperimentType<?> descendantNodeType) {
		return true;
	}
}
