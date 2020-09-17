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

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.bytes.conversion.ByteConversionAnnotations.collectAnnotations;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import uk.co.saiman.bytes.BitArray;
import uk.co.saiman.bytes.conversion.ByteConversionAnnotations;
import uk.co.saiman.bytes.conversion.ByteConverter;
import uk.co.saiman.bytes.conversion.ByteConverterProvider;
import uk.co.saiman.reflection.token.AnnotatedTypeToken;

/**
 * This byte converter implementation is simply a dynamic wrapper which always
 * fetches and delegates to the most appropriate converter which is available
 * from the {@link ByteConverterServiceImpl service}.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the type of the object to be converted
 */
public class CompositeByteConverter<T> implements ByteConverter<T> {
  private final ByteConverterServiceImpl service;
  private final AnnotatedTypeToken<T> type;
  private final ByteConversionAnnotations annotations;

  private final Map<ByteConverterProvider, ByteConverter<T>> components;

  CompositeByteConverter(ByteConverterServiceImpl service, AnnotatedTypeToken<T> type) {
    this.service = service;
    this.type = type;
    this.components = new HashMap<>();
    this.annotations = findAnnotations();
    getConverter();
  }

  @Override
  public T toObject(BitArray bits) {
    return getConverter().toObject(bits);
  }

  @Override
  public BitArray toBits(T object) {
    return getConverter().toBits(object);
  }

  private Stream<ByteConverterProvider> getProviders() {
    return service.getProviders();
  }

  private ByteConverter<T> getConverter() {
    List<ByteConverterProvider> providers = getProviders().collect(toList());

    components.keySet().retainAll(providers);

    var converter = providers
        .stream()
        .map(this::getConverter)
        .filter(Objects::nonNull)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Cannot find converter for type " + type));

    return converter;
  }

  private ByteConverter<T> getConverter(ByteConverterProvider provider) {
    return components.computeIfAbsent(provider, this::createConverter);
  }

  @SuppressWarnings("unchecked")
  private ByteConverter<T> createConverter(ByteConverterProvider provider) {
    if (!annotations
        .getAll()
        .map(Annotation::annotationType)
        .allMatch(provider::supportsAnnotation)) {
      return null;
    }

    ByteConverter<?> converter = provider
        .getConverter(type.getAnnotatedType(), annotations, service);
    return (ByteConverter<T>) converter;
  }

  private ByteConversionAnnotations findAnnotations() {
    return collectAnnotations(type.getAnnotatedType())
        .and(collectAnnotations(type.getErasedType()));
  }
}
