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
 * This file is part of uk.co.saiman.observable.
 *
 * uk.co.saiman.observable is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.observable is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.observable;

import java.util.Objects;
import java.util.Optional;

public class ImmutableObservableValue<T> implements ObservableValue<T> {
  private final T value;

  public ImmutableObservableValue(T value) {
    this.value = Objects.requireNonNull(value);
  }

  @Override
  public T get() {
    return value;
  }

  @Override
  public Observable<Change<T>> changes() {
    return new EmptyObservable<>();
  }

  @Override
  public Optional<T> tryGet() {
    return Optional.of(value);
  }

  @Override
  public Observable<T> value() {
    return Observable.of(value);
  }

  @Override
  public Observable<Optional<T>> optionalValue() {
    return Observable.of(Optional.of(Optional.of(value)));
  }
}
