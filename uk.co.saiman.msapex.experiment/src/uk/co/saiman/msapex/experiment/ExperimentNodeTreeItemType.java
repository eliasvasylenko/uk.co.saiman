package uk.co.saiman.msapex.experiment;

import java.util.List;
import java.util.stream.Collectors;

import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.strangeskies.reflection.TypeParameter;
import uk.co.strangeskies.reflection.TypeToken;

public class ExperimentNodeTreeItemType<T> implements TreeItemType<ExperimentNode<T>> {
	private final ExperimentType<T> experimentType;

	public ExperimentNodeTreeItemType(ExperimentType<T> experimentType) {
		this.experimentType = experimentType;
	}

	public static <U> TreeItemData<ExperimentNode<U>> getItemData(ExperimentNode<U> child) {
		return new TreeItemData<>(new ExperimentNodeTreeItemType<>(child.type()), child);
	}

	@Override
	public TypeToken<ExperimentNode<T>> getDataType() {
		return new TypeToken<ExperimentNode<T>>() {}.withTypeArgument(new TypeParameter<T>() {},
				experimentType.getStateType());
	}

	@Override
	public boolean hasChildren(ExperimentNode<T> data) {
		return !data.children().isEmpty();
	}

	@Override
	public List<TreeItemData<?>> getChildren(ExperimentNode<T> data) {
		return data.children().stream().map(c -> getItemData(c)).collect(Collectors.toList());
	}

	@Override
	public String getText(ExperimentNode<T> data) {
		return experimentType.getName();
	}

	@Override
	public String getSupplementalText(ExperimentNode<T> data) {
		return data.toString();
	}
}
