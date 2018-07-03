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

import java.lang.reflect.AnnotatedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uk.co.saiman.observable.ObservablePropertyImpl;
import uk.co.saiman.observable.ObservableValue;
import uk.co.saiman.observable.Observer;

/*
 * Implementation of localized property
 */
class LocalizedImpl<A> extends ObservablePropertyImpl<String>
    implements LocalizedString, Observer<Locale> {
  private final PropertyAccessorDelegate<A> propertyAccessorDelegate;

  private final String key;
  private final List<Object> arguments;
  private final Map<Locale, String> cache;

  public LocalizedImpl(
      PropertyAccessorDelegate<A> propertyAccessorDelegate,
      String key,
      List<?> arguments) {
    super(new IllegalStateException("Locale has failed to initialize"));

    this.propertyAccessorDelegate = propertyAccessorDelegate;

    this.key = key;
    this.arguments = new ArrayList<>(arguments);
    this.cache = new ConcurrentHashMap<>();

    locale().weakReference().observe(this);
    updateText(locale().get());
  }

  private synchronized void updateText(Locale locale) {
    set(get(locale));
  }

  @Override
  public String toString() {
    return get().toString();
  }

  @Override
  public synchronized String get() {
    return super.get();
  }

  @Override
  public String get(Locale locale) {
    AnnotatedType annotatedStringType;
    try {
      annotatedStringType = getClass().getDeclaredField("key").getAnnotatedType();
    } catch (NoSuchFieldException | SecurityException e) {
      throw new AssertionError();
    }

    return cache.computeIfAbsent(locale, l -> {
      return (String) this.propertyAccessorDelegate
          .parseValueString(annotatedStringType, key, locale)
          .apply(arguments);
    });
  }

  @Override
  public void onNext(Locale locale) {
    updateText(locale);
  }

  @Override
  public ObservableValue<Locale> locale() {
    return this.propertyAccessorDelegate.getLoader().locale();
  }
}
