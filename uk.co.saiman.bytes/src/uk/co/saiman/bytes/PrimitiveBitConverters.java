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

import static uk.co.saiman.reflection.Types.getErasedType;
import static uk.co.saiman.reflection.Types.unwrapPrimitive;
import static uk.co.saiman.reflection.Types.wrapPrimitive;

import java.lang.reflect.Type;

public class PrimitiveBitConverters implements BitConverterFactory {
  @Override
  public BitConverter<?> getBitConverter(Type type) {
    Class<?> erasedType = getErasedType(type);

    if (matchPrimitive(byte.class, erasedType)) {
      return new Bytes();

    } else if (matchPrimitive(short.class, erasedType)) {
      return new Shorts();

    } else if (matchPrimitive(int.class, erasedType)) {
      return new Ints();

    } else if (matchPrimitive(boolean.class, erasedType)) {
      return new Booleans();
    }

    return null;
  }

  private boolean matchPrimitive(Class<?> primitive, Class<?> erasedType) {
    return wrapPrimitive(primitive).isAssignableFrom(erasedType)
        || unwrapPrimitive(primitive).isAssignableFrom(erasedType);
  }

  public static class Booleans implements BitConverter<Boolean> {
    @Override
    public int getDefaultBits() {
      return 1;
    }

    @Override
    public Boolean toObject(BitArray bits) {
      return bits.resize(1).get(0);
    }

    @Override
    public BitArray toBits(Boolean object, int size) {
      return new BitArray(1).with(0, object).resize(size);
    }
  }

  public static class Bytes implements BitConverter<Byte> {
    @Override
    public int getDefaultBits() {
      return Byte.SIZE;
    }

    @Override
    public Byte toObject(BitArray bits) {
      return bits.toByte();
    }

    @Override
    public BitArray toBits(Byte object, int size) {
      return BitArray.fromByte(object).resize(size);
    }
  }

  public static class Shorts implements BitConverter<Short> {
    @Override
    public int getDefaultBits() {
      return Short.SIZE;
    }

    @Override
    public Short toObject(BitArray bits) {
      return bits.toShort();
    }

    @Override
    public BitArray toBits(Short object, int size) {
      return BitArray.fromShort(object).resize(size);
    }
  }

  public static class Ints implements BitConverter<Integer> {
    @Override
    public int getDefaultBits() {
      return Integer.SIZE;
    }

    @Override
    public Integer toObject(BitArray bits) {
      return bits.toInt();
    }

    @Override
    public BitArray toBits(Integer object, int size) {
      return BitArray.fromInt(object).resize(size);
    }
  }
}
