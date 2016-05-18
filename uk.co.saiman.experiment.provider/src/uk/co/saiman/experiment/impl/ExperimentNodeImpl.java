package uk.co.saiman.experiment.impl;

import static java.util.Collections.unmodifiableList;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentLifecycleState;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.ExperimentWorkspace;

/**
 * Reference implementation of {@link ExperimentNode}.
 * 
 * @author Elias N Vasylenko
 * 
 * @param <S>
 *          the type of the data describing the experiment configuration
 */
public class ExperimentNodeImpl<S> implements ExperimentNode<S> {
	private final ExperimentWorkspaceImpl workspace;
	private final ExperimentType<S> type;
	private final ExperimentNodeImpl<?> parent;

	private final List<ExperimentNodeImpl<?>> children;

	private ExperimentLifecycleState lifecycleState;
	private S state;

	/**
	 * Try to create a new experiment node of the given type, and with the given
	 * parent.
	 * 
	 * @param type
	 *          the type of the experiment
	 * @param parent
	 *          the parent of the experiment
	 */
	protected ExperimentNodeImpl(ExperimentType<S> type, ExperimentNodeImpl<?> parent) {
		this(type, parent.workspace, parent);

		forEachAncestorExclusive(a -> {
			if (!a.type.mayComeBefore(parent, type) || !type.mayComeAfter(a)) {
				throw new ExperimentException(workspace.getText().typeMayNotSucceed(type, a));
			}
		});

		parent.children.add(this);
	}

	protected ExperimentNodeImpl(ExperimentType<S> type, ExperimentWorkspaceImpl workspace) {
		this(type, workspace, null);
	}

	private ExperimentNodeImpl(ExperimentType<S> type, ExperimentWorkspaceImpl workspace, ExperimentNodeImpl<?> parent) {
		this.workspace = workspace;
		this.type = type;
		this.parent = parent;

		children = new ArrayList<>();

		lifecycleState = ExperimentLifecycleState.PREPARATION;
		state = type.createState(this);
	}

	@Override
	public int getIndex() {
		return getParent().map(p -> p.getChildren().indexOf(this)).orElse(workspace.getRootExperiments().indexOf(this));
	}

	protected <T> Optional<T> forEachAncestorExclusive(Function<? super ExperimentNodeImpl<?>, T> action) {
		Optional<? extends ExperimentNodeImpl<?>> ancestor = getParentImpl();

		while (ancestor.isPresent()) {
			T result = action.apply(ancestor.get());

			if (result != null) {
				return Optional.of(result);
			}

			ancestor = ancestor.flatMap(ExperimentNodeImpl::getParentImpl);
		}

		return Optional.empty();
	}

	protected void forEachAncestorExclusive(Consumer<? super ExperimentNodeImpl<?>> action) {
		forEachAncestorExclusive(n -> {
			action.accept(n);
			return null;
		});
	}

	protected <T> Optional<T> forEachAncestorInclusive(Function<? super ExperimentNodeImpl<?>, T> action) {
		T result = action.apply(this);

		if (result != null) {
			return Optional.of(result);
		}

		return forEachAncestorExclusive(action);
	}

	protected void forEachAncestorInclusive(Consumer<? super ExperimentNodeImpl<?>> action) {
		forEachAncestorInclusive(n -> {
			action.accept(n);
			return null;
		});
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
	public Path getExperimentDataRoot() {
		return parent.getExperimentDataRoot().resolve(getIndex() + "_" + type.getName());
	}

	@Override
	public ExperimentType<S> getType() {
		return type;
	}

	protected Optional<ExperimentNodeImpl<?>> getParentImpl() {
		return Optional.ofNullable(parent);
	}

	@Override
	public Optional<ExperimentNode<?>> getParent() {
		return Optional.ofNullable(parent);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void remove() {
		assertAvailable();

		if (parent != null) {
			parent.children.remove(this);
		} else {
			workspace.removeRootExperiment((ExperimentNode<ExperimentConfiguration>) this);
		}

		setDisposed();
	}

	private void setDisposed() {
		lifecycleState = ExperimentLifecycleState.DISPOSED;
		for (ExperimentNodeImpl<?> child : children) {
			child.setDisposed();
		}
	}

	protected void assertAvailable() {
		if (lifecycleState == ExperimentLifecycleState.DISPOSED) {
			throw new ExperimentException(workspace.getText().experimentIsDisposed(this));
		}
	}

	@Override
	public List<ExperimentNode<?>> getChildren() {
		return unmodifiableList(children);
	}

	@Override
	public Set<ExperimentType<?>> getAvailableChildExperimentTypes() {
		Set<ExperimentType<?>> experimentTypes = new HashSet<>(workspace.getRegisteredExperimentTypes());

		experimentTypes.removeIf(type -> !this.type.mayComeBefore(this, type) || !type.mayComeAfter(this));

		return experimentTypes;
	}

	@Override
	public <T> ExperimentNode<T> addChild(ExperimentType<T> childType) {
		assertAvailable();

		return new ExperimentNodeImpl<>(childType, this);
	}

	@Override
	public ExperimentLifecycleState getLifecycleState() {
		return lifecycleState;
	}

	@Override
	public String toString() {
		return type.getName() + " [" + lifecycleState + "]";
	}
}
