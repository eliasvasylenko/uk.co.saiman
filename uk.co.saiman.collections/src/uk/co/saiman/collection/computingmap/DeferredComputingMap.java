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
package uk.co.saiman.collection.computingmap;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class DeferredComputingMap<K, V> extends ComputingEntryHashMap<K, V> {
	public DeferredComputingMap(Function<K, V> computation) {
		this(computation, Executors.newFixedThreadPool(4));
	}

	public DeferredComputingMap(Function<K, V> computation, Executor executor) {
		super(computation, executor);
	}

	public DeferredComputingMap(ComputingEntryHashMap<K, V> other) {
		super(other);
	}

	@Override
	protected Entry<K, V> createEntry(K key) {
		return new DeferredEntry(key);
	}
}
