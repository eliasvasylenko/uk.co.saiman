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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentLifecycleState;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentResult;
import uk.co.saiman.experiment.ExperimentResultType;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.ExperimentWorkspace;
import uk.co.saiman.experiment.RootExperiment;
import uk.co.strangeskies.utilities.ObservableProperty;
import uk.co.strangeskies.utilities.ObservablePropertyImpl;
import uk.co.strangeskies.utilities.ObservableValue;

/**
 * Reference implementation of {@link ExperimentNode}.
 * 
 * @author Elias N Vasylenko
 * @param <T>
 *          the type of the experiment type
 * @param <S>
 *          the type of the data describing the experiment configuration
 */
public class ExperimentNodeImpl<T extends ExperimentType<S>, S> implements ExperimentNode<T, S> {
	private final ExperimentWorkspaceImpl workspace;
	private final T type;
	private final ExperimentNodeImpl<?, ?> parent;

	private final List<ExperimentNodeImpl<?, ?>> children;

	private final ObservableProperty<ExperimentLifecycleState, ExperimentLifecycleState> lifecycleState;
	private final S state;

	private HashMap<ExperimentResultType<? super S, ?>, ExperimentResultImpl<S, ?>> results;

	/**
	 * Try to create a new experiment node of the given type, and with the given
	 * parent.
	 * 
	 * @param type
	 *          the type of the experiment
	 * @param parent
	 *          the parent of the experiment
	 */
	protected ExperimentNodeImpl(T type, ExperimentNodeImpl<?, ?> parent) {
		this(type, parent.workspace, parent);

		if (!type.mayComeAfter(parent)) {
			throw new ExperimentException(workspace.getText().exception().typeMayNotSucceed(type, this));
		}
		parent.getAncestorsImpl().filter(a -> !a.type.mayComeBefore(parent, type)).forEach(a -> {
			throw new ExperimentException(workspace.getText().exception().typeMayNotSucceed(type, a));
		});

		parent.children.add(this);
	}

	protected ExperimentNodeImpl(T type, ExperimentWorkspaceImpl workspace) {
		this(type, workspace, null);
	}

	private ExperimentNodeImpl(T type, ExperimentWorkspaceImpl workspace, ExperimentNodeImpl<?, ?> parent) {
		this.workspace = workspace;
		this.type = type;
		this.parent = parent;

		children = new ArrayList<>();

		lifecycleState = ObservablePropertyImpl.over(ExperimentLifecycleState.PREPARATION);
		state = type.createState(this);

		results = new HashMap<>();
		getType().getResultTypes().forEach(r -> results.put(r, new ExperimentResultImpl<>(this, r)));
	}

	protected Stream<ExperimentNodeImpl<?, ?>> getAncestorsImpl() {
		return getAncestors().map(a -> (ExperimentNodeImpl<?, ?>) a);
	}

	@Override
	public S getState() {
		return state;
	}

	@Override
	public ExperimentWorkspace getExperimentWorkspace() {
		return workspace;
	}

	@Override
	public Path getExperimentDataPath() {
		return parent.getExperimentDataPath().resolve(getIndex() + "_" + type.getName());
	}

	@Override
	public T getType() {
		return type;
	}

	protected Optional<ExperimentNodeImpl<?, ?>> getParentImpl() {
		return Optional.ofNullable(parent);
	}

	@Override
	public Optional<ExperimentNode<?, ?>> getParent() {
		return Optional.ofNullable(parent);
	}

	protected ExperimentNodeImpl<RootExperiment, ExperimentConfiguration> getRootImpl() {
		return (ExperimentNodeImpl<RootExperiment, ExperimentConfiguration>) ExperimentNode.super.getRoot();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void remove() {
		assertAvailable();

		if (parent != null) {
			if (!parent.children.remove(this)) {
				throw new ExperimentException(workspace.getText().exception().experimentDoesNotExist(this));
			}
		} else {
			if (!workspace.removeRootExperiment((ExperimentNode<?, ExperimentConfiguration>) this)) {
				throw new ExperimentException(workspace.getText().exception().experimentDoesNotExist(this));
			}
		}

		setDisposed();
	}

	private void setDisposed() {
		lifecycleState.set(ExperimentLifecycleState.DISPOSED);
		for (ExperimentNodeImpl<?, ?> child : children) {
			child.setDisposed();
		}
	}

	protected void assertAvailable() {
		if (lifecycleState.get() == ExperimentLifecycleState.DISPOSED) {
			throw new ExperimentException(workspace.getText().exception().experimentIsDisposed(this));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Stream<ExperimentNode<?, ?>> getChildren() {
		return (Stream<ExperimentNode<?, ?>>) (Object) getChildrenImpl();
	}

	protected Stream<ExperimentNodeImpl<?, ?>> getChildrenImpl() {
		return children.stream();
	}

	@Override
	public Stream<ExperimentType<?>> getAvailableChildExperimentTypes() {
		return workspace.getRegisteredExperimentTypes()
				.filter(type -> this.type.mayComeBefore(this, type) && type.mayComeAfter(this));
	}

	@Override
	public <U, E extends ExperimentType<U>> ExperimentNode<E, U> addChild(E childType) {
		assertAvailable();

		return new ExperimentNodeImpl<>(childType, this);
	}

	@Override
	public ObservableValue<ExperimentLifecycleState> lifecycleState() {
		return lifecycleState;
	}

	@Override
	public String toString() {
		return type.getName() + " [" + lifecycleState + "]";
	}

	@Override
	public void process() {
		workspace.process(this);
	}

	@Override
	public boolean tryProcess() {
		return workspace.tryProcess(this);
	}

	protected boolean execute() {
		lifecycleState.set(ExperimentLifecycleState.PROCESSING);

		try {
			getType().execute(this);

			lifecycleState.set(ExperimentLifecycleState.COMPLETION);

			return true;
		} catch (Exception e) {
			lifecycleState.set(ExperimentLifecycleState.FAILURE);

			return false;
		}
	}

	@Override
	public Stream<ExperimentResult<S, ?>> getResults() {
		return results.values().stream().map(t -> (ExperimentResult<S, ?>) t);
	}

	@Override
	public void clearResults() {
		results.values().forEach(r -> r.setData(null));
	}

	@Override
	public <U> void setResult(ExperimentResultType<? super S, U> resultType, U resultData) {
		getResult(resultType).setData(resultData);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U> ExperimentResultImpl<S, U> getResult(ExperimentResultType<? super S, U> resultType) {
		return (ExperimentResultImpl<S, U>) results.get(resultType);
	}
}
