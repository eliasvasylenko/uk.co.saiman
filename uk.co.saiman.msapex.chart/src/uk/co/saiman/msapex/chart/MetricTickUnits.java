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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.util.StringConverter;
import uk.co.saiman.measurement.MetricUnitBuilder;
import uk.co.saiman.measurement.fx.QuantityFormatter;

public class MetricTickUnits<T extends Quantity<T>> implements TickUnits<T> {
  class MetricTickUnit implements TickUnit<T> {
    private final MetricTickUnitSubscale subscale;
    private final int exponent;

    private MetricTickUnit(MetricTickUnitSubscale subscale, int exponent) {
      this.subscale = subscale;
      this.exponent = exponent;
    }

    @Override
    public Unit<T> unit() {
      return baseUnit;
    }

    @Override
    public StringConverter<Number> format() {
      return formatter.quantityFormatter(unit());
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
    public MetricTickUnit unitAbove() {
      int ordinal = subscale.ordinal() + 1;
      int exponent = this.exponent;
      if (ordinal == MetricTickUnitSubscale.values().length) {
        ordinal = 0;
        exponent++;
      }
      return new MetricTickUnit(MetricTickUnitSubscale.values()[ordinal], exponent);
    }
  }

  static enum MetricTickUnitSubscale {
    ONE(1, 10), TWO(2, 10), FIVE(5, 5);

    private final int majorTickLength;
    private final int minorTickCount;

    private MetricTickUnitSubscale(int majorTickLength, int minorTickCount) {
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

  private final QuantityFormatter formatter;
  private final Unit<T> baseUnit;
  private final List<Unit<T>> prefixesBelow;
  private final List<Unit<T>> prefixesAbove;

  public MetricTickUnits(MetricUnitBuilder<T> unitBuilder) {
    formatter = defaultQuantityFormatter();
    unitBuilder = unitBuilder.none();
    baseUnit = unitBuilder.getUnit();
    prefixesBelow = createPrefixesBelow(unitBuilder);
    prefixesAbove = createPrefixesAbove(unitBuilder);
  }

  static <T extends Quantity<T>> List<Unit<T>> createPrefixesBelow(
      MetricUnitBuilder<T> unitBuilder) {
    ArrayList<Unit<T>> prefixedUnits = new ArrayList<>();
    prefixedUnits.add(unitBuilder.milli().getUnit());
    prefixedUnits.add(unitBuilder.micro().getUnit());
    prefixedUnits.add(unitBuilder.nano().getUnit());
    prefixedUnits.add(unitBuilder.pico().getUnit());
    prefixedUnits.add(unitBuilder.femto().getUnit());
    prefixedUnits.add(unitBuilder.atto().getUnit());
    prefixedUnits.add(unitBuilder.zepto().getUnit());
    prefixedUnits.add(unitBuilder.yocto().getUnit());
    prefixedUnits.trimToSize();
    return Collections.unmodifiableList(prefixedUnits);
  }

  static <T extends Quantity<T>> List<Unit<T>> createPrefixesAbove(
      MetricUnitBuilder<T> unitBuilder) {
    ArrayList<Unit<T>> prefixedUnits = new ArrayList<>();
    prefixedUnits.add(unitBuilder.kilo().getUnit());
    prefixedUnits.add(unitBuilder.mega().getUnit());
    prefixedUnits.add(unitBuilder.giga().getUnit());
    prefixedUnits.add(unitBuilder.tera().getUnit());
    prefixedUnits.add(unitBuilder.peta().getUnit());
    prefixedUnits.add(unitBuilder.exa().getUnit());
    prefixedUnits.add(unitBuilder.zetta().getUnit());
    prefixedUnits.add(unitBuilder.yotta().getUnit());
    prefixedUnits.trimToSize();
    return Collections.unmodifiableList(prefixedUnits);
  }

  @Override
  public TickUnit<T> getUnitBelow(double valueAbove) {
    int exponent = (int) floor(log10(valueAbove));
    double mantissa = valueAbove / pow(10, exponent);

    for (int i = MetricTickUnitSubscale.values().length - 1; i >= 0; i--) {
      MetricTickUnitSubscale subscale = MetricTickUnitSubscale.values()[i];
      if (mantissa > subscale.getMajorTickLength()) {
        return new MetricTickUnit(subscale, exponent);
      }
    }
    return new MetricTickUnit(MetricTickUnitSubscale.ONE, exponent);
  }
}