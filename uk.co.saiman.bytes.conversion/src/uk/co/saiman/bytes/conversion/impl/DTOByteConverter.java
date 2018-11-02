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

import static uk.co.saiman.collection.StreamUtilities.throwingMerger;
import static uk.co.saiman.reflection.token.AnnotatedTypeToken.forType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.bytes.BitArray;
import uk.co.saiman.bytes.conversion.ByteConversionAnnotations;
import uk.co.saiman.bytes.conversion.ByteConversionException;
import uk.co.saiman.bytes.conversion.ByteConverter;
import uk.co.saiman.bytes.conversion.ByteConverterService;
import uk.co.saiman.bytes.conversion.Bytes;
import uk.co.saiman.bytes.conversion.Offset;
import uk.co.saiman.reflection.Types;
import uk.co.saiman.reflection.token.AnnotatedTypeToken;

public class DTOByteConverter<T> implements ByteConverter<T> {
  private static class FieldConverter {
    private final Field field;
    private final ByteConverter<Object> byteConverter;
    private final int offset;

    @SuppressWarnings("unchecked")
    public FieldConverter(ByteConverterService converterService, Field field) {
      this.field = field;
      this.byteConverter = (ByteConverter<Object>) converterService
          .getConverter(forType(field.getAnnotatedType()));

      this.offset = Optional
          .ofNullable(field.getAnnotation(Offset.class))
          .map(Offset::value)
          .orElse(0);
    }

    public void put(Object object, BitArray bits) {
      Object value = byteConverter.toObject(bits.slice(offset, bits.length()));

      try {
        field.set(object, value);
      } catch (IllegalArgumentException | IllegalAccessException e) {
        throw new ByteConversionException("Cannot write to DTO field", e);
      }
    }

    public BitArray get(Object object, BitArray bits) {
      Object value;
      try {
        value = field.get(object);
      } catch (IllegalArgumentException | IllegalAccessException e) {
        throw new ByteConversionException("Cannot read from DTO field", e);
      }

      return bits.splice(offset, byteConverter.toBits(value));
    }
  }

  private final Constructor<?> constructor;

  private final Map<Field, FieldConverter> fieldConverters;

  private final int byteCount;

  DTOByteConverter(
      AnnotatedTypeToken<T> type,
      ByteConversionAnnotations annotations,
      ByteConverterService converterService) {
    Class<?> rawType = Types.getErasedType(type.getType());

    try {
      this.constructor = rawType.getDeclaredConstructor();
    } catch (NoSuchMethodException | SecurityException e) {
      throw new ByteConversionException("Cannot instantiate DTO", e);
    }

    this.fieldConverters = new HashMap<>();
    for (Field field : rawType.getFields()) {
      fieldConverters.put(field, new FieldConverter(converterService, field));
    }

    byteCount = annotations.get(Bytes.class).map(Bytes::value).orElse(-1);
  }

  public Stream<Field> getFields() {
    return fieldConverters.keySet().stream();
  }

  public FieldConverter getFieldConverter(Field field) {
    return fieldConverters.get(field);
  }

  @SuppressWarnings("unchecked")
  @Override
  public T toObject(BitArray bitSet) {
    T object;
    try {
      object = (T) constructor.newInstance();
    } catch (
        InstantiationException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException e) {
      throw new ByteConversionException("Cannot instantiate DTO", e);
    }

    getFields().map(this::getFieldConverter).forEach(c -> c.put(object, bitSet));

    return object;
  }

  @Override
  public BitArray toBits(T object) {
    BitArray bitSet = getFields()
        .map(this::getFieldConverter)
        .reduce(new BitArray(0), (b, c) -> c.get(object, b), throwingMerger());

    if (byteCount > 0) {
      bitSet.resize(byteCount * Byte.SIZE);
    }

    return bitSet;
  }
}