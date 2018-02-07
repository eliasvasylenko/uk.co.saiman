package uk.co.saiman.comms.impl;

import uk.co.saiman.comms.CommsChannel;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.CommsStream;
import uk.co.saiman.comms.serial.SerialPort;

public class InvalidSerialPort implements SerialPort {
  private final String name;

  public InvalidSerialPort(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean isOpen() {
    return false;
  }

  @Override
  public void close() {}

  @Override
  public CommsChannel openChannel() {
    throw getCannotOpenException();
  }

  @Override
  public CommsStream openStream(int packetSize) {
    throw getCannotOpenException();
  }

  private CommsException getCannotOpenException() {
    return new CommsException("Cannot open unrecognised simulation port " + name);
  }
}
