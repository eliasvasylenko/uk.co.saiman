package uk.co.saiman.comms.serial;

import java.util.stream.Stream;

/**
 * Service providing access to serial ports.
 * 
 * @author Elias N Vasylenko
 */
public interface SerialComms {
	/**
	 * Get all known serial ports available on the system.
	 * 
	 * @return a stream of available serial ports
	 */
	Stream<SerialPort> getPorts();

	/**
	 * Get the serial port with the given system name.
	 * 
	 * @param port
	 *          the system name of the port
	 * @return the serial port of the given name
	 */
	SerialPort getPort(String port);
}
