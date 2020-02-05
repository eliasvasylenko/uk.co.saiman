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
package uk.co.saiman.data.spectrum;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import uk.co.saiman.data.function.ArraySampledContinuousFunction;
import uk.co.saiman.data.function.SampledContinuousFunction;
import uk.co.saiman.data.function.SampledDomain;
import uk.co.saiman.observable.ExclusiveObserver;
import uk.co.saiman.observable.HotObservable;
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
  private final SampledDomain<UD> domain;
  private final Unit<UR> unitRange;
  private double[] intensities;

  private Throwable failure;
  private final CountDownLatch complete = new CountDownLatch(1);
  private SampledContinuousFunction<UD, UR> lastFunction;
  private final HotObservable<ContinuousFunctionAccumulator<UD, UR>> accumulation = new HotObservable<>();

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

    getLatestAccumulation();

    source
        .executeOn(newSingleThreadExecutor())
        .aggregateBackpressure()
        .observe(new ExclusiveObserver<>() {
          @Override
          public void onNext(List<SampledContinuousFunction<UD, UR>> message) {
            aggregateArray(message);
            getObservation().requestNext();
          }

          public void onObserve(Observation observation) {
            super.onObserve(observation);
            observation.requestNext();
          }

          @Override
          public void onComplete() {
            complete();
          }

          @Override
          public void onFail(Throwable t) {
            fail(t);
          }
        });
  }

  private void complete() {
    complete.countDown();
  }

  private void fail(Throwable t) {
    failure = t;
    complete.countDown();
  }

  private void aggregateArray(List<SampledContinuousFunction<UD, UR>> message) {
    synchronized (intensities) {
      for (SampledContinuousFunction<?, UR> c : message) {
        UnitConverter converter = c.range().getUnit().getConverterTo(unitRange);

        for (int i = 0; i < domain.getDepth(); i++) {
          intensities[i] += converter.convert(c.range().getSample(i));
        }
      }

      if (lastFunction != null) {
        lastFunction = null;
        accumulation.next(this);
      }
    }
  }

  public SampledDomain<UD> getDomain() {
    return domain;
  }

  public Unit<UR> getUnitRange() {
    return unitRange;
  }

  public Observable<ContinuousFunctionAccumulator<UD, UR>> accumulation() {
    return accumulation;
  }

  public SampledContinuousFunction<UD, UR> getLatestAccumulation() {
    synchronized (intensities) {
      if (lastFunction == null) {
        lastFunction = new ArraySampledContinuousFunction<>(domain, unitRange, intensities);
      }
      return lastFunction;
    }
  }

  public SampledContinuousFunction<UD, UR> getCompleteAccumulation() {
    try {
      complete.await();
    } catch (InterruptedException e) {
      new LockException(e);
    }
    if (failure != null)
      throw new IllegalStateException("Failed to accumulate", failure);
    return getLatestAccumulation();
  }
}
