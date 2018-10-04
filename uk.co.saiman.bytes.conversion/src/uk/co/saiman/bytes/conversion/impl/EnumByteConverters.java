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
package uk.co.saiman.bytes.conversion.impl;

import static uk.co.saiman.bytes.BitArray.fromNumber;
import static uk.co.saiman.bytes.Endianness.BIG_ENDIAN;
import static uk.co.saiman.reflection.Types.getErasedType;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.bytes.BitArray;
import uk.co.saiman.bytes.Endianness;
import uk.co.saiman.bytes.conversion.ByteConversionAnnotations;
import uk.co.saiman.bytes.conversion.ByteConverter;
import uk.co.saiman.bytes.conversion.ByteConverterProvider;
import uk.co.saiman.bytes.conversion.ByteConverterService;
import uk.co.saiman.bytes.conversion.Order;
import uk.co.saiman.bytes.conversion.Size;

@Component
public class EnumByteConverters implements ByteConverterProvider {
  @SuppressWarnings("unchecked")
  @Override
  public ByteConverter<?> getConverter(
      AnnotatedType type,
      ByteConversionAnnotations annotations,
      ByteConverterService converters) {
    Class<?> erasedType = getErasedType(type.getType());
    if (Enum.class.isAssignableFrom(erasedType)) {
      Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) erasedType;

      int size = annotations.get(Size.class).map(Size::value).orElse(32);
      Endianness endianness = annotations.get(Order.class).map(Order::value).orElse(BIG_ENDIAN);

      return new EnumBitConverter<>(enumType, size, endianness);
    }
    return null;
  }

  class EnumBitConverter<T extends Enum<?>> implements ByteConverter<T> {
    private final Class<T> type;
    private final int size;
    private final Endianness endianness;

    public EnumBitConverter(Class<T> type, int size, Endianness endianness) {
      this.type = type;
      this.size = size;
      this.endianness = endianness;
    }

    @Override
    public T toObject(BitArray bits) {
      return type.getEnumConstants()[(int) bits.toNumber(size, endianness)];
    }

    @Override
    public BitArray toBits(T object) {
      return fromNumber(object.ordinal(), size, endianness);
    }
  }

  @Override
  public boolean supportsAnnotation(Class<? extends Annotation> annotationType) {
    return annotationType == Size.class || annotationType == Order.class;
  }
}
