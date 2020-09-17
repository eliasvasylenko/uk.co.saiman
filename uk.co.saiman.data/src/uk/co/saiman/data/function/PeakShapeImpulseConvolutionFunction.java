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
 * This file is part of uk.co.saiman.data.
 *
 * uk.co.saiman.data is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.data is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.data.function;

import static java.lang.Math.abs;
import static java.util.Arrays.binarySearch;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import uk.co.saiman.mathematics.Interval;

/**
 * A function described by a convolution operation over a given set of impulses,
 * or stick intensities, by a given peak shape.
 * 
 * @param <UD>
 *          the type of the units of measurement of values in the domain
 * @param <UR>
 *          the type of the units of measurement of values in the range
 * @author Elias N Vasylenko
 */
public class PeakShapeImpulseConvolutionFunction<UD extends Quantity<UD>, UR extends Quantity<UR>>
    implements ContinuousFunction<UD, UR> {
  private static final int TWEEN_STEPS = 5;

  private final Domain<UD> domain;
  private final Range<UR> range;
  private final Unit<UR> rangeUnit;

  private final double[] values;
  private final PeakShapeFunction[] peakFunctions;

  /**
   * Define a new function by way of convolution of the given samples by the given
   * peak shape description.
   * 
   * @param unitDomain
   *          the units of measurement of values in the domain
   * @param unitRange
   *          the units of measurement of values in the range
   * @param samples
   *          the number of contributing samples
   * @param values
   *          the sorted sample positions
   * @param intensities
   *          the intensities corresponding to the given values
   * @param peakFunctionFactory
   *          the peak function by which to convolve
   */
  public PeakShapeImpulseConvolutionFunction(
      Unit<UD> unitDomain,
      Unit<UR> unitRange,
      int samples,
      double[] values,
      double[] intensities,
      PeakShapeFunctionFactory peakFunctionFactory) {
    requireNonNull(intensities);
    requireNonNull(unitDomain);
    rangeUnit = requireNonNull(unitRange);

    /*
     * TODO sort values
     */
    this.values = Arrays.copyOf(requireNonNull(values), samples);

    peakFunctions = new PeakShapeFunction[samples];
    for (int i = 0; i < samples; i++) {
      peakFunctions[i] = peakFunctionFactory.atPeakPosition(values[i], intensities[i]);
    }

    Interval<Double> domainExtent = Interval
        .bounded(
            peakFunctions[0].effectiveDomainStart(),
            peakFunctions[samples - 1].effectiveDomainEnd());

    domain = new Domain<UD>() {
      @Override
      public Interval<Double> getInterval() {
        return domainExtent;
      }

      @Override
      public Unit<UD> getUnit() {
        return unitDomain;
      }
    };

    range = getRangeExtent();
  }

  @Override
  public Domain<UD> domain() {
    return domain;
  }

  @Override
  public Range<UR> range() {
    return range;
  }

  private int getSamples() {
    return values.length;
  }

  private Range<UR> getRangeExtent() {
    return getRangeExtentBetween(0, getSamples() - 1);
  }

  private Range<UR> getRangeExtentBetween(int startIndex, int endIndex) {
    return getRangeExtentBetween(values[startIndex], values[endIndex], startIndex, endIndex);
  }

  private Range<UR> getRangeExtentBetween(double startX, double endX) {
    int startIndex = abs(binarySearch(values, startX) + 1);
    int endIndex = abs(binarySearch(values, endX) + 1) - 1;

    if (startIndex < 0)
      startIndex = 0;
    if (endIndex >= values.length)
      endIndex = values.length - 1;

    return getRangeExtentBetween(startX, endX, startIndex, endIndex);
  }

  /*
   * Estimate range in codomain by sampling at the centre of each stick position,
   * and at various points between each stick position.
   */
  private Range<UR> getRangeExtentBetween(
      double startX,
      double endX,
      int startIndex,
      int endIndex) {
    double previousValue = values[startIndex];

    double startSample = sample(startX);
    double endSample = sample(endX);

    double maximum;
    double minimum;
    if (startSample > endSample) {
      maximum = startSample;
      minimum = endSample;
    } else {
      maximum = endSample;
      minimum = startSample;
    }

    // Sample first index
    if (previousValue >= startX && previousValue <= endX) {
      double intensity = sample(previousValue);
      if (intensity > maximum)
        maximum = intensity;
      else if (intensity < minimum)
        minimum = intensity;
    }

    for (int i = startIndex + 1; i <= endIndex; i++) {
      double value = values[i];

      if (value >= startX) {
        double subValueStep = (value - previousValue) / TWEEN_STEPS;
        double subValue = previousValue;

        for (int j = 0; j < TWEEN_STEPS; j++) {
          subValue += subValueStep;

          if (subValue >= startX && subValue <= (endX + subValueStep)) {
            double intensity = sample(subValue);
            if (intensity > maximum)
              maximum = intensity;
            else if (intensity < minimum)
              minimum = intensity;
          }
        }

        if (value >= endX)
          break;
      }

      previousValue = value;
    }

    Interval<Double> between = Interval.bounded(minimum, maximum);

    return new Range<UR>() {
      @Override
      public Interval<Double> getInterval() {
        return between;
      }

      @Override
      public Unit<UR> getUnit() {
        return rangeUnit;
      }

      @Override
      public Range<UR> between(double domainStart, double domainEnd) {
        return getRangeExtentBetween(domainStart, domainEnd);
      }
    };
  }

  @Override
  public double sample(double xPosition) {
    double sample = 0;
    for (int i = 0; i < getSamples(); i++) {
      if (peakFunctions[i].effectiveDomainStart() > xPosition)
        break;

      sample += peakFunctions[i].sample(xPosition);
    }
    return sample;
  }

  @Override
  public SampledContinuousFunction<UD, UR> resample(SampledDomain<UD> resolvableSampleDomain) {
    UnitConverter converter = resolvableSampleDomain.getUnit().getConverterTo(domain.getUnit());

    int maximumLength = resolvableSampleDomain.getDepth() * 3 + 1;
    double[] values = new double[maximumLength];
    double[] intensities = new double[maximumLength];

    int sampleCount = 0;
    double previousSamplePosition = resolvableSampleDomain.getInterval().getLeftEndpoint();
    double samplePosition = previousSamplePosition;

    for (int i = 0; i < resolvableSampleDomain.getDepth(); i++) {
      samplePosition = resolvableSampleDomain.getSample(i);

      values[sampleCount] = previousSamplePosition;
      intensities[sampleCount] = sample(converter.convert(previousSamplePosition));
      sampleCount++;

      // TODO maxima & minima in interval

      previousSamplePosition = samplePosition;
    }

    previousSamplePosition = resolvableSampleDomain.getInterval().getRightEndpoint();
    values[sampleCount] = previousSamplePosition;
    intensities[sampleCount] = sample(converter.convert(previousSamplePosition));

    return new ArraySampledContinuousFunction<>(
        new IrregularSampledDomain<>(domain.getUnit(), Arrays.copyOf(values, sampleCount)),
        range.getUnit(),
        intensities);
  }
}
