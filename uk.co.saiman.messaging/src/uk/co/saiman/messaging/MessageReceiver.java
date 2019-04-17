package uk.co.saiman.messaging;

import java.io.IOException;
import java.nio.ByteBuffer;

import uk.co.saiman.observable.Observable;

/**
 * A receiver for a sequence of self-contained messages. Bytes are received in
 * the same unit-blocks in which they were sent, but they may not be guaranteed
 * to be in order.
 * 
 * @author Elias N Vasylenko
 *
 */
public interface MessageReceiver extends DataReceiver {
  @Override
  default Observable<ByteBuffer> receiveData() {
    return receiveMessages();
  }

  default MessageBuffer openMessageBuffer(int size) throws IOException {
    return new MessageReceiverBuffer(this, size);
  }

  /**
   * Receive a single cohesive message.
   */
  Observable<ByteBuffer> receiveMessages();
}
