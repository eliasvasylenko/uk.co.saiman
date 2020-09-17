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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class ComputingHashMap<K, V> implements ComputingMap<K, V> {
	private final Map<K, V> map;
	private final Function<K, V> computation;

	public ComputingHashMap(Function<K, V> computation) {
		map = new HashMap<>();
		this.computation = computation;
	}

	@Override
	public V get(K key) {
		return map.get(key);
	}

	@Override
	public boolean put(K key) {
		if (map.containsKey(key))
			return false;
		return putGet(key) != null;
	}

	@Override
	public V putGet(K key, Consumer<V> wasPresent, Consumer<V> wasMissing) {
		V value = map.get(key);

		if (value == null) {
			map.put(key, computation.apply(key));
			wasMissing.accept(value);
		} else {
			wasPresent.accept(value);
		}

		return value;
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

	@Override
	public V removeGet(K key) {
		return map.remove(key);
	}

	@Override
	public boolean clear() {
		if (!map.isEmpty())
			return false;
		map.clear();
		return true;
	}
}
