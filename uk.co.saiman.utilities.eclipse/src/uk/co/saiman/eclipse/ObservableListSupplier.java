package uk.co.saiman.eclipse;

import org.eclipse.e4.core.di.suppliers.ExtendedObjectSupplier;
import org.eclipse.e4.core.di.suppliers.IObjectDescriptor;
import org.eclipse.e4.core.di.suppliers.IRequestor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ObservableListSupplier<T> extends ExtendedObjectSupplier {
	private final ObservableList<T> items = FXCollections.observableArrayList();

	@Override
	public ObservableList<T> get(IObjectDescriptor descriptor, IRequestor requestor, boolean track, boolean group) {
		return FXCollections.unmodifiableObservableList(items);
	}

	public void addItem(T item) {
		items.add(item);
	}

	public void removeItem(T item) {
		items.remove(item);
	}
}
