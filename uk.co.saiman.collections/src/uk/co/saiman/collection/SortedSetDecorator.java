/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.collections.
 *
 * uk.co.saiman.collections is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.collections is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.collection;

import java.util.Comparator;
import java.util.SortedSet;

public interface SortedSetDecorator<E> extends SetDecorator<E>, SortedSet<E> {
	public static <E> SortedSet<E> over(SortedSet<E> component) {
		return new SortedSetDecorator<E>() {
			@Override
			public SortedSet<E> getComponent() {
				return component;
			}

			@Override
			public String toString() {
				return getComponent().toString();
			}

			@Override
			public int hashCode() {
				return getComponent().hashCode();
			}

			@Override
			public boolean equals(Object obj) {
				return getComponent().equals(obj);
			}
		};
	}

	@Override
	SortedSet<E> getComponent();

	@Override
	default Comparator<? super E> comparator() {
		return getComponent().comparator();
	}

	@Override
	default SortedSet<E> subSet(E fromElement, E toElement) {
		return getComponent().subSet(fromElement, toElement);
	}

	@Override
	default SortedSet<E> headSet(E toElement) {
		return getComponent().headSet(toElement);
	}

	@Override
	default SortedSet<E> tailSet(E fromElement) {
		return getComponent().tailSet(fromElement);
	}

	@Override
	default E first() {
		return getComponent().first();
	}

	@Override
	default E last() {
		return getComponent().last();
	}
}
