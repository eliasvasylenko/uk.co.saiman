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

import java.util.concurrent.atomic.AtomicLong;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import uk.co.saiman.data.ArraySampledContinuousFunction;
import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.saiman.data.SampledDomain;
import uk.co.strangeskies.utilities.AggregatingListener;
import uk.co.strangeskies.utilities.Observer;

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
public class AccumulatingContinuousFunction<UD extends Quantity<UD>, UR extends Quantity<UR>>
		extends ArraySampledContinuousFunction<UD, UR> {
	private AtomicLong count = new AtomicLong();
	private final Observer<? super SampledContinuousFunction<?, UR>> aggregator;

	/**
	 * @param domain
	 *          the domain of the accumulated function
	 * @param unitRange
	 *          the unit of the accumulation dimension
	 */
	public AccumulatingContinuousFunction(SampledDomain<UD> domain, Unit<UR> unitRange) {
		super(domain, unitRange, new double[domain.getDepth()]);

		AggregatingListener<SampledContinuousFunction<?, UR>> aggregator = new AggregatingListener<>(
				newSingleThreadExecutor());
		aggregator.addObserver(a -> {
			mutate(data -> {
				for (SampledContinuousFunction<?, UR> c : a) {
					UnitConverter converter = c.range().getUnit().getConverterTo(unitRange);

					for (int i = 0; i < domain.getDepth(); i++) {
						data[i] += converter.convert(c.range().getSample(i));
					}
				}
			});
		});

		this.aggregator = aggregator;
	}

	/**
	 * Add the data from the given function to the accumulations
	 * 
	 * @param function
	 *          the function to add
	 * @return the accumulation count after the operation
	 */
	public synchronized long accumulate(SampledContinuousFunction<?, UR> function) {
		aggregator.notify(function);
		return count.incrementAndGet();
	}

	/**
	 * @return the current number of accumulations
	 */
	public long getCount() {
		return count.get();
	}
}
