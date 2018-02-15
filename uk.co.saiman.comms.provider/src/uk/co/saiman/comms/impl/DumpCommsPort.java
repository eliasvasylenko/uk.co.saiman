package uk.co.saiman.comms.impl;

import java.io.IOException;
import java.nio.ByteBuffer;

import uk.co.saiman.comms.CommsChannel;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.CommsPort;
import uk.co.saiman.comms.CommsStream;
import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.Observer;

public class DumpCommsPort implements CommsPort {
  private final String name;
  private boolean open;

  public DumpCommsPort(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean isOpen() {
    return open;
  }

  @Override
  public void kill() {
    open = false;
  }

  @Override
  public CommsChannel openChannel() {
    if (isOpen()) {
      throw new CommsException("Port already in use " + this);
    }

    return new CommsChannel() {
      private boolean disposed;

      private void checkState() {
        if (disposed)
          throw new CommsException("Port is disposed");
      }

      @Override
      public Disposable observe(Observer<? super CommsChannel> observer) {
        checkState();
        return () -> {};
      }

      @Override
      public int write(ByteBuffer src) throws IOException {
        checkState();
        return src.remaining();
      }

      @Override
      public boolean isOpen() {
        return !disposed;
      }

      @Override
      public int read(ByteBuffer dst) throws IOException {
        checkState();
        return 0;
      }

      @Override
      public void close() {
        disposed = true;
        open = false;
      }

      @Override
      public int bytesAvailable() {
        return 0;
      }
    };
  }

  @Override
  public CommsStream openStream(int packetSize) {
    CommsChannel channel = openChannel();
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
      public Disposable observe(Observer<? super ByteBuffer> observer) {
        return channel.map(o -> (ByteBuffer) null).observe(observer);
      }

      @Override
      public void close() throws IOException {
        channel.close();
      }
    };
  }
}
