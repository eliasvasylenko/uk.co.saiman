package uk.co.saiman.messaging;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

/**
 * A receiver for a stream of data. Bytes are not necessarily received in the
 * same unit-blocks in which they were sent, but they are guaranteed to be in
 * order.
 * 
 * @author Elias N Vasylenko
 */
public interface MessageBuffer extends AutoCloseable {
  boolean isOpen();

  @Override
  void close() throws IOException;

  void flush();

  int availableMessages();

  ByteBuffer readMessage() throws IOException;

  ByteBuffer readMessage(TimeUnit timeUnit, long timeout) throws IOException;
}
