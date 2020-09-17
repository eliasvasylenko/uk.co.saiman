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
import static java.lang.Math.max;

import java.util.List;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Dimension2D;
import javafx.geometry.Side;
import javafx.scene.chart.ValueAxis;

public class QuantityAxis<T extends Quantity<T>> extends ValueAxis<Number> {
  private final TickUnits<T> tickUnits;

  public QuantityAxis(TickUnits<T> tickUnits) {
    this.tickUnits = tickUnits;
    setAutoRanging(true);
  }

  public QuantityAxis(Unit<T> tickUnit) {
    this(new ScalingTickUnits<>(tickUnit));
    setUnit(tickUnit);
  }

  /**
   * The value between each major tick mark in data units. This is automatically
   * set if we are auto-ranging.
   */
  private ObjectProperty<Unit<T>> unit = new SimpleObjectProperty<>(null);

  public final Unit<T> getUnit() {
    return unit.get();
  }

  public final QuantityAxis<T> setUnit(Unit<T> value) {
    unit.set(value);
    return this;
  }

  public final ObjectProperty<Unit<T>> unitProperty() {
    return unit;
  }

  /**
   * The value between each major tick mark in data units. This is automatically
   * set if we are auto-ranging.
   */
  private DoubleProperty majorTickUnit = new SimpleDoubleProperty(1);

  public final double getMajorTickUnit() {
    return majorTickUnit.get();
  }

  public final QuantityAxis<T> setMajorTickUnit(double value) {
    majorTickUnit.set(value);
    return this;
  }

  public final DoubleProperty majorTickUnitProperty() {
    return majorTickUnit;
  }

  /**
   * The value between each major tick mark in data units. This is automatically
   * set if we are auto-ranging.
   */
  private BooleanProperty paddingApplied = new SimpleBooleanProperty(false);

  public final boolean isPaddingApplied() {
    return paddingApplied.get();
  }

  public final QuantityAxis<T> setPaddingApplied(boolean value) {
    paddingApplied.set(value);
    return this;
  }

  public final BooleanProperty paddingAppliedProperty() {
    return paddingApplied;
  }

  /**
   * The value between each major tick mark in data units. This is automatically
   * set if we are auto-ranging.
   */
  private BooleanProperty snapRangeToMajorTick = new SimpleBooleanProperty(false);

  public final boolean isSnapRangeToMajorTick() {
    return snapRangeToMajorTick.get();
  }

  public final QuantityAxis<T> setSnapRangeToMajorTick(boolean value) {
    snapRangeToMajorTick.set(value);
    return this;
  }

  public final BooleanProperty snapRangeToMajorTick() {
    return snapRangeToMajorTick;
  }

  /**
   * The value between each major tick mark in data units. This is automatically
   * set if we are auto-ranging.
   */
  private BooleanProperty forceZeroInRange = new SimpleBooleanProperty(false);

  public final boolean isForceZeroInRange() {
    return forceZeroInRange.get();
  }

  public final QuantityAxis<T> setForceZeroInRange(boolean value) {
    forceZeroInRange.set(value);
    return this;
  }

  public final BooleanProperty forceZeroInRange() {
    return forceZeroInRange;
  }

  /**
   * Get the string label name for a tick mark with the given value
   *
   * @param value
   *          The value to format into a tick label string
   * @return A formatted string for the given value
   */
  @Override
  protected String getTickMarkLabel(Number value) {
    return getTickLabelFormatter().toString(value);
  }

  /**
   * Called to get the current axis range.
   *
   * @return A range object that can be passed to setRange() and
   *         calculateTickValues()
   */
  @Override
  protected Range<T> getRange() {
    return new Range<>(
        getLowerBound(),
        getUpperBound(),
        getDisplayPosition(getUpperBound()) - getDisplayPosition(getLowerBound()),
        getUnit(),
        getMajorTickUnit(),
        getMinorTickCount(),
        getTickLabelFormatter());
  }

  /**
   * Called to set the current axis range to the given range. If isAnimating()
   * is true then this method should animate the range to the new range.
   *
   * @param rangeObject
   *          A range object returned from autoRange()
   * @param animate
   *          If true animate the change in range
   */
  @Override
  protected void setRange(Object rangeObject, boolean animate) {
    @SuppressWarnings("unchecked")
    Range<T> range = (Range<T>) rangeObject;

    setLowerBound(range.lowerBound());
    setUpperBound(range.upperBound());

    currentLowerBound.set(range.lowerBound());
    setScale(calculateNewScale(range.pixelLength(), range.lowerBound(), range.upperBound()));
    setUnit(range.tickUnit().unit());
    setMajorTickUnit(range.tickUnit().majorTick());
    setMinorTickCount(range.tickUnit().minorTickCount());
    setTickLabelFormatter(range.tickUnit().format());
  }

