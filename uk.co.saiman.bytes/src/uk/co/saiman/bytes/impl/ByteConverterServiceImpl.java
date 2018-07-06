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
package uk.co.saiman.bytes.impl;

import static java.util.Collections.synchronizedSortedMap;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;
import static uk.co.saiman.reflection.token.AnnotatedTypeToken.forType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.bytes.conversion.ByteConverterProvider;
import uk.co.saiman.bytes.conversion.ByteConverterService;
import uk.co.saiman.reflection.token.AnnotatedTypeToken;

@Component
public class ByteConverterServiceImpl implements ByteConverterService {
  volatile Map<ServiceReference<ByteConverterProvider>, ByteConverterProvider> byteConverterProviders = synchronizedSortedMap(
      new TreeMap<>());

  private final Map<AnnotatedTypeToken<?>, CompositeByteConverter<?>> typedByteConverters = Collections
      .synchronizedMap(new HashMap<>());

  @Reference(policy = DYNAMIC, policyOption = GREEDY, cardinality = MULTIPLE)
  void addConverter(
      ServiceReference<ByteConverterProvider> reference,
      ByteConverterProvider converter) {
    byteConverterProviders.put(reference, converter);
  }

  void removeConverter(
      ServiceReference<ByteConverterProvider> reference,
      ByteConverterProvider converter) {
    byteConverterProviders.remove(reference, converter);
  }

  public Stream<ByteConverterProvider> getProviders() {
    return byteConverterProviders.values().stream();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> CompositeByteConverter<T> getConverter(Class<T> type) {
    CompositeByteConverter<?> converter = getConverter(forType(new AnnotatedClassUse(type)));
    return (CompositeByteConverter<T>) converter;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> CompositeByteConverter<T> getConverter(AnnotatedTypeToken<T> type) {
    return (CompositeByteConverter<T>) typedByteConverters
        .computeIfAbsent(type, t -> new CompositeByteConverter<>(this, t));
  }
}
