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
package uk.co.saiman.bytes.conversion;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;

/**
 * An interface for providing byte converter implementations for given types.
 * Typically users may implement this class to provide behavior to a
 * {@link ByteConverterService byte converter service}, and should not invoke
 * the methods of this interface themselves.
 * 
 * @author Elias N Vasylenko
 */
public interface ByteConverterProvider {
  /**
   * Test whether the provider is able to provide converters for types annotated
   * with the given {@link Convertible byte conversion annotation}.
   * <p>
   * The system guarantees to invoke this method with all byte conversion
   * annotations on a type before calling
   * {@link #getConverter(AnnotatedType, ByteConversionAnnotations, ByteConverterService)},
   * and only to pass annotations which are byte conversion annotations.
   * 
   * @param annotation a byte conversion annotation type
   * @return true if the provider is able to provide a converter for a type with
   *         an annotation of the given type
   */
  boolean supportsAnnotation(Class<? extends Annotation> annotationType);

  /**
   * Get or instantiate a converter which can deal with the given type. The
   * converter may further delegate tasks to other converters.
   * <p>
   * The system guarantees that null return values will be safely dealt with.
   * 
   * @param type        the type we wish to convert
   * @param annotations byte conversion annotations to consider, which may be
   *                    different from those present on the type
   * @param converters  the set of all converters, such that an implementation may
   *                    delegate
   * @return a converter for the given type, or null if the type is not supported
   */
  ByteConverter<?> getConverter(
      AnnotatedType type,
      ByteConversionAnnotations annotations,
      ByteConverterService converters);
}
