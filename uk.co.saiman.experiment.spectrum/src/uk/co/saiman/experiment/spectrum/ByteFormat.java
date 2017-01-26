package uk.co.saiman.experiment.spectrum;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

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
}
