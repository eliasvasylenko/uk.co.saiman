/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.spectrum.
 *
 * uk.co.saiman.experiment.spectrum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.spectrum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.data.spectrum;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static uk.co.saiman.observable.Observer.onObservation;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import uk.co.saiman.data.function.ArraySampledContinuousFunction;
import uk.co.saiman.data.function.SampledContinuousFunction;
import uk.co.saiman.data.function.SampledDomain;
import uk.co.saiman.observable.Invalidation;
import uk.co.saiman.observable.Observable;

/**
 * A continuous function to accumulate the sum of input continuous functions.
 * Accumulations made in rapid succession are batched so as to only lock for
 * writing sparingly and minimize update events.
 * 
 * @author Elias N Vasylenko
 *
 * @param <UD>
 *          the type of the units of measurement of values in the domain
 * @param <UR>
 *          the type of the units of measurement of values in the range
 */
public class ContinuousFunctionAccumulator<UD extends Quantity<UD>, UR extends Quantity<UR>> {
  private final SampledDomain<UD> domain;
  private final Unit<UR> unitRange;
  private double[] intensities;
  private long count;
  private final Observable<Invalidation<SampledContinuousFunction<UD, UR>>> accumulation;

  /**
   * @param domain
   *          the domain of the accumulated function
   * @param unitRange
   *          the unit of the accumulation dimension
   */
  public ContinuousFunctionAccumulator(
      Observable<SampledContinuousFunction<UD, UR>> source,
      SampledDomain<UD> domain,
      Unit<UR> unitRange) {
    this.domain = domain;
    this.unitRange = unitRange;
    this.intensities = new double[domain.getDepth()];

    accumulation = source
        .then(m -> count++)
        .aggregateBackpressure()
        .executeOn(newSingleThreadExecutor())
        .then(onObservation(o -> o.requestUnbounded()))
        .map(a -> {
          synchronized (intensities) {
            for (SampledContinuousFunction<?, UR> c : a) {
              UnitConverter converter = c.range().getUnit().getConverterTo(unitRange);

              for (int i = 0; i < domain.getDepth(); i++) {
                intensities[i] += converter.convert(c.range().getSample(i));
              }
            }
          }
          return intensities;
        })
        .invalidateLazyRevalidate()
        .map(m -> m.map(i -> {
          synchronized (i) {
            return new ArraySampledContinuousFunction<>(domain, unitRange, intensities);
          }
        }));
  }

  /**
   * @return the current number of accumulations
   */
  public long getCount() {
    return count;
  }

  public SampledDomain<UD> getDomain() {
    return domain;
  }

  public Unit<UR> getUnitRange() {
    return unitRange;
  }

  public Observable<Invalidation<SampledContinuousFunction<UD, UR>>> accumulation() {
    return accumulation;
  }
}
