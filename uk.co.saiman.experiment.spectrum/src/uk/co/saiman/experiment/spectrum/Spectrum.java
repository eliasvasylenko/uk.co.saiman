package uk.co.saiman.experiment.spectrum;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;

import uk.co.saiman.data.ContinuousFunction;

public interface Spectrum {
	void complete();

	ContinuousFunction<Time, Dimensionless> getRawData();

	ContinuousFunction<Mass, Dimensionless> getCalibratedData();

	/**
	 * @return the calibration settings of the spectrum experiment at the time of
	 *         collection
	 */
	SpectrumCalibration getCalibration();
}
