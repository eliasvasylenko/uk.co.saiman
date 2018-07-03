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

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface ByteConversionAnnotations {
  public static ByteConversionAnnotations collectAnnotations(AnnotatedElement element) {
    return new ByteConversionAnnotationsImpl(element);
  }

  <T extends Annotation> Optional<T> get(Class<T> type);

  <T extends Annotation> Stream<T> getAll(Class<T> type);

  Stream<Annotation> getAll();

  /**
   * Combine this set of annotations with another.
   * <p>
   * If both the receiving and given set contains annotations for the same class,
   * exclusive preference will be given to the receiver.
   * 
   * @param more
   *          a set of annotations to take in combination with the receiver
   * @return a set of annotations containing those from the receiver and given
   *         sets
   */
  ByteConversionAnnotations and(ByteConversionAnnotations more);
}

class ByteConversionAnnotationsImpl implements ByteConversionAnnotations {
  private final Map<Class<?>, Set<Annotation>> annotations;

  public ByteConversionAnnotationsImpl(AnnotatedElement element) {
    annotations = Stream
        .of(element.getAnnotations())
        .flatMap(this::getRepeatedAnnotations)
        .collect(
            toMap(
                Annotation::annotationType,
                Collections::singleton,
                (a, b) -> concat(a.stream(), b.stream()).collect(toSet())));
  }

  private Stream<Annotation> getRepeatedAnnotations(Annotation annotation) {
    try {
      Method value = annotation.annotationType().getMethod("value");
      if (value.getReturnType().isArray()) {

        Class<?> repeatedType = value.getReturnType().getComponentType();
        if (Annotation.class.isAssignableFrom(repeatedType)) {

          Repeatable repeatable = repeatedType.getAnnotation(Repeatable.class);
          if (repeatable != null && repeatable.value() == annotation.annotationType()) {
            return concat(
                Stream.of(annotation),
                Stream.of((Annotation[]) value.invoke(annotation)));
          }
        }
      }
    } catch (
        NoSuchMethodException
        | SecurityException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException e) {}
    return Stream.of(annotation);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Annotation> Optional<T> get(Class<T> type) {
    Set<Annotation> annotations = this.annotations.get(type);

    if (annotations != null && annotations.size() == 1) {
      return Optional.of((T) annotations.iterator().next());
    } else {
      return Optional.empty();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Annotation> Stream<T> getAll(Class<T> type) {
    Set<Annotation> annotations = this.annotations.get(type);

    if (annotations != null) {
      return annotations.stream().map(a -> (T) a);
    } else {
      return Stream.empty();
    }
  }

  @Override
  public Stream<Annotation> getAll() {
    return annotations.values().stream().flatMap(Set::stream);
  }

  @Override
  public ByteConversionAnnotations and(ByteConversionAnnotations more) {
    return new ByteConversionAnnotations() {
      @Override
      public Stream<Annotation> getAll() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public <T extends Annotation> Stream<T> getAll(Class<T> type) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public <T extends Annotation> Optional<T> get(Class<T> type) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public ByteConversionAnnotations and(ByteConversionAnnotations more) {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }
}
