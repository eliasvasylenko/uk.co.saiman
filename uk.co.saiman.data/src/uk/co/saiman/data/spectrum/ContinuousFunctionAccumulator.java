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
import static uk.co.saiman.observable.Observer.onCompletion;
import static uk.co.saiman.observable.Observer.onObservation;
import static uk.co.saiman.observable.Observer.singleUse;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import uk.co.saiman.data.function.ArraySampledContinuousFunction;
import uk.co.saiman.data.function.SampledContinuousFunction;
import uk.co.saiman.data.function.SampledDomain;
import uk.co.saiman.observable.Invalidation;
import uk.co.saiman.observable.LockException;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.observable.Observation;

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
  private final CountDownLatch complete;
  private final SampledDomain<UD> domain;
  private final Unit<UR> unitRange;
  private double[] intensities;
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

    complete = new CountDownLatch(1);
    accumulation = source
        .aggregateBackpressure()
        .executeOn(newSingleThreadExecutor())
        .map(this::aggregateArray)
        .then(onObservation(Observation::requestNext))
        .then(singleUse(o -> m -> o.requestNext()))
        .then(onCompletion(complete::countDown))
        .invalidateLazyRevalidate()
        .reemit()
        .map(m -> m.map(i -> {
          synchronized (i) {
            return new ArraySampledContinuousFunction<>(domain, unitRange, intensities);
          }
        }));
  }

  private double[] aggregateArray(List<SampledContinuousFunction<UD, UR>> next) {
    synchronized (intensities) {
      for (SampledContinuousFunction<?, UR> c : next) {
        UnitConverter converter = c.range().getUnit().getConverterTo(unitRange);

        for (int i = 0; i < domain.getDepth(); i++) {
          intensities[i] += converter.convert(c.range().getSample(i));
        }
      }
    }
    return intensities;
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

  public SampledContinuousFunction<UD, UR> getAccumulation() {
    try {
      System.out.println("wait!");
      complete.await();
      System.out.println("waited!");
    } catch (InterruptedException e) {
      new LockException(e);
    }
    return new ArraySampledContinuousFunction<>(domain, unitRange, intensities);
  }
}
