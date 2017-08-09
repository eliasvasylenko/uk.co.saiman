/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.collections.observable.
 *
 * uk.co.saiman.collections.observable is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.collections.observable is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.collection.observable;

import java.util.SortedSet;

import uk.co.saiman.collection.SortedSetDecorator;

public abstract class SynchronizedObservableSortedSet<S extends ObservableSortedSet<S, E>, E>
		extends SynchronizedObservableSet<S, E> implements SortedSetDecorator<E>, ObservableSortedSet<S, E> {
	static class SynchronizedObservableSortedSetImpl<E>
			extends SynchronizedObservableSet<SynchronizedObservableSortedSetImpl<E>, E> {
		SynchronizedObservableSortedSetImpl(ObservableSortedSet<?, E> component) {
			super(component);
		}

		@SuppressWarnings("unchecked")
		@Override
		public SynchronizedObservableSortedSetImpl<E> copy() {
			return new SynchronizedObservableSortedSetImpl<>(((ObservableSortedSet<?, E>) getComponent()).copy());
		}
	}

	public static <E> SynchronizedObservableSortedSetImpl<E> over(ObservableSortedSet<?, E> component) {
		return new SynchronizedObservableSortedSetImpl<>(component);
	}

	protected SynchronizedObservableSortedSet(ObservableSet<?, E> component) {
		super(component);
	}

	@Override
	public SortedSet<E> getComponent() {
		return (SortedSet<E>) super.getComponent();
	}
}
