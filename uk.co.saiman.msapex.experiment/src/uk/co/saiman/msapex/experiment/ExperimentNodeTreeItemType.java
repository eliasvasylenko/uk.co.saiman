package uk.co.saiman.msapex.experiment;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.e4.ui.services.EMenuService;

import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.strangeskies.fx.TreeItemData;
import uk.co.strangeskies.reflection.TypeParameter;
import uk.co.strangeskies.reflection.TypeToken;

public class ExperimentNodeTreeItemType<T> extends ExperimentTreeItemType<ExperimentNode<T>> {
	private final ExperimentType<T> experimentType;

	public ExperimentNodeTreeItemType(ExperimentType<T> experimentType, EMenuService menuService) {
		super(menuService);
		this.experimentType = experimentType;
	}

	public <U> TreeItemData<ExperimentNode<U>> getItemData(ExperimentNode<U> child) {
		/*
		 * TODO we somehow need to fetch the best available published service here!
		 */
		return new TreeItemData<>(new ExperimentNodeTreeItemType<>(child.getType(), getMenuService()), child);
	}

	@Override
	public TypeToken<ExperimentNode<T>> getDataType() {
		return new TypeToken<ExperimentNode<T>>() {}.withTypeArgument(new TypeParameter<T>() {},
				experimentType.getStateType());
	}

	@Override
	public boolean hasChildren(ExperimentNode<T> data) {
		return !data.getChildren().isEmpty();
	}

	@Override
	public List<TreeItemData<?>> getChildren(ExperimentNode<T> data) {
		return data.getChildren().stream().map(c -> getItemData(c)).collect(Collectors.toList());
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
