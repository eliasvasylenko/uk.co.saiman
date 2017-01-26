package uk.co.saiman.experiment.spectrum;

import static java.nio.ByteBuffer.allocate;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.Unit;

import uk.co.saiman.data.ArraySampledContinuousFunction;
import uk.co.saiman.data.RegularSampledDomain;
import uk.co.saiman.data.SampledContinuousFunction;

/**
 * @author Elias N Vasylenko
 *
 * @param <UD>
 *          the type of the units of measurement of values in the domain
 * @param <UR>
 *          the type of the units of measurement of values in the range
 */
public class RegularSampledContinuousFunctionByteFormat<UD extends Quantity<UD>, UR extends Quantity<UR>>
		implements ByteFormat<SampledContinuousFunction<UD, UR>> {
	private final RegularSampledDomain<UD> domain;
	private final Unit<UR> rangeUnit;

	protected RegularSampledContinuousFunctionByteFormat(RegularSampledDomain<UD> domain, Unit<UR> rangeUnit) {
		this.domain = domain;
		this.rangeUnit = rangeUnit;
	}

	/**
	 * @param domain
	 *          a description of the domain of the function to format
	 * @param rangeUnit
	 *          the type of the units of measurement of values in the range
	 * @return a continuous function byte format for the given domain frequency
	 *         and domain start
	 */
	public static <UD extends Quantity<UD>, UR extends Quantity<UR>> ByteFormat<SampledContinuousFunction<UD, UR>> overDomain(
			RegularSampledDomain<UD> domain,
			Unit<UR> rangeUnit) {
		return new RegularSampledContinuousFunctionByteFormat<>(domain, rangeUnit);
	}

	/**
	 * @param domainUnit
	 *          the type of the units of measurement of values in the domain
	 * @param rangeUnit
	 *          the type of the units of measurement of values in the range
	 * @return a continuous function byte format which encodes the domain start
	 *         and domain frequency
	 */
	public static <UD extends Quantity<UD>, UR extends Quantity<UR>> ByteFormat<SampledContinuousFunction<UD, UR>> overEncodedDomain(
			Unit<UD> domainUnit,
			Unit<UR> rangeUnit) {
		return new ByteFormat<SampledContinuousFunction<UD, UR>>() {
			@Override
			public SampledContinuousFunction<UD, UR> load(ReadableByteChannel inputChannel) throws IOException {
				RegularSampledDomain<UD> domain = new RegularSampledDomainByteFormat<>(domainUnit).load(inputChannel);
				return overDomain(domain, rangeUnit).load(inputChannel);
			}

			@Override
			public void save(WritableByteChannel outputChannel, SampledContinuousFunction<UD, UR> data) throws IOException {
				RegularSampledDomain<UD> domain = (RegularSampledDomain<UD>) data.domain();
				new RegularSampledDomainByteFormat<>(domainUnit).save(outputChannel, domain);
				overDomain(domain, rangeUnit).save(outputChannel, data);
			}
		};
	}

	@Override
	public SampledContinuousFunction<UD, UR> load(ReadableByteChannel inputChannel) throws IOException {
		ByteBuffer buffer = allocate(Double.BYTES * domain.getDepth());
		while (buffer.hasRemaining()) {
			inputChannel.read(buffer);
		}
		buffer.flip();

		double[] intensities = new double[domain.getDepth()];
		buffer.asDoubleBuffer().get(intensities);

		return new ArraySampledContinuousFunction<>(domain, rangeUnit, intensities);
	}

	@Override
	public void save(WritableByteChannel outputChannel, SampledContinuousFunction<UD, UR> data) throws IOException {
		if (!(data.domain() instanceof RegularSampledDomain<?>)) {
			throw new IllegalArgumentException();
		}
		RegularSampledDomain<UD> dataDomain = (RegularSampledDomain<UD>) data.domain();

		if (dataDomain.getFrequency() != domain.getFrequency()) {
			throw new IllegalArgumentException(dataDomain.getFrequency() + " != " + domain.getFrequency());
		}
		if (!Objects.equals(dataDomain.getExtent().getFrom(), domain.getExtent().getFrom())) {
			throw new IllegalArgumentException(dataDomain.getExtent().getFrom() + " != " + domain.getExtent().getFrom());
		}
		if (dataDomain.getDepth() != domain.getDepth()) {
			throw new IllegalArgumentException(dataDomain.getDepth() + " != " + domain.getDepth());
		}

		ByteBuffer buffer = allocate(Double.BYTES * data.getDepth());
		for (int i = 0; i > data.getDepth(); i++) {
			buffer.putDouble(data.range().getSample(i));
		}
		buffer.flip();
		while (buffer.hasRemaining()) {
			outputChannel.write(buffer);
		}
	}
}
