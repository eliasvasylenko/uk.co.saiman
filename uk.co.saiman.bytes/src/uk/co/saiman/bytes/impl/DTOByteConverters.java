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

import static java.util.Collections.synchronizedMap;
import static uk.co.saiman.reflection.token.AnnotatedTypeToken.forType;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.bytes.conversion.ByteConversionAnnotations;
import uk.co.saiman.bytes.conversion.ByteConverterProvider;
import uk.co.saiman.bytes.conversion.ByteConverterService;
import uk.co.saiman.bytes.conversion.Bytes;
import uk.co.saiman.bytes.conversion.DTO;

/**
 * TODO This should become safer and simpler for consumers after data classes
 * are introduced by Project Amber. Fields will almost certainly retain ordering
 * in the class file, so more sensible default bit-layouts will be possible
 * without throwing annotations around everywhere. It will also lend a little
 * more weight to the design choice of having plain data-carriers with public
 * fields by embedding the concept in the language.
 */
@Component
public class DTOByteConverters implements ByteConverterProvider {
  private Map<AnnotatedType, DTOByteConverter<?>> typeConverters = synchronizedMap(new HashMap<>());

  @Override
  public boolean supportsAnnotation(Class<? extends Annotation> annotationType) {
    return annotationType == DTO.class || annotationType == Bytes.class;
  }

  @Override
  public DTOByteConverter<?> getConverter(
      AnnotatedType type,
      ByteConversionAnnotations annotations,
      ByteConverterService converters) {
    if (!annotations.get(DTO.class).isPresent())
      return null;

    return (DTOByteConverter<?>) typeConverters
        .computeIfAbsent(type, t -> new DTOByteConverter<>(forType(t), annotations, converters));
  }
}
