package uk.co.saiman.msapex.experiment;

import java.util.List;

import uk.co.strangeskies.reflection.TypeParameter;
import uk.co.strangeskies.reflection.TypeToken;

public interface TreeItemType<T> {
	default TypeToken<T> getDataType() {
		return TypeToken.over(getClass()).resolveSupertypeParameters(TreeItemType.class)
				.resolveTypeArgument(new TypeParameter<T>() {}).infer();
	}

	public boolean hasChildren(T data);

	public void addChildren(T data, List<TreeItemData<?>> children);

	default String getText(T data) {
		return data.toString();
	}

	default String getSupplementalText(T data) {
		return null;
	}
}
