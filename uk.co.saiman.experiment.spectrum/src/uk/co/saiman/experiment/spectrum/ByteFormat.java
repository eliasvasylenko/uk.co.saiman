package uk.co.saiman.experiment.spectrum;

import static java.nio.file.Files.newByteChannel;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;

/**
 * A binary data format for loading and saving to and from a certain object
 * type.
 * 
 * @author Elias N Vasylenko
 * @param <T>
 *          the type of data
 */
public interface ByteFormat<T> {
	/**
	 * @return the default path extension for files of this format
	 */
	String getPathExtension();

	/**
	 * Load an object from a readable byte channel.
	 * 
	 * <p>
	 * The calling context should take care of opening and cleaning up resources.
	 * The channel is assumed to be already open upon invocation, and should not
	 * be closed by any implementing method.
	 * 
	 * @param inputChannel
	 *          the input byte source
	 * @return the object represented by the bytes
	 * @throws IOException
	 *           it's an IO operation after all...
	 */
	T load(ReadableByteChannel inputChannel) throws IOException;

	/**
	 * @see #load(ReadableByteChannel) for the given file path
	 */
	@SuppressWarnings("javadoc")
	default T load(Path location) throws IOException {
		try (ReadableByteChannel inputChannel = newByteChannel(location, READ)) {
			return load(inputChannel);
		}
	}

	/**
	 * @see #load(ReadableByteChannel) for the given path and file name, using the
	 *      {@link #getPathExtension() default extension}
	 */
	@SuppressWarnings("javadoc")
	default T load(Path location, String name) throws IOException {
		return load(location.resolve(name + "." + getPathExtension()));
	}

	/**
	 * Save an object to a writable byte channel.
	 * 
	 * <p>
	 * The calling context should take care of opening and cleaning up resources.
	 * The channel is assumed to be already open upon invocation, and should not
	 * be closed by any implementing method.
	 * 
	 * @param outputChannel
	 *          the output byte sink
	 * @param data
	 *          the object represented by the bytes
	 * @throws IOException
	 *           it's an IO operation after all...
	 */
	void save(WritableByteChannel outputChannel, T data) throws IOException;

	/**
	 * @see #save(WritableByteChannel, Object) for the given file path
	 */
	@SuppressWarnings("javadoc")
	default void save(Path location, T data) throws IOException {
		try (WritableByteChannel outputChannel = newByteChannel(location, WRITE, CREATE)) {
			save(outputChannel, data);
		}
	}

	/**
	 * @see #load(ReadableByteChannel) for the given path and file name, using the
	 *      {@link #getPathExtension() default extension}
	 */
	@SuppressWarnings("javadoc")
	default void save(Path location, String name, T data) throws IOException {
		save(location.resolve(name + "." + getPathExtension()), data);
	}
}
