/*
 * Copyright (C) 2015 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import java.util.Arrays;

public class SparseSampledContinuum implements RegularSampledContinuum {
	private final double frequency;
	private final int depth;

	private final int[] indices;
	private final double[] intensities;

	public SparseSampledContinuum(double frequency, int depth, int samples, int[] indices, double[] intensities) {
		this.frequency = frequency;
		this.depth = depth;

		/*
		 * TODO sort the indices & intensities here
		 */
		
		this.indices = Arrays.copyOf(indices, samples);
		this.intensities = Arrays.copyOf(intensities, samples);
	}

	private int getIndexIndex(int index) {
		int from = 0;
		int to = indices.length;

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
					to = index;
				} else {
					from = index;
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
	public double getYSample(int index) {
		int indexIndex = getIndexIndex(index);
		if (indexIndex < 0) {
			return 0;
		} else {
			return intensities[indexIndex];
		}
	}

	@Override
	public InterpolationStrategy getInterpolationStrategy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getFrequency() {
		return frequency;
	}
}
