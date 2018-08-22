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
 * This file is part of uk.co.saiman.mathematics.
 *
 * uk.co.saiman.mathematics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.mathematics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.mathematics;

public class FloatingPointUtilities {
  private FloatingPointUtilities() {}

  public static double unitInTheLastPlaceAbove(double value) {
    if (Double.isNaN(value) || Double.isInfinite(value)) {
      return value;
    }
    double absoluteValue = Math.abs(value);

    long nextValueLong = Double.doubleToLongBits(absoluteValue) + 1;
    double nextValue = Double.longBitsToDouble(nextValueLong);

    // if ended on bad number go down instead
    if (Double.isNaN(nextValue) || Double.isInfinite(nextValue)) {
      nextValueLong = nextValueLong - 2;
      nextValue = absoluteValue;
      absoluteValue = Double.longBitsToDouble(nextValueLong);
    }

    return Math.abs(nextValue - absoluteValue);
  }

  public final double unitInTheLastPlaceBelow(double value) {
    if (Double.isNaN(value) || Double.isInfinite(value)) {
      return value;
    }
    double absoluteValue = Math.abs(value);

    long nextValueLong = Double.doubleToLongBits(absoluteValue) - 1;
    double nextValue = Double.longBitsToDouble(nextValueLong);

    // if ended on bad number go up instead
    if (Double.isNaN(nextValue) || Double.isInfinite(nextValue)) {
      nextValueLong = nextValueLong + 2;
      nextValue = absoluteValue;
      absoluteValue = Double.longBitsToDouble(nextValueLong);
    }

    return Math.abs(nextValue - absoluteValue);
  }
}