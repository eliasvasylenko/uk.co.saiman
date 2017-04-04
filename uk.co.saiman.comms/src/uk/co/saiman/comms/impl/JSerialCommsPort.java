package uk.co.saiman.comms.impl;

import static com.fazecast.jSerialComm.SerialPort.NO_PARITY;
import static com.fazecast.jSerialComm.SerialPort.ONE_STOP_BIT;
import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_READ_BLOCKING;
import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_WRITE_BLOCKING;
import static uk.co.saiman.comms.Comms.CommsStatus.FAULT;
import static uk.co.saiman.comms.Comms.CommsStatus.OPEN;
import static uk.co.saiman.comms.Comms.CommsStatus.READY;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;

import uk.co.saiman.comms.CommsChannel;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.CommsStream;
import uk.co.strangeskies.utilities.ObservableImpl;
import uk.co.strangeskies.utilities.ObservableProperty;
import uk.co.strangeskies.utilities.ObservableValue;
import uk.co.strangeskies.utilities.Observer;

/**
 * A {@link uk.co.saiman.comms.serial.SerialPort serial port} implementation
 * based on the JSerialComms library.
 * 
 * @author Elias N Vasylenko
 */
public class JSerialCommsPort implements uk.co.saiman.comms.serial.SerialPort {
	private final SerialPort serialPort;

	private CommsChannel openChannel;

	private final ObservableProperty<CommsStatus, CommsStatus> status;
	private CommsException lastFault;

	protected JSerialCommsPort(SerialPort serialPort) {
		this.serialPort = serialPort;
		status = ObservableProperty.over(READY);

		if (isPortValid(false)) {
			serialPort.setNumDataBits(Byte.SIZE);
			serialPort.setNumStopBits(ONE_STOP_BIT);
			serialPort.setParity(NO_PARITY);
			serialPort.setComPortTimeouts(TIMEOUT_READ_BLOCKING | TIMEOUT_WRITE_BLOCKING, 1000, 1000);
		}
	}

	@Override
	public synchronized void reset() {
		try {
			closeChannel();
		} catch (Exception e) {}
		status.set(READY);
		isPortValid(false);
	}

	@Override
	public synchronized ObservableValue<CommsStatus> status() {
		return status;
	}

	@Override
	public synchronized Optional<CommsException> getFault() {
		return status.get() == FAULT ? Optional.of(lastFault) : Optional.empty();
	}

	@Override
	public synchronized CommsException setFault(CommsException commsException) {
		status.set(FAULT);
		this.lastFault = commsException;
		return commsException;
	}

	private boolean isPortValid(boolean throwing) {
		if (serialPort.getSystemPortName().equals("/dev/null")) {
			CommsException fault = setFault(
					new CommsException(
							"Port is not valid " + getSystemName() + " - " + getDescriptiveName()));
			if (throwing) {
				throw fault;
			}
			return false;
		} else {
			return true;
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
	public synchronized CommsChannel openChannel() {
		isPortValid(true);

		if (openChannel != null) {
			throw new CommsException(
					"Port already in use " + getSystemName() + " - " + getDescriptiveName());
		}

		closeChannel();

		if (!serialPort.openPort()) {
			throw setFault(
					new CommsException("Cannot open port " + getSystemName() + " - " + getDescriptiveName()));
		}

		ObservableProperty<Integer, Integer> availableObservable = ObservableProperty
				.over((a, c) -> a, Objects::equals, serialPort.bytesAvailable());

		setPortListener(new SerialPortDataListener() {
			@Override
			public void serialEvent(SerialPortEvent event) {
				availableObservable.set(serialPort.bytesAvailable());
			}

			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
			}
		});

		openChannel = new CommsChannel() {
			private boolean open = true;

			@Override
			public ObservableValue<Integer> availableBytes() {
				assertOpen();
				return availableObservable;
			}

			@Override
			public int write(ByteBuffer src) throws IOException {
				assertOpen();
				try {
					byte[] bytes = new byte[src.remaining()];
					src.get(bytes);
					return serialPort.writeBytes(bytes, bytes.length);
				} catch (Exception e) {
					setFault(new CommsException("Problem writing to comms channel", e));
					throw e;
				}
			}

			@Override
			public int read(ByteBuffer dst) throws IOException {
				assertOpen();
				try {
					byte[] bytes = new byte[dst.remaining()];
					int read = serialPort.readBytes(bytes, bytes.length);
					dst.put(bytes, 0, read);
					return read;
				} catch (Exception e) {
					setFault(new CommsException("Problem reading from comms channel", e));
					throw e;
				}
			}

			private void assertOpen() {
				if (!isOpen())
					throw new CommsException("Channel is closed");
			}

			@Override
			public boolean isOpen() {
				return open;
			}

			@Override
			public void close() {
				if (open) {
					open = false;
					closeChannel();
				}
			}
		};

		status.set(OPEN);

		return openChannel;
	}

	@Override
	public synchronized CommsStream openStream(int packetSize) {
		CommsChannel channel = openChannel();
		ObservableImpl<ByteBuffer> byteObservable = new ObservableImpl<>();

		if (packetSize <= 0) {
			channel.availableBytes().addObserver(b -> {
				byte[] bytes = new byte[serialPort.bytesAvailable()];
				serialPort.readBytes(bytes, bytes.length);
				byteObservable.fire(ByteBuffer.wrap(bytes));
			});
		} else {
			setPortListener(new SerialPortPacketListener() {
				@Override
				public void serialEvent(SerialPortEvent event) {
					byteObservable.fire(ByteBuffer.wrap(event.getReceivedData()));
				}

				@Override
				public int getListeningEvents() {
					return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
				}

				@Override
				public int getPacketSize() {
					return packetSize;
				}
			});
		}

		return new CommsStream() {
			@Override
			public boolean isOpen() {
				return channel.isOpen();
			}

			@Override
			public int write(ByteBuffer src) throws IOException {
				return channel.write(src);
			}

			@Override
			public boolean removeObserver(Observer<? super ByteBuffer> observer) {
				return byteObservable.removeObserver(observer);
			}

			@Override
			public boolean addObserver(Observer<? super ByteBuffer> observer) {
				return byteObservable.addObserver(observer);
			}

			@Override
			public void close() throws IOException {
				serialPort.removeDataListener();
				channel.close();
			}
		};
	}

	private synchronized void closeChannel() {
		openChannel = null;
		if (!serialPort.closePort()) {
			throw setFault(
					new CommsException(
							"Cannot close port " + getSystemName() + " - " + getDescriptiveName()));
		}
		status.set(READY);
	}

	private void setPortListener(SerialPortDataListener listener) {
		serialPort.removeDataListener();
		if (!serialPort.addDataListener(listener)) {
			closeChannel();
			throw setFault(
					new CommsException(
							"Cannot add listener to port " + getSystemName() + " - " + getDescriptiveName()));
		}
	}
}
