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
 * This file is part of uk.co.saiman.properties.
 *
 * uk.co.saiman.properties is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.properties is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.properties;

import java.util.Locale;
import java.util.Optional;

import uk.co.saiman.observable.Observable;
import uk.co.saiman.observable.ObservableValue;

/**
 * A localized property interface which is observable over the value changes due
 * to updated locale.
 * 
 * @author Elias N Vasylenko
 */
public interface Localized<T> extends ObservableValue<T> {
  /**
   * @return the current locale of the string
   */
  ObservableValue<Locale> locale();

  @Override
  T get();

  /**
   * @param locale the locale to translate to
   * @return the localized string value according to the given locale
   */
  T get(Locale locale);

  /**
   * Create a localized view of a value with a static locale.
   * 
   * @param value  the localized value
   * @param locale the locale of the given text
   * @return a localized string over the given text and locale
   */
  static <T> Localized<T> forStaticLocale(T value, Locale locale) {
    return new Localized<T>() {
      @Override
      public T get() {
        return value;
      }

      @Override
      public String toString() {
        return value.toString();
      }

      @Override
      public T get(Locale locale) {
        return get();
      }

      @Override
      public ObservableValue<Locale> locale() {
        return ObservableValue.of(locale);
      }

      @Override
      public Observable<Change<T>> changes() {
        return Observable.empty();
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
    };
  }
}
