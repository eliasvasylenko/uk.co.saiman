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
 * This file is part of uk.co.saiman.utilities.
 *
 * uk.co.saiman.utilities is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.utilities is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.utility;

import static java.lang.Character.toUpperCase;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;

/**
 * Enum static utilities.
 * 
 * @author Elias N Vasylenko
 *
 * @param <S>
 *          Self bounding on the type of the enumeration class.
 */
public final class Enums {
  private Enums() {}

  /**
   * @param enumItem
   *          an enumeration item to make readable, by substituting underscores
   *          with spaces, and properly capitalizing
   * @return a readable version of the given enum item's name
   */
  public static String readableName(Enum<?> enumItem) {
    return Arrays
        .stream(enumItem.name().split("\\s|_"))
        .map(s -> toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase())
        .collect(joining(" "));
  }

  /**
   * @param <E>
   *          the type of the given enum item
   * @param item
   *          the enum item we wish to find the next in the sequence from
   * @return the next enumeration item in the sequence
   */
  @SuppressWarnings("unchecked")
  public static <E extends Enum<?>> E next(E item) {
    int ordinal = item.ordinal() + 1;
    Enum<?>[] items = item.getDeclaringClass().getEnumConstants();
    return items.length == ordinal ? (E) items[0] : (E) items[ordinal];
  }
}
