package uk.co.saiman.experiment.spectrum;

import static java.nio.file.Files.newByteChannel;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;

import uk.co.saiman.data.ContinuousFunction;
import uk.co.saiman.experiment.CachingResource;

public class FileSpectrum<C extends ContinuousFunction<Time, Dimensionless>> implements Spectrum {
	private final CachingResource<C> data;

	private final Path location;
	private final ByteFormat<C> format;

	protected FileSpectrum(C data, Path location, ByteFormat<C> format) {
		this.data = new CachingResource<>(this::load, this::save);
		this.data.setData(data);

		this.location = location;
		this.format = format;
	}

	public Path getLocation() {
		return location;
	}

	@Override
	public void save() {
		data.save();
	}

	protected C load() {
		try (ReadableByteChannel inputChannel = newByteChannel(location, READ)) {
			return format.load(inputChannel);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void save(C data) {
		try (WritableByteChannel outputChannel = newByteChannel(location, WRITE, CREATE)) {
			format.save(outputChannel, data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ContinuousFunction<Time, Dimensionless> getRawData() {
		return data.getData();
	}

	@Override
	public ContinuousFunction<Mass, Dimensionless> getCalibratedData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SpectrumCalibration getCalibration() {
		// TODO Auto-generated method stub
		return null;
	}
}
