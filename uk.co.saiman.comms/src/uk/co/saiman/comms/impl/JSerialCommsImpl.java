package uk.co.saiman.comms.impl;

import static com.fazecast.jSerialComm.SerialPort.getCommPort;
import static com.fazecast.jSerialComm.SerialPort.getCommPorts;
import static java.util.Arrays.stream;

import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.comms.serial.SerialPorts;
import uk.co.saiman.comms.serial.SerialPort;

@Component
public class JSerialCommsImpl implements SerialPorts {
	@Override
	public Stream<SerialPort> getPorts() {
		return stream(getCommPorts()).map(JSerialCommsPort::new);
	}

	@Override
	public SerialPort getPort(String port) {
		return new JSerialCommsPort(getCommPort(port));
	}
}
