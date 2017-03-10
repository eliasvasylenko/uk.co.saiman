package uk.co.saiman.experiment.spectrum;

import static uk.co.saiman.experiment.spectrum.RegularSampledContinuousFunctionByteFormat.overEncodedDomain;

import java.nio.file.Path;

import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import uk.co.saiman.data.RegularSampledDomain;
import uk.co.saiman.data.SampledContinuousFunction;

public class AccumulatingFileSpectrum extends FileSpectrum<SampledContinuousFunction<Time, Dimensionless>> {
	private final AccumulatingContinuousFunction<Time, Dimensionless> accumulation;

	public AccumulatingFileSpectrum(
			Path location,
			String name,
			RegularSampledDomain<Time> sampleDomain,
			Unit<Dimensionless> sampleIntensityUnits) {
		this(
				location,
				name,
				sampleDomain,
				sampleIntensityUnits,
				new AccumulatingContinuousFunction<>(sampleDomain, sampleIntensityUnits));
	}

	private AccumulatingFileSpectrum(
			Path location,
			String name,
			RegularSampledDomain<Time> sampleDomain,
			Unit<Dimensionless> sampleIntensityUnits,
			AccumulatingContinuousFunction<Time, Dimensionless> accumulation) {
		super(location, name, accumulation, overEncodedDomain(sampleDomain.getUnit(), sampleIntensityUnits));

		this.accumulation = accumulation;
	}

	public long accumulate(SampledContinuousFunction<Time, Dimensionless> accumulate) {
		return accumulation.accumulate(accumulate);
	}
}
