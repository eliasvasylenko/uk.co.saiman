package uk.co.saiman.msapex.chart;

import static java.lang.Math.floor;
import static java.lang.Math.log10;
import static java.lang.Math.pow;
import static uk.co.saiman.measurement.fx.QuantityFormatter.quantityFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.util.StringConverter;
import uk.co.saiman.measurement.MetricUnitBuilder;
import uk.co.saiman.measurement.Units;
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

  public MetricTickUnits(Units units, Function<Units, MetricUnitBuilder<T>> unitBuilder) {
    formatter = quantityFormatter(units);
    MetricUnitBuilder<T> builder = unitBuilder.apply(units).none();
    baseUnit = builder.get();
    prefixesBelow = createPrefixesBelow(builder);
    prefixesAbove = createPrefixesAbove(builder);
  }

  static <T extends Quantity<T>> List<Unit<T>> createPrefixesBelow(
      MetricUnitBuilder<T> unitBuilder) {
    ArrayList<Unit<T>> prefixedUnits = new ArrayList<>();
    prefixedUnits.add(unitBuilder.milli().get());
    prefixedUnits.add(unitBuilder.micro().get());
    prefixedUnits.add(unitBuilder.nano().get());
    prefixedUnits.add(unitBuilder.pico().get());
    prefixedUnits.add(unitBuilder.femto().get());
    prefixedUnits.add(unitBuilder.atto().get());
    prefixedUnits.add(unitBuilder.zepto().get());
    prefixedUnits.add(unitBuilder.yocto().get());
    prefixedUnits.trimToSize();
    return Collections.unmodifiableList(prefixedUnits);
  }

  static <T extends Quantity<T>> List<Unit<T>> createPrefixesAbove(
      MetricUnitBuilder<T> unitBuilder) {
    ArrayList<Unit<T>> prefixedUnits = new ArrayList<>();
    prefixedUnits.add(unitBuilder.kilo().get());
    prefixedUnits.add(unitBuilder.mega().get());
    prefixedUnits.add(unitBuilder.giga().get());
    prefixedUnits.add(unitBuilder.tera().get());
    prefixedUnits.add(unitBuilder.peta().get());
    prefixedUnits.add(unitBuilder.exa().get());
    prefixedUnits.add(unitBuilder.zetta().get());
    prefixedUnits.add(unitBuilder.yotta().get());
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