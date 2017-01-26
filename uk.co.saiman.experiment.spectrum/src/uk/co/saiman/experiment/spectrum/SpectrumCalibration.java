package uk.co.saiman.experiment.spectrum;

import javax.measure.Quantity;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;

public interface SpectrumCalibration {
	Quantity<Mass> getMass(Quantity<Time> time);
}
