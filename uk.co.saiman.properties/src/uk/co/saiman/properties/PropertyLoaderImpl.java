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

import static java.util.Objects.requireNonNull;

import java.util.Locale;

import uk.co.saiman.collection.computingmap.CacheComputingMap;
import uk.co.saiman.collection.computingmap.ComputingMap;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.observable.ObservableValue;

class PropertyLoaderImpl implements PropertyLoader {
  private final ComputingMap<Class<?>, Object> localizationCache;

  private final LocaleProvider locale;
  private Log log;

  private final PropertyLoaderProperties text;

  /**
   * Create a new {@link PropertyLoader} instance for the given initial locale.
   * 
   * @param locale
   *          the initial locale
   * @param log
   *          the log for localization
   */
  public PropertyLoaderImpl(LocaleProvider locale, Log log) {
    localizationCache = new CacheComputingMap<>(c -> instantiateProperties(c), true);

    this.locale = locale;
    this.log = requireNonNull(log);

    PropertyLoaderProperties text;
    try {
      text = getProperties(PropertyLoaderProperties.class);
    } catch (Exception e) {
      text = new DefaultPropertyLoaderProperties();
    }
    this.text = text;

    if (log != null) {
      locale().observe(l -> {
        log.log(Level.INFO, getProperties().localeChanged(locale, getLocale()).toString());
      });
    }
  }

  public PropertyLoaderProperties getProperties() {
    return text;
  }

  Log getLog() {
    return log;
  }

  @Override
  public Locale getLocale() {
    return locale.getLocale();
  }

  @Override
  public ObservableValue<Locale> locale() {
    return locale;
  }

  protected <T> T instantiateProperties(Class<T> source) {
    return new PropertyAccessorDelegate<>(
        this,
        PropertyResource.getBundle(source),
        getLog(),
        source).getProxy();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getProperties(Class<T> accessorConfiguration) {
    return (T) localizationCache.putGet(accessorConfiguration);
  }
}
