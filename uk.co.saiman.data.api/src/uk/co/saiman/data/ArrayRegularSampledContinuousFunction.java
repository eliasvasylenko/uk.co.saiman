/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,-========\     ,`===\    /========== \
 *      /== \___/== \  ,`==.== \   \__/== \___\/
 *     /==_/____\__\/,`==__|== |     /==  /
 *     \========`. ,`========= |    /==  /
 *   ___`-___)== ,`== \____|== |   /==  /
 *  /== \__.-==,`==  ,`    |== '__/==  /_
 *  \======== /==  ,`      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.data.api.
 *
 * uk.co.saiman.data.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.data.api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.data;

import java.util.function.Consumer;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * A mutable implementation conforming to the contract of
 * {@link RegularSampledContinuousFunction} and backed by an array.
 * 
 * @param <UD>
 *          the type of the units of measurement of values in the domain
 * @param <UR>
 *          the type of the units of measurement of values in the range
 * @author Elias N Vasylenko
 */
public class ArrayRegularSampledContinuousFunction<UD extends Quantity<UD>, UR extends Quantity<UR>>
		extends LockingSampledContinuousFunction<UD, UR> implements RegularSampledContinuousFunction<UD, UR> {
	private final double frequency;
	private final double domainStart;
	private final double[] intensities;

	/**
	 * Create an instance with the given frequency, intensities, and domain value
	 * of first index.
	 * 
	 * @param unitDomain
	 *          the units of measurement of values in the domain
	 * @param unitRange
	 *          the units of measurement of values in the range
	 * @param frequency
	 *          The frequency as per {@link #getFrequency()}
	 * @param domainStart
	 *          The domain value of the sample at the first index
	 * @param intensities
	 *          The intensities of each sample in sequence
	 */
	public ArrayRegularSampledContinuousFunction(Unit<UD> unitDomain, Unit<UR> unitRange, double frequency,
			double domainStart, double[] intensities) {
		super(unitDomain, unitRange);

		this.frequency = frequency;
		this.domainStart = domainStart;

		this.intensities = intensities.clone();
	}

	@Override
	public int getDepth() {
		return read(() -> intensities.length);
	}

	@Override
	public double getY(int index) {
		return read(() -> intensities[index]);
	}

	/**
	 * Safely modify the data of this sampled continuous function.
	 * 
	 * @param mutation
	 *          The mutation operation
	 */
	public void mutate(Consumer<double[]> mutation) {
		beginWrite();
		try {
			mutation.accept(intensities);
		} finally {
			endWrite();
		}
	}

	@Override
	public double getFrequency() {
		return read(() -> frequency);
	}

	@Override
	public double getX(int index) {
		return read(() -> RegularSampledContinuousFunction.super.getX(index) + domainStart);
	}

	@Override
	public int getIndexBelow(double xValue) {
		return read(() -> RegularSampledContinuousFunction.super.getIndexBelow(xValue));
	}

	@Override
	public ArrayRegularSampledContinuousFunction<UD, UR> copy() {
		return read(() -> new ArrayRegularSampledContinuousFunction<>(getDomainUnit(), getRangeUnit(), frequency,
				domainStart, intensities));
	}

	@Override
	public ContinuousFunction<UD, UR> evaluate() {
		return this;
	}
}
