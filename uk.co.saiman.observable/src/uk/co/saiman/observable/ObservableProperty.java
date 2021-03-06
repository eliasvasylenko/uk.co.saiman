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

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

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
  static <T> ObservableProperty<T> over(Supplier<Throwable> initialProblem) {
    return new ObservablePropertyImpl<>(initialProblem);
  }

  void setProblem(Supplier<Throwable> t);

  @Override
  default T unset() {
    T t = tryGet().orElse(null);
    setProblem(NullPointerException::new);
    return t;
  }

  @Override
  Optional<T> tryGet();

  @Override
  default <U> ObservableProperty<U> map(
      Function<? super T, ? extends U> mappingOut,
      Function<? super U, ? extends T> mappingIn) {
    ObservableProperty<T> owner = this;
    return new ObservableProperty<U>() {
      @Override
      public U get() {
        return mappingOut.apply(owner.get());
      }

      @Override
      public Optional<U> tryGet() {
        return owner.tryGet().map(mappingOut::apply);
      }

      @Override
      public Observable<Change<U>> changes() {
        return owner.changes().map(change -> new Change<U>() {
          @Override
          public ObservableValue<U> previousValue() {
            return change.previousValue().map(mappingOut);
          }

          @Override
          public ObservableValue<U> newValue() {
            return change.newValue().map(mappingOut);
          }
        });
      }

      @Override
      public Observable<U> value() {
        return owner.value().map(mappingOut);
      }

      @Override
      public Observable<Optional<U>> optionalValue() {
        return owner.optionalValue().map(o -> o.map(mappingOut));
      }

      @Override
      public U set(U to) {
        return mappingOut.apply(owner.set(mappingIn.apply(to)));
      }

      @Override
      public void setProblem(Supplier<Throwable> t) {
        owner.setProblem(t);
      }
    };
  }
}
