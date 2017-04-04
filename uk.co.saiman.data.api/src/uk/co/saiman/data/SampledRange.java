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

import javax.measure.Quantity;
import javax.measure.Unit;

public abstract class SampledRange<U extends Quantity<U>> implements Range<U>, SampledDimension<U> {
	private final SampledContinuousFunction<?, U> function;

	public SampledRange(SampledContinuousFunction<?, U> function) {
		this.function = function;
	}

	@Override
	public uk.co.strangeskies.mathematics.Range<Double> getExtent() {
		if (getDepth() == 0)
			return uk.co.strangeskies.mathematics.Range.between(0d, 0d).setInclusive(false, false);
		return between(0, getDepth() - 1).getExtent();
	}

	/**
	 * Find the interval between the smallest to the largest value of the codomain
	 * of the function within the interval in the domain described by the given
	 * sample indices.
	 * 
	 * @param startIndex
	 *          The index of the sample at the beginning of the domain interval
	 *          whose range we wish to determine
	 * @param endIndex
	 *          The index of the sample at the end of the domain interval whose
	 *          range we wish to determine
	 * @return The range from the smallest to the largest value of the codomain of
	 *         the function within the given interval
	 */
	public SampledRange<U> betweenIndices(int startIndex, int endIndex) {
		if (startIndex < 0)
			startIndex = 0;
		if (endIndex >= getDepth())
			endIndex = getDepth() - 1;

		double startSample = getSample(startIndex);
		double endSample = getSample(endIndex);

		uk.co.strangeskies.mathematics.Range<Double> yRange = uk.co.strangeskies.mathematics.Range
				.between(startSample, endSample);

		for (int i = startIndex; i < endIndex; i++)
			yRange.extendThrough(getSample(i), true);

		int finalStartIndex = startIndex;
		SampledRange<U> component = this;
		return new SampledRange<U>(function) {
			@Override
			public uk.co.strangeskies.mathematics.Range<Double> getExtent() {
				return yRange;
			}

			@Override
			public Unit<U> getUnit() {
				return component.getUnit();
			}

			@Override
			public int getDepth() {
				return component.getDepth();
			}

			@Override
			public double getSample(int index) {
				return component.getSample(index + finalStartIndex);
			}

			@Override
			public SampledRange<U> between(double startX, double endX) {
				return component.between(startX + startSample, endX + startSample);
			}

			@Override
			public SampledRange<U> betweenIndices(int startIndex, int endIndex) {
				return super.betweenIndices(startIndex + finalStartIndex, endIndex + finalStartIndex);
			}
		};
	}

	@Override
	public SampledRange<U> between(double startX, double endX) {
		if (getDepth() == 0) {
			return betweenIndices(0, 0);
		}

		double startSample = function.sample(startX);
		double endSample = function.sample(endX);

		int startIndex = function.domain().getIndexAbove(startX);
		int endIndex = function.domain().getIndexBelow(endX);

		uk.co.strangeskies.mathematics.Range<Double> yRange;
		if (getDepth() > 2) {
			yRange = betweenIndices(startIndex, endIndex).getExtent();
		} else {
			yRange = uk.co.strangeskies.mathematics.Range.between(startSample, startSample);
		}

		yRange.extendThrough(startSample, true);
		yRange.extendThrough(endSample, true);

		SampledRange<U> component = this;
		return new SampledRange<U>(function) {
			@Override
			public uk.co.strangeskies.mathematics.Range<Double> getExtent() {
				return yRange;
			}

			@Override
			public Unit<U> getUnit() {
				return component.getUnit();
			}

			@Override
			public int getDepth() {
				return component.getDepth();
			}

			@Override
			public double getSample(int index) {
				return component.getSample(index + startIndex);
			}

			@Override
			public SampledRange<U> between(double startX, double endX) {
				return component.between(startX + startSample, endX + startSample);
			}

			@Override
			public SampledRange<U> betweenIndices(int start, int end) {
				return super.betweenIndices(start + startIndex, end + startIndex);
			}
		};
	}
}
