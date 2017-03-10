package uk.co.saiman.experiment.spectrum;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.measure.Quantity;
import javax.measure.Unit;

import uk.co.saiman.data.RegularSampledDomain;

public class RegularSampledDomainByteFormat<U extends Quantity<U>> implements ByteFormat<RegularSampledDomain<U>> {
	private static final int SIZE = Double.BYTES * 2 + Integer.BYTES;
	private static final String MASS_SPECTRUM_DOMAIN_EXTENSION = "msd";

	private final Unit<U> domainUnit;

	public RegularSampledDomainByteFormat(Unit<U> domainUnit) {
		this.domainUnit = domainUnit;
	}

	@Override
	public String getPathExtension() {
		return MASS_SPECTRUM_DOMAIN_EXTENSION;
	}

	@Override
	public RegularSampledDomain<U> load(ReadableByteChannel inputChannel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(SIZE);
		do {
			inputChannel.read(buffer);
		} while (buffer.hasRemaining());
		buffer.flip();
		double frequency = buffer.getDouble();
		double domainStart = buffer.getDouble();
		int depth = buffer.getInt();

		return new RegularSampledDomain<>(domainUnit, depth, frequency, domainStart);
	}

	@Override
	public void save(WritableByteChannel outputChannel, RegularSampledDomain<U> domain) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(SIZE);
		buffer.putDouble(domain.getFrequency());
		buffer.putDouble(domain.getExtent().getFrom());
		buffer.putInt(domain.getDepth());
		buffer.flip();
		do {
			outputChannel.write(buffer);
		} while (buffer.hasRemaining());
	}
}
