package uk.co.saiman.instrument.simulation;

import java.util.Random;

import uk.co.saiman.data.SampledContinuousFunction;

public interface SignalSimulation {
	public SampledContinuousFunction acquire(Random random, double resolution, int depth, SimulationSample sample);
}
