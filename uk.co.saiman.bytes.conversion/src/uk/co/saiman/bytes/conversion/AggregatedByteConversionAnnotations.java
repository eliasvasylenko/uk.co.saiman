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

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;
import static uk.co.saiman.collection.StreamUtilities.throwingMerger;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

final class AggregatedByteConversionAnnotations implements ByteConversionAnnotations {
  private final ByteConversionAnnotations first;
  private final ByteConversionAnnotations second;

  AggregatedByteConversionAnnotations(
      ByteConversionAnnotations first,
      ByteConversionAnnotations second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public Stream<Annotation> getAll() {
    Set<Class<?>> repeats = new HashSet<>();
    return concat(
        first.getAll().peek(a -> repeats.add(a.annotationType())),
        second.getAll().filter(a -> repeats.add(a.annotationType())));
  }

  @Override
  public <T extends Annotation> Stream<T> getAll(Class<T> type) {
    if (first.getAll(type).findAny().isPresent()) {
      return first.getAll(type);
    } else {
      return second.getAll(type);
    }
  }

  @Override
  public <T extends Annotation> Optional<T> get(Class<T> type) {
    return getAll(type).reduce(throwingMerger());
  }

  @Override
  public ByteConversionAnnotations and(ByteConversionAnnotations more) {
    return new AggregatedByteConversionAnnotations(this, more);
  }

  @Override
  public String toString() {
    return getAll().distinct().map(Annotation::toString).collect(joining(", ", "(", ")"));
  }
}