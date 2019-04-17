package uk.co.saiman.messaging;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import uk.co.saiman.observable.Observable;

/**
 * A receiver for a stream of data. Bytes are not necessarily received in the
 * same unit-blocks in which they were sent, but they are guaranteed to be in
 * order.
 * 
 * @author Elias N Vasylenko
 */
public interface DataReceiver {
  Observable<ByteBuffer> receiveData();

  default DataBuffer openDataBuffer(int size) throws IOException {
    return new DataReceiverBuffer(this, size);
  }

  default DataReceiver packeting(int size) {
    return new PackatingDataReceiver(this, size);
  }

  default ReadableByteChannel openByteChannel(int bufferSize) throws IOException {
    return DataByteChannel.over(this, bufferSize);
  }
}
