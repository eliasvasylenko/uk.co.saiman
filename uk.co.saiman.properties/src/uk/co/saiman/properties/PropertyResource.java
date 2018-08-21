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

import static java.util.Collections.emptySet;

import java.util.Locale;
import java.util.Set;

/**
 * A resource bundle with a configurable locale
 * 
 * @author Elias N Vasylenko
 */
public interface PropertyResource {
  /**
   * @return the set of available keys according to the root locale
   */
  default Set<String> getKeys() {
    return getKeys(Locale.ROOT);
  }

  /**
   * @param locale
   *          the locale
   * @return the set of available keys according to the given locale
   */
  Set<String> getKeys(Locale locale);

  /**
   * @param key
   *          the key of the property
   * @return the value of the property of the given key according to the root
   *         locale
   */
  default String getValue(String key) {
    return getValue(key, Locale.ROOT);
  }

  /**
   * @param key
   *          the key of the property
   * @param locale
   *          the locale
   * @return the value of the property of the given key according to the given
   *         locale
   */
  String getValue(String key, Locale locale);

  static PropertyResource empty() {
    return new PropertyResource() {
      @Override
      public String getValue(String key, Locale locale) {
        return null;
      }

      @Override
      public Set<String> getKeys(Locale locale) {
        return emptySet();
      }
    };
  }
}
