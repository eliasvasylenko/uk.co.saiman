package uk.co.saiman.instrument;

import java.util.function.Consumer;

public interface HardwareModule {
	String getName();

	/*
	 * Is a communication link with the hardware properly established.
	 */
	boolean isConnected();

	void reset();

	/*
	 * If the hardware module is performing some sort of operation, e.g. a
	 * currently running raster or acquisition card, that operation should be
	 * aborted, or for high voltages they should be turned off if possible.
	 */
	void abortOperation();

	void addErrorListener(Consumer<Exception> exception);
}
