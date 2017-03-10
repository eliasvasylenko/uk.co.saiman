package uk.co.saiman.experiment.chemicalmap;

import java.nio.file.Path;

import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import uk.co.saiman.data.RegularSampledDomain;
import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.saiman.experiment.spectrum.RegularSampledContinuousFunctionByteFormat;
import uk.co.saiman.experiment.spectrum.RegularSampledDomainByteFormat;

public class AccumulatingFileChemicalMap extends FileChemicalMap<SampledContinuousFunction<Time, Dimensionless>> {
	public AccumulatingFileChemicalMap(
			Path location,
			String name,
			int width,
			int height,
			RegularSampledDomain<Time> domain,
			Unit<Dimensionless> rangeUnit) {
		super(
				location,
				name,
				width,
				height,
				domain,
				new RegularSampledDomainByteFormat<>(domain.getUnit()),
				d -> RegularSampledContinuousFunctionByteFormat.overDomain(d, rangeUnit));
	}
}
