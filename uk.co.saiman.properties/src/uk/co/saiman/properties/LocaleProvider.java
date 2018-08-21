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

import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.observable.ObservableValue;
import uk.co.saiman.observable.Observer;

/**
 * Management interface over and associate {@link PropertyLoader localiser
 * instance}, allowing the locale of that instance to be changed.
 * <p>
 * A locale manager is observable over changes to its locale.
 * 
 * @author Elias N Vasylenko
 */
public interface LocaleProvider extends ObservableValue<Locale> {
  /**
   * As returned by {@link #getDefaultProvider()}.
   */
  LocaleProvider DEFAULT_PROVIDER = new DefaultLocaleProvider();

  /**
   * @return the current locale
   */
  default Locale getLocale() {
    return get();
  }

  /**
   * Create a locale provider based on the system default locale, as returned by
   * {@link Locale#getDefault()}.
   * 
   * @return a locale manager initialized according to the system locale
   */
  static LocaleProvider getDefaultProvider() {
    return DEFAULT_PROVIDER;
  }

  static LocaleProvider getStaticProvider(Locale locale) {
    return new LocaleProvider() {
      @Override
      public Disposable observe(Observer<? super Locale> observer) {
        return Observable.of(locale).observe(observer);
      }

      @Override
      public Locale get() {
        return locale;
      }

      @Override
      public Observable<Change<Locale>> changes() {
        return Observable.<Change<Locale>>empty();
      }
    };
  }
}

class DefaultLocaleProvider implements LocaleProvider {
  private final Locale locale;

  public DefaultLocaleProvider() {
    this.locale = Locale.getDefault();
  }

  @Override
  public Locale get() {
    return locale;
  }

  @Override
  public Observable<Change<Locale>> changes() {
    return Observable.empty();
  }

  @Override
  public Disposable observe(Observer<? super Locale> observer) {
    return Observable.empty().observe();
  }
}