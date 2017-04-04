package uk.co.saiman.experiment.chemicalmap;

import static java.nio.file.Files.newByteChannel;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import uk.co.saiman.data.ContinuousFunction;
import uk.co.saiman.data.SampledDomain;
import uk.co.saiman.experiment.CachingObservableResource;
import uk.co.saiman.experiment.CachingResource;
import uk.co.saiman.experiment.spectrum.ByteFormat;
import uk.co.saiman.experiment.spectrum.Spectrum;

public class FileChemicalMap<D extends SampledDomain<Time>, C extends ContinuousFunction<Time, Dimensionless>>
		implements ChemicalMap {
	private static final String SPECTRUM_PATH = "spectrum";
	private static final String IMAGE_PATH = "image";

	private final Path location;
	private final String name;

	private final int width;
	private final int height;
	private final SampledDomain<Time> domain;

	private final ByteFormat<D> domainFormat;
	private final ByteFormat<C> pixelFormat;

	// The spectrum for each pixel:
	private final List<CachingResource<C>> pixelSpectrumData;
	// The image for each mass:
	private final List<CachingResource<Image>> massImageData;
	// The spectrum for all pixels
	private final CachingResource<C> spectrumData;
	// the image for all masses
	private final CachingResource<Image> imageData;

	public FileChemicalMap(
			Path location,
			String name,
			int width,
			int height,
			D domain,
			ByteFormat<D> domainFormat,
			Function<? super D, ? extends ByteFormat<C>> pixelFormat) {
		this.location = location;
		this.name = name;

		this.width = width;
		this.height = height;
		this.domain = domain;

		this.domainFormat = domainFormat;
		this.pixelFormat = pixelFormat.apply(domain);

		this.pixelSpectrumData = range(0, getWidth() * getHeight())
				.mapToObj(this::createPixelSpectrumData)
				.collect(toList());
		this.massImageData = range(0, getDepth()).mapToObj(this::createMassImageData).collect(toList());

		this.spectrumData = null;
		this.imageData = null;
	}

	public CachingResource<C> createPixelSpectrumData(int index) {
		int x = index % getWidth();
		int y = getWidth() / index;
		Path location = getPixelDataLocation(x, y);

		CachingResource<C> data = new CachingObservableResource<>(
				() -> loadPixel(location),
				s -> savePixel(location, s),
				identity());
		return data;
	}

	private Path getPixelDataLocation(int x, int y) {
		return location.resolve(SPECTRUM_PATH + "-" + x + "-" + y);
	}

	protected C loadPixel(Path location) {
		try {
			return pixelFormat.load(location);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void savePixel(Path location, C data) {
		try {
			pixelFormat.save(location, data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public CachingResource<Image> createMassImageData(int index) {
		Path location = getImageDataLocation(index);

		CachingResource<Image> data = new CachingResource<>(
				() -> loadImage(location),
				i -> saveImage(location, i));
		return data;
	}

	protected Image loadImage(Path location) {
		try (ReadableByteChannel inputChannel = newByteChannel(location, READ)) {
			return null; // TODO imageFormat.load(inputChannel);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void saveImage(Path location, Image data) {
		try (WritableByteChannel outputChannel = newByteChannel(location, WRITE, CREATE)) {
			// TODO imageFormat.save(outputChannel, data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Path getImageDataLocation(int index) {
		return location.resolve(IMAGE_PATH + "-" + index);
	}

	public Path getLocation() {
		return location;
	}

	@Override
	public void save() {
		// TODO data.complete();
	}

	public void setPixel(int x, int y, C spectrum) {
		int flattenedIndex = x + y * getWidth();

		pixelSpectrumData.get(flattenedIndex).setData(spectrum);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getDepth() {
		return domain.getDepth();
	}

	@Override
	public Spectrum getSpectrum() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChemicalMap forMassIndices(MassIndices massIndices) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChemicalMap forAllMasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChemicalMap forImageCoordinates(ImageCoordinates imageCoordinates) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChemicalMap forAllOfImage() {
		// TODO Auto-generated method stub
		return null;
	}
}
