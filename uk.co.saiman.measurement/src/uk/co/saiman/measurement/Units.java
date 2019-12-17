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
 * This file is part of uk.co.saiman.measurement.
 *
 * uk.co.saiman.measurement is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.measurement is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.measurement;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.AmountOfSubstance;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Length;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Time;

import si.uom.SI;
import tec.uom.se.AbstractUnit;

public final class Units {
  private static final Unit<Dimensionless> COUNT = AbstractUnit.ONE;

  private static <T extends Quantity<T>> MetricUnitBuilder<T> withMetricUnit(Unit<T> unit) {
    return new UnitBuilderImpl<>(unit, null);
  }

  static <T extends Quantity<T>> UnitBuilder<T> withUnit(Unit<T> unit) {
    return new UnitBuilderImpl<>(unit, null);
  }

  public static MetricUnitBuilder<Length> metre() {
    return withMetricUnit(SI.METRE);
  }

  public static MetricUnitBuilder<Dimensionless> count() {
    return withMetricUnit(COUNT);
  }

  public static MetricUnitBuilder<Time> second() {
    return withMetricUnit(SI.SECOND);
  }

  public static MetricUnitBuilder<Frequency> hertz() {
    return withMetricUnit(SI.HERTZ);
  }

  public static UnitBuilder<Dimensionless> percent() {
    return withMetricUnit(SI.PERCENT);
  }

  public static MetricUnitBuilder<Mass> dalton() {
    return withMetricUnit(SI.UNIFIED_ATOMIC_MASS);
  }

  public static MetricUnitBuilder<AmountOfSubstance> mole() {
    return withMetricUnit(SI.MOLE);
  }

  public static MetricUnitBuilder<Mass> gram() {
    return withMetricUnit(SI.GRAM);
  }

  public static MetricUnitBuilder<Pressure> pascal() {
    return withMetricUnit(SI.PASCAL);
  }

  public static UnitFormat unitFormat() {
    return new UnitFormatImpl();
  }
}
