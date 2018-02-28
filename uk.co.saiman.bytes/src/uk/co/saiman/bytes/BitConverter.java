/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.bytes.
 *
 * uk.co.saiman.bytes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.bytes is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.bytes;

import java.util.function.Function;

public interface BitConverter<T> {
	int getDefaultBits();

	BitArray toBits(T object, int bits);

	T toObject(BitArray bits);

	default <U> BitConverter<U> map(Function<T, U> toObject, Function<U, T> toBits) {
		BitConverter<T> base = BitConverter.this;
		return new BitConverter<U>() {
			@Override
			public int getDefaultBits() {
				return base.getDefaultBits();
			}

			@Override
			public BitArray toBits(U object, int bits) {
				return base.toBits(toBits.apply(object), bits);
			}

			@Override
			public U toObject(BitArray bits) {
				return toObject.apply(base.toObject(bits));
			}
		};
	}

	default BitConverter<T> withDefaultBits(int bits) {
		BitConverter<T> base = BitConverter.this;
		return new BitConverter<T>() {
			@Override
			public int getDefaultBits() {
				return bits;
			}

			@Override
			public BitArray toBits(T object, int bits) {
				return base.toBits(object, bits);
			}

			@Override
			public T toObject(BitArray bits) {
				return base.toObject(bits);
			}
		};
	}
}
