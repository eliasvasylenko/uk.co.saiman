package uk.co.saiman.instrument.simulation;

import java.util.Map;

import uk.co.saiman.chemistry.ChemicalComposition;

public interface SimulationSample {
	Map<ChemicalComposition, Double> chemicalIntensities();
}
