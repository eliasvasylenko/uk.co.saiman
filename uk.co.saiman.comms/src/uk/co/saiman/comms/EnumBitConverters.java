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

import static uk.co.saiman.comms.BitArray.fromInt;
import static uk.co.saiman.reflection.Types.getErasedType;

import java.lang.reflect.Type;

import org.osgi.service.component.annotations.Component;

@Component
public class EnumBitConverters implements BitConverterFactory {
	@SuppressWarnings("unchecked")
	@Override
	public BitConverter<?> getBitConverter(Type type) {
		if (Enum.class.isAssignableFrom(getErasedType(type))) {
			Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) (Class<?>) type;
			return new EnumBitConverter<>(enumType);
		}
		return null;
	}

	class EnumBitConverter<T extends Enum<?>> implements BitConverter<T> {
		private final Class<T> type;

		public EnumBitConverter(Class<T> type) {
			this.type = type;
		}

		private int log2(int number) {
			if (number <= 0)
				throw new IllegalArgumentException();
			return 31 - Integer.numberOfLeadingZeros(number);
		}

		@Override
		public int getDefaultBits() {
			return log2(type.getEnumConstants().length);
		}

		@Override
		public T toObject(BitArray bits) {
			return type.getEnumConstants()[bits.toInt()];
		}

		@Override
		public BitArray toBits(T object, int size) {
			return fromInt(object.ordinal()).resize(size);
		}
	}
}
