package uk.co.saiman.experiment.msapex.impl;

import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static uk.co.strangeskies.utilities.collection.StreamUtilities.streamOptional;
import static uk.co.strangeskies.utilities.collection.StreamUtilities.tryOptional;

import java.util.stream.Stream;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;

import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.ExperimentWorkspace;

public class ExperimentNodeAdapterFactory implements IAdapterFactory {
	private final IAdapterManager adapterManager;
	private final ExperimentWorkspace workspace;

	public ExperimentNodeAdapterFactory(IAdapterManager adapterManager, ExperimentWorkspace workspace) {
		this.adapterManager = adapterManager;
		this.workspace = workspace;
		adapterManager.registerAdapters(this, ExperimentNode.class);
	}

	public void unregister() {
		adapterManager.unregisterAdapters(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		ExperimentNode<?, ?> node = (ExperimentNode<?, ?>) adaptableObject;

		if (adapterType == ExperimentType.class) {
			return (T) node.getType();
		}

		if (adapterType == node.getType().getStateType().getRawType()) {
			return (T) node.getState();
		}

		return (T) adapterManager.loadAdapter(node.getState(), adapterType.getName());
	}

	@Override
	public Class<?>[] getAdapterList() {
		return concat(
				of(ExperimentType.class),
				workspace
						.getRegisteredExperimentTypes()
						.map(type -> type.getStateType().getRawType())
						.flatMap(this::getTransitive)).toArray(Class<?>[]::new);
	}

	public Stream<? extends Class<?>> getTransitive(Class<?> adapterType) {
		return concat(
				of(adapterType),
				of(adapterManager.computeAdapterTypes(adapterType)).distinct().flatMap(
						typeName -> streamOptional(tryOptional(() -> getClass().getClassLoader().loadClass(typeName)))));
	}
}
