package uk.co.saiman.comms.serial;

import uk.co.saiman.comms.CommsChannel;

public interface SerialPort extends CommsChannel {
	String getSystemName();

	int bytesAvailable();
}
