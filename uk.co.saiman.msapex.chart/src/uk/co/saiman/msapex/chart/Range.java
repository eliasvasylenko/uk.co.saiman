package uk.co.saiman.msapex.chart;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.util.StringConverter;

public class Range<T extends Quantity<T>> {
  private final double lowerBound;
  private final double upperBound;
  private final double pixelLength;
  private final TickUnit<T> tickUnit;

  private final List<Number> majorTicks;
  private final List<Number> minorTicks;

  public Range(double lowerBound, double upperBound, double pixelLength, TickUnit<T> tickUnit) {
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.pixelLength = pixelLength;
    this.tickUnit = tickUnit;

    majorTicks = unmodifiableList(calculateMajorTickValues());
    minorTicks = unmodifiableList(calculateMinorTickValues());
  }

  public Range(
      double lowerBound,
      double upperBound,
      double pixelLength,
      Unit<T> unit,
      double majorTickUnit,
      int minorTickCount,
      StringConverter<Number> tickLabelFormatter) {
    this(lowerBound, upperBound, pixelLength, new TickUnit<T>() {
      @Override
      public Unit<T> unit() {
        return unit;
      }

      @Override
      public double majorTick() {
        return majorTickUnit;
      }

      @Override
      public int minorTickCount() {
        return minorTickCount;
      }

      @Override
      public TickUnit<T> unitAbove() {
        return this;
      }

      @Override
      public StringConverter<Number> format() {
        return tickLabelFormatter;
      }
    });
  }

  public double lowerBound() {
    return lowerBound;
  }

  public double upperBound() {
    return upperBound;
  }

  public double interval() {
    return upperBound - lowerBound;
  }

  public double pixelLength() {
    return pixelLength;
  }

  public TickUnit<T> tickUnit() {
    return tickUnit;
  }

  public List<Number> majorTicks() {
    return majorTicks;
  }

  public List<Number> minorTicks() {
    return minorTicks;
  }

  private List<Number> calculateMajorTickValues() {
    List<Number> tickValues = new ArrayList<>();

    if (upperBound <= lowerBound) {
      tickValues.add(lowerBound);
    } else {
      tickValues.add(lowerBound);

      if (tickUnit.majorTick() > 0) {
        int majorFrom = getMajorTickAbove(lowerBound);
        int majorTo = getMajorTickBelow(upperBound);
        for (int i = majorFrom; i <= majorTo; i++) {
          tickValues.add(i * tickUnit.majorTick());
        }
      }

      tickValues.add(upperBound);
    }

    return tickValues;
  }

  private List<Number> calculateMinorTickValues() {
    final List<Number> minorTickMarks = new ArrayList<>();

    if (tickUnit.minorTickCount() <= 0 || tickUnit.majorTick() <= 0)
      return minorTickMarks;

    final double minorTickUnit = tickUnit.majorTick() / tickUnit.minorTickCount();
    if ((interval() / minorTickUnit) > 10000) {
      return minorTickMarks;
    }

    int majorFrom = getMajorTickAbove(lowerBound);
    int majorTo = getMajorTickBelow(upperBound);

    int minorfrom = getMinorTickAbove(lowerBound);
    for (int j = minorfrom; j < tickUnit.minorTickCount(); j++) {
      minorTickMarks.add((majorFrom - 1) * tickUnit.majorTick() + j * minorTickUnit);
    }

    for (int i = majorFrom; i <= majorTo; i++) {
      for (int j = 1; j < tickUnit.minorTickCount(); j++) {
        minorTickMarks.add(i * tickUnit.majorTick() + j * minorTickUnit);
      }
    }

    int minorTo = getMinorTickBelow(upperBound);
    for (int j = 1; j < minorTo; j++) {
      minorTickMarks.add(majorTo * tickUnit.majorTick() + j * minorTickUnit);
    }

    return minorTickMarks;
  }

  public int getMajorTickBelowOrEqual(double value) {
    return (int) floor(value / tickUnit.majorTick());
  }

  public int getMajorTickAboveOrEqual(double value) {
    return (int) ceil(value / tickUnit.majorTick());
  }

  public int getMajorTickBelow(double value) {
    return getMajorTickAboveOrEqual(value) - 1;
  }

  public int getMajorTickAbove(double value) {
    return getMajorTickBelowOrEqual(value) + 1;
  }

  public int getMinorTickBelowOrEqual(double value) {
    return (int) floor(
        (value / tickUnit.majorTick() - getMajorTickBelowOrEqual(value))
            * tickUnit.minorTickCount());
  }

  public int getMinorTickAboveOrEqual(double value) {
    return (int) ceil(
        (value / tickUnit.majorTick() - getMajorTickBelow(value)) * tickUnit.minorTickCount());
  }

  public int getMinorTickBelow(double value) {
    return getMinorTickAboveOrEqual(value) - 1;
  }

  public int getMinorTickAbove(double value) {
    return getMinorTickBelowOrEqual(value) + 1;
  }
}