  /**
   * Measure the size of the label for given tick mark value. This uses the font
   * that is set for the tick marks
   *
   * @param value
   *          tick mark value
   * @param rangeObject
   *          range to use during calculations
   * @return size of tick mark label for given value
   */
  @Override
  protected Dimension2D measureTickMarkSize(Number value, Object rangeObject) {
    @SuppressWarnings("unchecked")
    Range<T> range = (Range<T>) rangeObject;
    return measureTickMarkLabelSize(range.tickUnit().format().toString(value));
  }

  protected Dimension2D measureTickMarkLabelSize(String label) {
    return measureTickMarkLabelSize(label, getTickLabelRotation());
  }

  public double measureTickMarkLabelSide(String label) {
    Dimension2D size = measureTickMarkLabelSize(label);
    return getSide() == Side.LEFT || getSide() == Side.RIGHT ? size.getHeight() : size.getWidth();
  }

  /**
   * Calculate a list of all the data values for each tick mark in range
   *
   * @param length
   *          The length of the axis in display units
   * @param rangeObject
   *          A range object returned from autoRange()
   * @return A list of tick marks that fit along the axis if it was the given
   *         length
   */
  @Override
  protected List<Number> calculateTickValues(double length, Object rangeObject) {
    @SuppressWarnings("unchecked")
    Range<T> range = (Range<T>) rangeObject;
    // the length is redundant here and shouldn't have been part of the API.
    return calculateTickValues(range);
  }

  protected List<Number> calculateTickValues(Range<T> range) {
    return range.majorTicks();
  }

  /**
   * Calculate a list of the data values for every minor tick mark
   *
   * @return List of data values where to draw minor tick marks
   */
  @Override
  protected List<Number> calculateMinorTickMarks() {
    return getRange().minorTicks();
  }

  /**
   * Called to set the upper and lower bound and anything else that needs to be
   * auto-ranged
   *
   * @param minValue
   *          The min data value that needs to be plotted on this axis
   * @param maxValue
   *          The max data value that needs to be plotted on this axis
   * @param pixelLength
   *          The length of the axis in display coordinates
   * @param labelSize
   *          The approximate average size a label takes along the axis
   * @return The calculated range
   */
  @Override
  protected Range<T> autoRange(
      double minValue,
      double maxValue,
      double pixelLength,
      double labelSize) {
    return calculateRange(minValue, maxValue, pixelLength);
  }

  protected Range<T> calculateRange(double minValue, double maxValue, double pixelLength) {
    // predict an upper bound on the number of ticks for an initial scaling
    int numOfTickMarks = (int) floor(pixelLength / measureTickMarkLabelSide("0"));
    numOfTickMarks = max(numOfTickMarks, 2);
    TickUnit<T> metricTickUnit = tickUnits.getUnitBelow((maxValue - minValue) / numOfTickMarks);

    double reqLength;
    Range<T> range;
    do {
      range = calculateRange(minValue, maxValue, pixelLength, metricTickUnit);

      reqLength = calculateRequiredLength(range);

      if (reqLength > pixelLength)
        metricTickUnit = metricTickUnit.unitAbove();
    } while (reqLength > pixelLength);

    return range;
  }

  protected Range<T> calculateRange(
      double minValue,
      double maxValue,
      double pixelLength,
      TickUnit<T> tickUnit) {
    // check if we need to force zero into range
    if (isForceZeroInRange()) {
      if (maxValue < 0) {
        maxValue = 0;
      } else if (minValue > 0) {
        minValue = 0;
      }
    }

    double interval = maxValue - minValue;

    if (isPaddingApplied()) {
      final double padding = ((interval != 0) ? interval : max(1, minValue)) * 0.1;

      // if min and max are not zero then add padding to them
      // check padding has not pushed min or max over zero line
      if (minValue * (minValue - padding) > 0)
        minValue -= padding;
      else
        minValue = 0;
      if (maxValue * (maxValue + padding) > 0)
        maxValue += padding;
      else
        maxValue = 0;
      interval = maxValue - minValue;
    }

    if (isSnapRangeToMajorTick() && tickUnit.majorTick() != 0) {
      minValue = Math.floor(minValue / tickUnit.majorTick()) * tickUnit.majorTick();
      maxValue = Math.ceil(maxValue / tickUnit.majorTick()) * tickUnit.majorTick();
    }

    // return new range
    return new Range<>(minValue, maxValue, pixelLength, tickUnit);
  }

  protected double calculateRequiredLength(Range<T> range) {
    double maxReqTickGap = 0;
    double previousTickSize = 0;
    int tickCount = (int) Math
        .ceil((range.upperBound() - range.lowerBound()) / range.tickUnit().majorTick()) - 1;
    for (int i = 0; i < tickCount; i++) {
      double tickPosition = range.lowerBound() + i * range.tickUnit().majorTick();
      double tickSize = measureTickMarkLabelSide(range.tickUnit().format().toString(tickPosition));
      if (i > 0) {
        maxReqTickGap = Math.max(maxReqTickGap, previousTickSize + 6 + (tickSize / 2));
      }
      previousTickSize = tickSize / 2;
    }
    return (tickCount + 1) * maxReqTickGap;
  }
}
