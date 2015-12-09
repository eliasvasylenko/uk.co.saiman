package uk.co.saiman.data.api;

import java.util.Arrays;

public class SparseSampledContinuum implements RegularSampledContinuum {
	private final double frequency;
	private final int depth;

	private int[] indices;
	private double[] intensities;

	public SparseSampledContinuum(double frequency, int depth, int samples, int[] indices, double[] intensities) {
		this.frequency = frequency;
		this.depth = depth;

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
