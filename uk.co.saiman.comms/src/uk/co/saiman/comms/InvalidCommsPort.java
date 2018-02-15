package uk.co.saiman.comms;

import uk.co.saiman.comms.CommsChannel;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.CommsPort;
import uk.co.saiman.comms.CommsStream;

public class InvalidCommsPort implements CommsPort {
  private final String name;

  public InvalidCommsPort(String name) {
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
  public void kill() {}

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
