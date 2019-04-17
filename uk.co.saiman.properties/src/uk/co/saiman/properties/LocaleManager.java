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

import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservablePropertyImpl;

/**
 * Management interface over and associate {@link PropertyLoader localizer
 * instance}, allowing the locale of that instance to be changed.
 * <p>
 * A locale manager is observable over changes to its locale.
 * 
 * @author Elias N Vasylenko
 */
public interface LocaleManager extends LocaleProvider, ObservableProperty<Locale> {
  /**
   * @param locale
   *          the new locale
   */
  default void setLocale(Locale locale) {
    set(locale);
  }

  /**
   * @return a simple mutable locale manager, with its locale initialised to the
   *         system default
   */
  static LocaleManager getManager() {
    return getManager(Locale.getDefault());
  }

  /**
   * @param locale
   *          the initial locale
   * @return a simple mutable locale manager
   */
  static LocaleManager getManager(Locale locale) {
    return new LocaleManagerImpl(locale);
  }
}

class LocaleManagerImpl extends ObservablePropertyImpl<Locale> implements LocaleManager {
  public LocaleManagerImpl(Locale locale) {
    super(locale);
  }

  @Override
  public synchronized void setLocale(Locale locale) {
    set(locale);
  }

  @Override
  public synchronized Locale getLocale() {
    return get();
  }
}
