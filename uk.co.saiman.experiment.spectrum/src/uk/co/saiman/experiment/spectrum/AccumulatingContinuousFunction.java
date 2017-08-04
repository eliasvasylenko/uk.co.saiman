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
package uk.co.saiman.experiment.spectrum;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static uk.co.strangeskies.observable.Observer.onObservation;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import uk.co.saiman.data.ArraySampledContinuousFunction;
import uk.co.saiman.data.ContinuousFunction;
import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.saiman.data.SampledDomain;
import uk.co.strangeskies.observable.HotObservable;
import uk.co.strangeskies.observable.Observable;
import uk.co.strangeskies.observable.Observer;

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
public abstract class AccumulatingContinuousFunction<UD extends Quantity<UD>, UR extends Quantity<UR>>
    extends ArraySampledContinuousFunction<UD, UR> {
  private long count;

  public static <UD extends Quantity<UD>, UR extends Quantity<UR>> AccumulatingContinuousFunction<UD, UR> accumulate(
      SampledDomain<UD> domain,
      Unit<UR> unitRange) {
    HotObservable<SampledContinuousFunction<UD, UR>> source = new HotObservable<>();
    return new AccumulatingContinuousFunction<UD, UR>(source, domain, unitRange) {
      @Override
      public synchronized long accumulate(SampledContinuousFunction<UD, UR> accumulate) {
        source.next(accumulate);
        return getCount();
      }
    };
  }

  public static <UD extends Quantity<UD>, UR extends Quantity<UR>> ContinuousFunction<UD, UR> accumulateFrom(
      Observable<SampledContinuousFunction<UD, UR>> source,
      SampledDomain<UD> domain,
      Unit<UR> unitRange) {
    return new AccumulatingContinuousFunction<UD, UR>(source, domain, unitRange) {
      @Override
      public synchronized long accumulate(SampledContinuousFunction<UD, UR> accumulate) {
        throw new UnsupportedOperationException();
      }
    };
  }

  /**
   * @param domain
   *          the domain of the accumulated function
   * @param unitRange
   *          the unit of the accumulation dimension
   */
  public AccumulatingContinuousFunction(
      Observable<SampledContinuousFunction<UD, UR>> source,
      SampledDomain<UD> domain,
      Unit<UR> unitRange) {
    super(domain, unitRange, new double[domain.getDepth()]);

    source
        .then(m -> System.out.println("!"))
        .then(m -> System.out.println("?"))
        .executeOn(newSingleThreadExecutor())
        .aggregateBackpressure()
        .executeOn(newSingleThreadExecutor())
        .then(onObservation(o -> o.requestNext()))
        .then(m -> System.out.println("*"))
        .observe(Observer.singleUse(o -> m -> {
          o.requestNext();
          System.out.println(m.size());
        }));

    source
        .then(m -> count++)
        .aggregateBackpressure()
        .executeOn(newSingleThreadExecutor())
        .then(onObservation(o -> o.requestNext()))
        .observe(Observer.singleUse(o -> a -> {
          mutate(data -> {
            for (SampledContinuousFunction<?, UR> c : a) {
              UnitConverter converter = c.range().getUnit().getConverterTo(unitRange);

              for (int i = 0; i < domain.getDepth(); i++) {
                data[i] += converter.convert(c.range().getSample(i));
              }
            }
          });
          o.requestNext();
        }));
  }

  /**
   * @return the current number of accumulations
   */
  public long getCount() {
    return count;
  }

  public abstract long accumulate(SampledContinuousFunction<UD, UR> accumulate);
}
