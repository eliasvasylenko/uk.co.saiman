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
package uk.co.saiman.properties.impl;

import static uk.co.saiman.properties.LocaleProvider.getStaticProvider;

import java.lang.reflect.AnnotatedType;
import java.util.List;
import java.util.Locale;

import uk.co.saiman.observable.ObservablePropertyImpl;
import uk.co.saiman.observable.ObservableValue;
import uk.co.saiman.observable.Observer;
import uk.co.saiman.properties.LocaleProvider;
import uk.co.saiman.properties.Localized;
import uk.co.saiman.properties.PropertyResource;
import uk.co.saiman.properties.PropertyValueConversion;
import uk.co.saiman.properties.PropertyValueConverter;

/*
 * Implementation of localized property
 */
public class LocalizedConversion implements PropertyValueConversion<Localized<?>> {
  private final PropertyValueConverter valueConverter;
  private final PropertyValueConversion<?> conversion;

  private final LocaleProvider localeProvider;
  private final PropertyResource propertyResource;
  private final AnnotatedType type;
  private final String key;

  public LocalizedConversion(
      PropertyValueConverter valueConverter,
      LocaleProvider localeProvider,
      PropertyResource propertyResource,
      AnnotatedType type,
      String key) {
    this.valueConverter = valueConverter;
    this.conversion = valueConverter.getConversion(localeProvider, propertyResource, type, key);

    this.localeProvider = localeProvider;
    this.propertyResource = propertyResource;
    this.type = type;
    this.key = key;
  }

  @Override
  public Localized<?> applyConversion(List<?> arguments) {
    return new LocalizedImpl<>(arguments);
  }

  class LocalizedImpl<T> extends ObservablePropertyImpl<T>
      implements Localized<T>, Observer<Locale> {
    private final List<?> arguments;

    public LocalizedImpl(List<?> arguments) {
      super(() -> new IllegalStateException("Locale has failed to initialize"));

      this.arguments = arguments;

      locale().value().weakReference().observe(this);
      updateText(locale().get());
    }

    private synchronized void updateText(Locale locale) {
      set(get(locale));
    }

    @Override
    public String toString() {
      return get().toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized T get() {
      return (T) conversion.applyConversion(arguments);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(Locale locale) {
      return (T) valueConverter
          .getConversion(getStaticProvider(locale), propertyResource, type, key)
          .applyConversion(arguments);
    }

    @Override
    public void onNext(Locale locale) {
      updateText(locale);
    }

    @Override
    public ObservableValue<Locale> locale() {
      return localeProvider;
    }
  }
}
