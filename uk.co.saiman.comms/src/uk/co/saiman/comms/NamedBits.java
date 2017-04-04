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
 * This file is part of uk.co.saiman.comms.
 *
 * uk.co.saiman.comms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms;

import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class NamedBits<T extends Enum<T>> {
	private final BitSet bits;
	private final Class<T> enumClass;

	public NamedBits(Class<T> enumClass) {
		this.bits = new BitSet(enumClass.getEnumConstants().length);
		this.enumClass = enumClass;
	}

	public NamedBits(Class<T> enumClass, byte[] bytes) {
		this.bits = BitSet.valueOf(bytes);
		this.enumClass = enumClass;
	}

	protected BitSet getBitSet() {
		return (BitSet) bits.clone();
	}

	public Class<T> getBitClass() {
		return enumClass;
	}

	public boolean isSet(T t) {
		return bits.get(t.ordinal());
	}

	public NamedBits<T> withSet(T bit, boolean set) {
		NamedBits<T> copy = new NamedBits<>(enumClass, getBytes());
		copy.bits.set(bit.ordinal(), set);
		return copy;
	}

	public byte[] getBytes() {
		return bits.toByteArray();
	}

	public Map<T, Boolean> toMap() {
		Map<T, Boolean> map = new LinkedHashMap<>();
		for (T t : enumClass.getEnumConstants()) {
			map.put(t, isSet(t));
		}
		return map;
	}

	@Override
	public String toString() {
		return toMap().toString();
	}
}
