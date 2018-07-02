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
package uk.co.saiman.bytes.conversion;

import static uk.co.saiman.bytes.BitArray.fromByteArray;

import java.util.function.Function;

import uk.co.saiman.bytes.BitArray;

public interface ByteConverter<T> {
  BitArray toBits(T object);

  default byte[] toBytes(T object) {
    return toBits(object).toByteArray();
  }

  T toObject(BitArray bits);

  default T toObject(byte[] bytes) {
    return toObject(fromByteArray(bytes));
  }

  static <T> ByteConverter<T> byteConverter(
      Class<T> type,
      Function<T, BitArray> toBits,
      Function<BitArray, T> toObject) {
    return new ByteConverter<T>() {
      @Override
      public BitArray toBits(T object) {
        return toBits.apply(object);
      }

      @Override
      public T toObject(BitArray bits) {
        return toObject.apply(bits);
      }
    };
  }

  default <U> ByteConverter<U> map(Function<T, U> toObject, Function<U, T> toBits) {
    ByteConverter<T> base = ByteConverter.this;
    return new ByteConverter<U>() {
      @Override
      public BitArray toBits(U object) {
        return base.toBits(toBits.apply(object));
      }

      @Override
      public U toObject(BitArray bits) {
        return toObject.apply(base.toObject(bits));
      }
    };
  }
}
