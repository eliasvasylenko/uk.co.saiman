package uk.co.saiman.comms.serial.impl;

import static com.fazecast.jSerialComm.SerialPort.NO_PARITY;
import static com.fazecast.jSerialComm.SerialPort.ONE_STOP_BIT;
import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_READ_BLOCKING;
import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_WRITE_BLOCKING;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import com.fazecast.jSerialComm.SerialPort;

import uk.co.saiman.comms.CommsException;

/**
 * A {@link uk.co.saiman.comms.serial.SerialPort serial port} implementation
 * based on the JSerialComms library.
 * 
 * @author Elias N Vasylenko
 */
public class JSerialCommsPort implements uk.co.saiman.comms.serial.SerialPort {
	private final SerialPort serialPort;

	private CommsException currentFault;
	private ByteChannel openChannel;

	protected JSerialCommsPort(SerialPort serialPort) {
		this.serialPort = serialPort;

		if (isPortValid()) {
			serialPort.setNumDataBits(Byte.SIZE);
			serialPort.setNumStopBits(ONE_STOP_BIT);
			serialPort.setParity(NO_PARITY);
			serialPort.setComPortTimeouts(TIMEOUT_READ_BLOCKING | TIMEOUT_WRITE_BLOCKING, 1000, 1000);
		}
	}

	private boolean isPortValid() {
		if (serialPort.getSystemPortName().equals("/dev/null")) {
			currentFault = new CommsException("Port is not valid " + getSystemName() + " - " + getDescriptiveName());
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Status getStatus() {
		if (openChannel != null) {
			return Status.OPEN;
		} else if (currentFault != null) {
			return Status.FAULT;
		} else {
			return Status.READY;
		}
	}

	@Override
	public String getSystemName() {
		return serialPort.getSystemPortName();
	}

	@Override
	public String getDescriptiveName() {
		return serialPort.getDescriptivePortName() + " " + serialPort.getSystemPortName();
	}

	@Override
	public synchronized int bytesAvailable() {
		return serialPort.bytesAvailable();
	}

	@Override
	public synchronized ByteChannel openChannel() {
		if (!isPortValid()) {
			throw currentFault;
		}

		if (openChannel != null) {
			throw new CommsException("Port already in use " + getSystemName() + " - " + getDescriptiveName());
		}

		closeChannel();

		if (!serialPort.openPort()) {
			setFault(new CommsException("Cannot open port " + getSystemName() + " - " + getDescriptiveName()));
		}

		openChannel = new ByteChannel() {
			private boolean open = true;

			@Override
			public int write(ByteBuffer src) throws IOException {
				byte[] bytes = new byte[src.remaining()];
				src.get(bytes);
				return serialPort.writeBytes(bytes, bytes.length);
			}

			@Override
			public int read(ByteBuffer dst) throws IOException {
				byte[] bytes = new byte[dst.remaining()];
				int read = serialPort.readBytes(bytes, bytes.length);
				dst.put(bytes, 0, read);
				return read;
			}

			@Override
			public boolean isOpen() {
				return open;
			}

			@Override
			public void close() throws IOException {
				if (open) {
					open = false;
					closeChannel();
				}
			}
		};

		currentFault = null;

		return openChannel;
	}

	private synchronized void closeChannel() {
		openChannel = null;
		if (!serialPort.closePort()) {
			setFault(new CommsException("Cannot close port " + getSystemName() + " - " + getDescriptiveName()));
		}
	}

	private void setFault(CommsException commsException) {
		this.currentFault = commsException;
		throw commsException;
	}
}
