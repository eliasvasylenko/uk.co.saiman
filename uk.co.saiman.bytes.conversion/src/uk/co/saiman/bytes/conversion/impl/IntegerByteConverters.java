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
 * This file is part of uk.co.saiman.bytes.conversion.
 *
 * uk.co.saiman.bytes.conversion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.bytes.conversion is distributed in the hope that it will be useful,
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
import static uk.co.saiman.bytes.conversion.ByteConverter.byteConverter;
import static uk.co.saiman.reflection.Types.getErasedType;
import static uk.co.saiman.reflection.Types.unwrapPrimitive;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.Optional;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.bytes.Endianness;
import uk.co.saiman.bytes.conversion.ByteConversionAnnotations;
import uk.co.saiman.bytes.conversion.ByteConverter;
import uk.co.saiman.bytes.conversion.ByteConverterProvider;
import uk.co.saiman.bytes.conversion.ByteConverterService;
import uk.co.saiman.bytes.conversion.Order;
import uk.co.saiman.bytes.conversion.Size;

@Component
public class IntegerByteConverters implements ByteConverterProvider {
  @Override
  public ByteConverter<?> getConverter(
      AnnotatedType type,
      ByteConversionAnnotations annotations,
      ByteConverterService converters) {
    Class<?> erasedType = getErasedType(type.getType());

    Optional<Integer> size = annotations.get(Size.class).map(Size::value);
    Endianness endianness = annotations.get(Order.class).map(Order::value).orElse(BIG_ENDIAN);

    int bits;
    Class<? extends Number> primitive;
    Function<Number, Number> asNumber;

    if (byte.class == unwrapPrimitive(erasedType)) {
      bits = size.orElse(Byte.SIZE);
      primitive = byte.class;
      asNumber = Number::byteValue;

    } else if (short.class == unwrapPrimitive(erasedType)) {
      bits = size.orElse(Short.SIZE);
      primitive = short.class;
      asNumber = Number::shortValue;

    } else if (int.class == unwrapPrimitive(erasedType)) {
      bits = size.orElse(Integer.SIZE);
      primitive = int.class;
      asNumber = Number::intValue;

    } else if (long.class == unwrapPrimitive(erasedType)) {
      bits = size.orElse(Long.SIZE);
      primitive = long.class;
      asNumber = Number::longValue;

    } else {
      return null;

    }

    return getConverter(primitive, asNumber, bits, endianness);
  }

  @SuppressWarnings("unchecked")
  public <T extends Number> ByteConverter<T> getConverter(
      Class<T> primitive,
      Function<? super Number, ? extends Number> asNumber,
      int bits,
      Endianness endianness) {
    return byteConverter(
        primitive,
        b -> fromNumber(b.longValue(), bits, endianness),
        a -> (T) asNumber.apply(a.toNumber(bits, endianness)));
  }

  @Override
  public boolean supportsAnnotation(Class<? extends Annotation> annotationType) {
    return annotationType == Size.class || annotationType == Order.class;
  }
}
