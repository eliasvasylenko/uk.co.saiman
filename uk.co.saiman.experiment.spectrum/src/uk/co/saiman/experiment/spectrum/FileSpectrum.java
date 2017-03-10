package uk.co.saiman.experiment.spectrum;

import static java.util.function.Function.identity;

import java.io.IOException;
import java.nio.file.Path;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;

import uk.co.saiman.data.ContinuousFunction;
import uk.co.saiman.experiment.CachingObservableResource;
import uk.co.saiman.experiment.CachingResource;

public class FileSpectrum<C extends ContinuousFunction<Time, Dimensionless>> implements Spectrum {
	private final CachingResource<C> data;

	private final Path location;
	private final ByteFormat<C> format;

	protected FileSpectrum(Path location, String name, C data, ByteFormat<C> format) {
		this.data = new CachingObservableResource<>(this::load, this::save, identity());

		this.location = location.resolve(name + "." + format.getPathExtension());
		this.format = format;

		this.data.setData(data);
	}

	public Path getLocation() {
		return location;
	}

	@Override
	public void complete() {
		data.save();
	}

	protected C load() {
		try {
			return format.load(location);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void save(C data) {
		try {
			format.save(location, data);
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
