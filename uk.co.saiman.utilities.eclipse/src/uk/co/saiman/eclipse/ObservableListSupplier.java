/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.utilities.eclipse.
 *
 * uk.co.saiman.utilities.eclipse is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.utilities.eclipse is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse;

import org.eclipse.e4.core.di.suppliers.ExtendedObjectSupplier;
import org.eclipse.e4.core.di.suppliers.IObjectDescriptor;
import org.eclipse.e4.core.di.suppliers.IRequestor;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ObservableListSupplier<T> extends ExtendedObjectSupplier {
	private final ObservableList<T> items = FXCollections.observableArrayList();

	@Override
	public ObservableList<T> get(IObjectDescriptor descriptor, IRequestor requestor, boolean track, boolean group) {
		return FXCollections.unmodifiableObservableList(items);
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	public void addItem(T item) {
		items.add(item);
	}

	public void removeItem(T item) {
		items.remove(item);
	}
}
