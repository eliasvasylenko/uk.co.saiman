package uk.co.saiman.experiment.spectrum;

import static uk.co.saiman.experiment.spectrum.RegularSampledContinuousFunctionByteFormat.overEncodedDomain;

import java.nio.file.Path;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import uk.co.saiman.data.SampledContinuousFunction;

public class DefaultFileSpectrum extends FileSpectrum<SampledContinuousFunction<Time, Dimensionless>> {
	protected DefaultFileSpectrum(SampledContinuousFunction<Time, Dimensionless> data, Path location) {
		super(data, location, overEncodedDomain(data.domain().getUnit(), data.range().getUnit()));
	}
}
