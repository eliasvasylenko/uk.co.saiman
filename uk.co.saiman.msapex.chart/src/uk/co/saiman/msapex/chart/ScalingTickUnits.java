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
 * This file is part of uk.co.saiman.msapex.chart.
 *
 * uk.co.saiman.msapex.chart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.chart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.chart;

import static java.lang.Math.floor;
import static java.lang.Math.log10;
import static java.lang.Math.pow;
import static uk.co.saiman.measurement.fx.QuantityFormatter.defaultQuantityFormatter;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.util.StringConverter;
import uk.co.saiman.measurement.fx.QuantityFormatter;

public class ScalingTickUnits<T extends Quantity<T>> implements TickUnits<T> {
  class ScalingTickUnit implements TickUnit<T> {
    private final TickUnitSubscale subscale;
    private final int exponent;

    private ScalingTickUnit(TickUnitSubscale subscale, int exponent) {
      this.subscale = subscale;
      this.exponent = exponent;
    }

    @Override
    public Unit<T> unit() {
      return unit;
    }

    @Override
    public double majorTick() {
      return subscale.getMajorTickLength() * pow(10, exponent);
    }

    @Override
    public int minorTickCount() {
      return subscale.getMinorTickCount();
    }

    @Override
    public StringConverter<Number> format() {
      return formatter;
    }

    @Override
    public ScalingTickUnit unitAbove() {
      int ordinal = subscale.ordinal() + 1;
      int exponent = this.exponent;
      if (ordinal == TickUnitSubscale.values().length) {
        ordinal = 0;
        exponent++;
      }
      return new ScalingTickUnit(TickUnitSubscale.values()[ordinal], exponent);
    }
  }

  static enum TickUnitSubscale {
    ONE(1, 10), TWO(2, 10), FIVE(5, 5);

    private final int majorTickLength;
    private final int minorTickCount;

    private TickUnitSubscale(int majorTickLength, int minorTickCount) {
      this.majorTickLength = majorTickLength;
      this.minorTickCount = minorTickCount;
    }

    public int getMajorTickLength() {
      return majorTickLength;
    }

    public int getMinorTickCount() {
      return minorTickCount;
    }
  }

  private final StringConverter<Number> formatter;
  private final Unit<T> unit;

  public ScalingTickUnits(Unit<T> unit) {
    this(defaultQuantityFormatter(), unit);
  }

  public ScalingTickUnits(QuantityFormatter formatter, Unit<T> unit) {
    this(formatter.quantityFormatter(unit), unit);
  }

  public ScalingTickUnits(StringConverter<Number> formatter, Unit<T> unit) {
    this.formatter = formatter;
    this.unit = unit;
  }

  @Override
  public TickUnit<T> getUnitBelow(double valueAbove) {
    int exponent = (int) floor(log10(valueAbove));
    double mantissa = valueAbove / pow(10, exponent);

    for (int i = TickUnitSubscale.values().length - 1; i >= 0; i--) {
      TickUnitSubscale subscale = TickUnitSubscale.values()[i];
      if (mantissa > subscale.getMajorTickLength()) {
        return new ScalingTickUnit(subscale, exponent);
      }
    }
    return new ScalingTickUnit(TickUnitSubscale.ONE, exponent);
  }
}