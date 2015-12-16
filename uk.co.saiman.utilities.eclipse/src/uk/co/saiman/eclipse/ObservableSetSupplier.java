package uk.co.saiman.eclipse;

import org.eclipse.e4.core.di.suppliers.ExtendedObjectSupplier;
import org.eclipse.e4.core.di.suppliers.IObjectDescriptor;
import org.eclipse.e4.core.di.suppliers.IRequestor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

public class ObservableSetSupplier<T> extends ExtendedObjectSupplier {
	@SuppressWarnings("unchecked")
	private final ObservableSet<T> items = FXCollections.observableSet();

	@Override
	public ObservableSet<T> get(IObjectDescriptor descriptor, IRequestor requestor, boolean track, boolean group) {
		return FXCollections.unmodifiableObservableSet(items);
	}

	public void addItem(T item) {
		items.add(item);
	}

	public void removeItem(T item) {
		items.remove(item);
	}
}
