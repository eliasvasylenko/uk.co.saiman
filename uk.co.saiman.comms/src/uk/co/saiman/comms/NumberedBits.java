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

import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class NumberedBits {
	public static final String PREFIX_SEPARATOR = "_";

	private final String prefix;
	private final BitSet bits;
	private final int size;

	public NumberedBits(String prefix) {
		this(prefix, Byte.SIZE);
	}

	public NumberedBits(String prefix, int size) {
		this.prefix = prefix;
		this.bits = new BitSet(size);
		this.size = size;
	}

	public NumberedBits(String prefix, int size, byte[] bytes) {
		this.prefix = prefix;
		this.bits = BitSet.valueOf(bytes);
		this.size = size;
	}

	public boolean isSet(int index) {
		checkBounds(index);
		return bits.get(index);
	}

	private NumberedBits set(int index, boolean on) {
		checkBounds(index);
		bits.set(index, on);
		return this;
	}

	private void checkBounds(int index) {
		if (index >= size)
			throw new ArrayIndexOutOfBoundsException();
	}

	public NumberedBits withSet(int index, boolean on) {
		return new NumberedBits(prefix, size, getBytes()).set(index, on);
	}

	public NumberedBits withSet(int index) {
		return withSet(index, true);
	}

	public NumberedBits withUnset(int index) {
		return withSet(index, false);
	}

	public byte[] getBytes() {
		return Arrays.copyOf(bits.toByteArray(), (int) Math.ceil(size / (double) Byte.SIZE));
	}

	public int getCount() {
		return size;
	}

	public Map<String, Boolean> toMap() {
		Map<String, Boolean> map = new LinkedHashMap<>();

		for (int i = 0; i < size; i++) {
			map.put(prefix + PREFIX_SEPARATOR + i, isSet(i));
		}

		return map;
	}

	@Override
	public String toString() {
		return toMap().toString();
	}
}
