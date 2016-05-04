package uk.co.saiman.experiment.impl;

import static java.util.Collections.unmodifiableList;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import uk.co.saiman.experiment.ExperimentLifecycleState;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.ExperimentWorkspace;

public class ExperimentNodeImpl<S> implements ExperimentNode<S> {
	private final ExperimentWorkspaceImpl workspace;
	private final ExperimentType<S> type;
	private final ExperimentNodeImpl<?> parent;

	private final List<ExperimentNode<?>> children;

	private ExperimentLifecycleState lifecycleState;
	private S state;

	public ExperimentNodeImpl(ExperimentWorkspaceImpl workspace, ExperimentType<S> type, ExperimentNodeImpl<?> parent) {
		this.workspace = workspace;
		this.type = type;
		this.parent = parent;

		children = new ArrayList<>();

		lifecycleState = ExperimentLifecycleState.PREPARATION;
		state = type.createConfiguration(this);
	}

	@Override
	public S configuration() {
		return state;
	}

	@Override
	public void configure(S configuration) {
		state = type.updateConfiguration(state, configuration);
	}

	@Override
	public ExperimentWorkspace getExperimentWorkspace() {
		return workspace;
	}

	@Override
	public Path getExperimentDataRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExperimentType<S> type() {
		return type;
	}

	@Override
	public Optional<ExperimentNode<?>> parent() {
		return Optional.ofNullable(parent);
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ExperimentNode<?>> children() {
		return unmodifiableList(children);
	}

	@Override
	public Set<ExperimentType<?>> getAvailableChildExperimentTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> ExperimentNode<T> addChild(ExperimentType<T> childType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExperimentLifecycleState lifecycleState() {
		return lifecycleState;
	}
}
