package uk.co.saiman.comms;

import java.nio.channels.ByteChannel;

/**
 * Some sort of comms interface which can be opened as a byte channel.
 * 
 * @author Elias N Vasylenko
 */
public interface CommsChannel {
	/**
	 * A description of the status of a comms channel.
	 * 
	 * @author Elias N Vasylenko
	 */
	enum Status {
		/**
		 * The byte channel is currently open. Subsequent invocations of
		 * {@link CommsChannel#openChannel()} will fail until the previously opened
		 * byte channel is closed.
		 */
		OPEN,

		/**
		 * The byte channel is ready to be opened. This does not guarantee that a
		 * subsequent invocation of {@link CommsChannel#openChannel()} will succeed,
		 * it simply indicates that it is expected to succeed. If invocation fails,
		 * the comms channel will enter the {@link #FAULT} state.
		 */
		READY,

		/**
		 * There is a problem with the comms channel. If a comms channel is in the
		 * {@link #FAULT} state, then invocation of
		 * {@link CommsChannel#openChannel()} should either clear the fault or throw
		 * an exception describing the fault.
		 */
		FAULT
	}

	/**
	 * @return a human readable name for the channel, where available
	 */
	String getDescriptiveName();

	/**
	 * @return the current status of the channel
	 */
	Status getStatus();

	/**
	 * Open a byte channel over the comms interface. The caller is responsible for
	 * closing the channel, and has exclusive access to it until this time.
	 * Successive invocations will fail until the previously returned channel is
	 * closed.
	 * 
	 * @return the opened byte channel
	 */
	ByteChannel openChannel();
}
