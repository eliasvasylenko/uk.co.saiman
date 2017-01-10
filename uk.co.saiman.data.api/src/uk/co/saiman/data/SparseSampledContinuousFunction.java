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

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.Arrays;

import javax.measure.Quantity;
import javax.measure.Unit;

import uk.co.strangeskies.mathematics.expression.ImmutableExpression;

/**
 * A (currently) immutable implementation of
 * {@link RegularSampledContinuousFunction} which optimizes memory usage for
 * sampled continua with mostly 0 sample values in the codomain.
 * 
 * @param <UD>
 *          the type of the units of measurement of values in the domain
 * @param <UR>
 *          the type of the units of measurement of values in the range
 * @author Elias N Vasylenko
 */
public class SparseSampledContinuousFunction<UD extends Quantity<UD>, UR extends Quantity<UR>>
		extends ImmutableExpression<ContinuousFunction<UD, UR>> implements RegularSampledContinuousFunction<UD, UR> {
	private final Unit<UD> unitDomain;
	private final Unit<UR> unitRange;

	private final double frequency;
	private final int depth;

	private final int[] indices;
	private final double[] intensities;

	/**
	 * Instantiate based on the given significant sample indices and intensities.
	 * Samples at indices other than those given are assumed to be of intensity 0
	 * in the codomain.
	 * 
	 * @param unitDomain
	 *          the units of measurement of values in the domain
	 * @param unitRange
	 *          the units of measurement of values in the range
	 * @param frequency
	 *          The number of samples per unit in the domain
	 * @param depth
	 *          The number of conceptual samples, starting from 0
	 * @param samples
	 *          The number of non-zero samples
	 * @param indices
	 *          The sequential indices of non-zero samples
	 * @param intensities
	 *          The intensities at the given non-zero sample indices
	 */
	public SparseSampledContinuousFunction(Unit<UD> unitDomain, Unit<UR> unitRange, double frequency, int depth,
			int samples, int[] indices, double[] intensities) {
		this.unitDomain = unitDomain;
		this.unitRange = unitRange;

		this.frequency = frequency;
		this.depth = depth;

		/*
		 * TODO sort the indices & intensities here
		 */

		this.indices = Arrays.copyOf(indices, samples);
		this.intensities = Arrays.copyOf(intensities, samples);
	}

	/**
	 * Create a memory efficient view of the given array, with the given
	 * frequency.
	 * 
	 * @param unitDomain
	 *          the units of measurement of values in the domain
	 * @param unitRange
	 *          the units of measurement of values in the range
	 * @param frequency
	 *          The number of samples per unit in the domain
	 * @param intensities
	 *          The intensities as a sequence of samples at the given frequency
	 */
	public SparseSampledContinuousFunction(Unit<UD> unitDomain, Unit<UR> unitRange, double frequency,
			double[] intensities) {
		this.unitDomain = unitDomain;
		this.unitRange = unitRange;

		this.frequency = frequency;
		int depth = 0;

		for (double intensity : intensities) {
			if (intensity != 0) {
				depth++;
			}
		}

		this.depth = depth;

		this.indices = new int[depth];
		this.intensities = new double[depth];

		int index = 0;
		for (int i = 0; i < intensities.length; i++) {
			if (intensities[i] != 0) {
				indices[index] = i;
				this.intensities[index] = intensities[i];

				index++;
			}
		}
	}

	@Override
	public Unit<UD> getDomainUnit() {
		return unitDomain;
	}

	@Override
	public Unit<UR> getRangeUnit() {
		return unitRange;
	}

	private int getIndexIndex(int index) {
		int from = 0;
		int to = indices.length - 1;

		if (to < 0) {
			return -1;
		}

		do {
			if (indices[to] < index) {
				return -1;
			} else if (indices[to] == index) {
				return to;
			} else if (indices[from] > index) {
				return -1;
			} else if (indices[from] == index) {
				return from;
			} else {
				int mid = (to + from) / 2;
				if (indices[mid] > index) {
					to = mid;
				} else {
					from = mid;
				}
			}
		} while (to - from > 1);

		return -1;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public double getY(int index) {
		int indexIndex = getIndexIndex(index);
		if (indexIndex < 0) {
			return 0;
		} else {
			return intensities[indexIndex];
		}
	}

	@Override
	public double getFrequency() {
		return frequency;
	}

	@Override
	public SparseSampledContinuousFunction<UD, UR> copy() {
		return this;
	}

	@Override
	public ContinuousFunction<UD, UR> getValue() {
		return this;
	}

	@Override
	public SampledContinuousFunction<UD, UR> resample(double startX, double endX, int resolvableUnits) {
		int sourceSamples = indices.length;

		/*
		 * shortcut for empty
		 */
		if (sourceSamples == 0 || getX(indices[sourceSamples - 1]) < startX || getX(indices[0]) > endX) {
			double from = max(startX, 0);
			double to = min(endX, getRange().getTo());
			if (to > from) {
				return new ArraySampledContinuousFunction<>(getDomainUnit(), getRangeUnit(), 2, new double[] { from, to },
						new double[] { 0, 0 });
			} else {
				return new ArraySampledContinuousFunction<>(getDomainUnit(), getRangeUnit(), 2, new double[] { from },
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
		for (int i = 0; i < indices.length; i++) {
			int sampleIndex = indices[i];

			if (sampleIndex > lastSampleIndex + 1) {
				if (sampleIndex > lastSampleIndex + 2) {
					positions[sampleCount] = getX(lastSampleIndex + 1);
					intensities[sampleCount] = 0;
					sampleCount++;
				}

				positions[sampleCount] = getX(sampleIndex - 1);
				intensities[sampleCount] = 0;
				sampleCount++;
			}

			positions[sampleCount] = getX(sampleIndex);
			intensities[sampleCount] = this.intensities[i];
			sampleCount++;

			lastSampleIndex = sampleIndex;
		}

		if (lastSampleIndex < getDepth() - 1) {
			positions[sampleCount] = getX(getDepth() - 1);
			intensities[sampleCount] = 0;
			sampleCount++;
		}

		return new ArraySampledContinuousFunction<>(getDomainUnit(), getRangeUnit(), sampleCount, positions, intensities)
				.resample(startX, endX, resolvableUnits);
	}
}
