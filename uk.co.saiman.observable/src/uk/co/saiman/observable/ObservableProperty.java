/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import java.util.Optional;

import uk.co.saiman.property.Property;

/**
 * A {@link Property property} which is observable as per
 * {@link ObservableValue}, and whose value can also be set.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the type of the value
 */
public interface ObservableProperty<T> extends ObservableValue<T>, Property<T> {
  /**
   * Instantiate an observable property with identity assignment and identity
   * equality.
   * 
   * @param <T>          the type of event message to produce and which we may
   *                     assign from
   * @param initialValue the initial value
   * @return an observable property with the given default value
   */
  static <T> ObservableProperty<T> over(T initialValue) {
    return new ObservablePropertyImpl<>(initialValue);
  }

  /**
   * Instantiate an observable property with identity assignment and identity
   * equality.
   * 
   * @param <T>            the type of event message to produce and which we may
   *                       assign from
   * @param initialProblem the initial problem
   * @return an observable property with the given default value
   */
  static <T> ObservableProperty<T> over(Throwable initialProblem) {
    return new ObservablePropertyImpl<>(initialProblem);
  }

  void setProblem(Throwable t);

  @Override
  default T unset() {
    T t = tryGet().orElse(null);
    setProblem(new NullPointerException());
    return t;
  }

  @Override
  Optional<T> tryGet();
}
