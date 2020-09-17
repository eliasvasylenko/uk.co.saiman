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

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.function.Function;

import javax.measure.Quantity;
import javax.measure.Unit;

import uk.co.saiman.mathematics.Interval;

/**
 * A (currently) immutable implementation of {@link RegularSampledDomain} which
 * optimizes memory usage for sampled continua with mostly 0 sample values in
 * the codomain.
 * 
 * @param <UD>
 *          the type of the units of measurement of values in the domain
 * @param <UR>
 *          the type of the units of measurement of values in the range
 * @author Elias N Vasylenko
 */
public class SparseSampledContinuousFunction<UD extends Quantity<UD>, UR extends Quantity<UR>>
    implements SampledContinuousFunction<UD, UR> {
  private final SampledDomain<UD> domain;
  private final SampledRange<UR> range;

  private final int[] intensityIndices;
  private final double[] intensities;

  /**
   * Instantiate based on the given significant sample indices and intensities.
   * Samples at indices other than those given are assumed to be of intensity 0 in
   * the codomain.
   * 
   * @param domain
   *          the domain of the function
   * @param unitRange
   *          the units of measurement of values in the range
   * @param samples
   *          The number of non-zero samples
   * @param indices
   *          The sequential indices of non-zero samples
   * @param intensities
   *          The intensities at the given non-zero sample indices
   */
  public SparseSampledContinuousFunction(
      SampledDomain<UD> domain,
      Unit<UR> unitRange,
      int samples,
      int[] indices,
      double[] intensities) {
    /*
     * TODO sort the indices & intensities here
     */
    this.intensityIndices = Arrays.copyOf(requireNonNull(indices), samples);
    this.intensities = Arrays.copyOf(requireNonNull(intensities), samples);
    this.domain = requireNonNull(domain);
    this.range = createDefaultRange(i -> intensities[i], unitRange);
  }

  /**
   * Create a memory efficient view of the given array, with the given frequency.
   * 
   * @param domain
   *          the domain of the function
   * @param unitRange
   *          the units of measurement of values in the range
   * @param intensities
   *          The intensities as a sequence of samples at the given frequency
   */
  public SparseSampledContinuousFunction(
      SampledDomain<UD> domain,
      Unit<UR> unitRange,
      double[] intensities) {
    int depth = 0;
    for (double intensity : intensities) {
      if (intensity != 0) {
        depth++;
      }
    }

    this.intensityIndices = new int[depth];
    this.intensities = new double[depth];

    int index = 0;
    for (int i = 0; i < intensities.length; i++) {
      if (intensities[i] != 0) {
        intensityIndices[index] = i;
        this.intensities[index] = intensities[i];

        index++;
      }
    }

    this.domain = domain;
    this.range = createDefaultRange(i -> intensities[i], unitRange);
  }

  protected SampledRange<UR> createDefaultRange(
      Function<Integer, Double> intensityAtIndex,
      Unit<UR> unitRange) {
    requireNonNull(intensityAtIndex);
    requireNonNull(unitRange);

    return new SampledRange<UR>(this) {
      @Override
      public Unit<UR> getUnit() {
        return unitRange;
      }

      @Override
      public int getDepth() {
        return domain().getDepth();
      }

      @Override
      public double getSample(int index) {
        int indexIndex = getIndexIndex(index);
        if (indexIndex < 0) {
          return 0;
        } else {
          return intensityAtIndex.apply(index);
        }
      }

      @Override
      public Interval<Double> getInterval() {
        return super.getInterval();
      }

      @Override
      public boolean equals(Object obj) {
        return super.equals(obj);
      }

      @Override
      public int hashCode() {
        return super.hashCode();
      }

    };
  }

  private int getIndexIndex(int index) {
    int from = 0;
    int to = intensityIndices.length - 1;

    if (to < 0) {
      return -1;
    }

    do {
      if (intensityIndices[to] < index) {
        return -1;
      } else if (intensityIndices[to] == index) {
        return to;
      } else if (intensityIndices[from] > index) {
        return -1;
      } else if (intensityIndices[from] == index) {
        return from;
      } else {
        int mid = (to + from) / 2;
        if (intensityIndices[mid] > index) {
          to = mid;
        } else {
          from = mid;
        }
      }
    } while (to - from > 1);

    return -1;
  }

  @Override
  public SampledRange<UR> range() {
    return range;
  }

  @Override
  public SampledDomain<UD> domain() {
    return domain;
  }

  @Override
  public int getDepth() {
    return domain().getDepth();
  }

  @Override
  public SampledContinuousFunction<UD, UR> resample(SampledDomain<UD> resolvableSampleDomain) {
    int sourceSamples = intensityIndices.length;

    /*
     * shortcut for empty
     */
    if (sourceSamples == 0
        || domain().getSample(intensityIndices[sourceSamples - 1]) < resolvableSampleDomain
            .getInterval()
            .getLeftEndpoint()
        || domain().getSample(intensityIndices[0]) > resolvableSampleDomain
            .getInterval()
            .getRightEndpoint()) {
      double from = max(resolvableSampleDomain.getInterval().getLeftEndpoint(), 0);
      double to = min(
          resolvableSampleDomain.getInterval().getRightEndpoint(),
          domain().getInterval().getRightEndpoint());
      if (to > from) {
        return new ArraySampledContinuousFunction<>(
            new IrregularSampledDomain<>(domain().getUnit(), new double[] { from, to }),
            range().getUnit(),
            new double[] { 0, 0 });
      } else {
        return new ArraySampledContinuousFunction<>(
            new IrregularSampledDomain<>(domain().getUnit(), new double[] { from }),
            range().getUnit(),
            new double[] { 0 });
      }
    }

    /*
     * result arrays
     */
    int maximumSampleCount = sourceSamples * 3 + 2;
    int sampleCount = 0;
    double[] positions = new double[maximumSampleCount];
    double[] intensities = new double[maximumSampleCount];

    int lastSampleIndex = -1;
    for (int i = 0; i < intensityIndices.length; i++) {
      int sampleIndex = intensityIndices[i];

      if (sampleIndex > lastSampleIndex + 1) {
        if (sampleIndex > lastSampleIndex + 2) {
          positions[sampleCount] = domain().getSample(lastSampleIndex + 1);
          intensities[sampleCount] = 0;
          sampleCount++;
        }

        positions[sampleCount] = domain().getSample(sampleIndex - 1);
        intensities[sampleCount] = 0;
        sampleCount++;
      }

      positions[sampleCount] = domain().getSample(sampleIndex);
      intensities[sampleCount] = this.intensities[i];
      sampleCount++;

      lastSampleIndex = sampleIndex;
    }

    if (lastSampleIndex < getDepth() - 1) {
      positions[sampleCount] = domain().getSample(getDepth() - 1);
      intensities[sampleCount] = 0;
      sampleCount++;
    }

    return new ArraySampledContinuousFunction<>(
        new IrregularSampledDomain<>(domain().getUnit(), positions),
        range().getUnit(),
        intensities).resample(resolvableSampleDomain);
  }
}
