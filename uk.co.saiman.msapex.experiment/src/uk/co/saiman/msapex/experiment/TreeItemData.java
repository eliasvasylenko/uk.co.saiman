package uk.co.saiman.msapex.experiment;

public class TreeItemData<T> {
	private final TreeItemType<T> type;
	private final T data;

	public TreeItemData(TreeItemType<T> type, T data) {
		this.type = type;
		this.data = data;
	}

	public TreeItemType<T> getItemType() {
		return type;
	}

	public T getData() {
		return data;
	}

	@Override
	public String toString() {
		String text = getItemType().getText(getData());
		String supplemental = getItemType().getSupplementalText(getData());

		if (supplemental != null)
			text += " - " + supplemental;

		return text;
	}
}
