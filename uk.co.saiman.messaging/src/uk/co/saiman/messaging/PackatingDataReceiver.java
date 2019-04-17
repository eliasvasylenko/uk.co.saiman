package uk.co.saiman.messaging;

import java.nio.ByteBuffer;

import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

class PackatingDataReceiver implements DataReceiver {
  private ByteBuffer packet;
  private final HotObservable<ByteBuffer> downstream;

  public PackatingDataReceiver(DataReceiver receiver, int size) {
    packet = ByteBuffer.allocate(size);
    downstream = new HotObservable<>() {
      private Disposable observation;

      protected void open() throws Exception {
        observation = receiver
            .receiveData()
            .weakReference(PackatingDataReceiver.this)
            .observe(o -> o.owner().send(o.message()));
      }

      protected void close() throws Exception {
        observation.cancel();
      }
    };
  }

  public synchronized void send(ByteBuffer buffer) {
    while (buffer.remaining() >= packet.remaining()) {
      byte[] bytes = new byte[packet.remaining()];
      buffer.get(bytes);
      packet.put(bytes);
      sendDownstream();
    }
    packet.put(buffer);
  }

  private synchronized void sendDownstream() {
    packet.flip();
    downstream.next(packet);
    packet = ByteBuffer.allocate(packet.capacity());
  }

  @Override
  public Observable<ByteBuffer> receiveData() {
    return downstream;
  }
}
