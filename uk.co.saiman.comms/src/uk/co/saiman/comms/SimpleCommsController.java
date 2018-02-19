package uk.co.saiman.comms;

import java.nio.channels.ByteChannel;

import uk.co.saiman.function.ThrowingFunction;

public abstract class SimpleCommsController {
  private CommsPort port;
  private CommsChannel channel;
  private CommsException fault;

  protected void activate(CommsPort port) {
    this.port = port;
    setFault(new CommsException("Awaiting connection"));
    new Thread(() -> reset()).start();
  }

  protected synchronized void deactivate() {
    if (channel != null) {
      channel.close();
      channel = null;
      commsClosed();
    }
  }

  protected synchronized void reset() {
    try {
      if (fault != null) {
        fault = null;
      }
      if (channel != null && !channel.isOpen()) {
        channel = null;
        commsClosed();
      }
      if (channel == null) {
        channel = port.openChannel();
        channel.read();
        commsOpened();
      }

      checkComms();
    } catch (CommsException e) {
      throw setFault(e);
    } catch (Exception e) {
      throw setFault(new CommsException("Problem opening comms", e));
    }
  }

  protected abstract void commsOpened();

  protected abstract void commsClosed();

  protected abstract void checkComms();

  protected synchronized CommsException setFault(CommsException commsException) {
    this.fault = commsException;
    return commsException;
  }

  public CommsPort getPort() {
    return port;
  }

  protected synchronized <U> U useChannel(ThrowingFunction<ByteChannel, U, Exception> action) {
    try {
      if (fault != null)
        throw fault;

      if (channel != null && !channel.isOpen()) {
        channel = null;
        commsClosed();
      }
      if (channel == null)
        throw new CommsException("Port is closed");

      return action.apply(channel);
    } catch (CommsException e) {
      throw e;
    } catch (Exception e) {
      throw new CommsException("Problem transferring data", e);
    }
  }
}
