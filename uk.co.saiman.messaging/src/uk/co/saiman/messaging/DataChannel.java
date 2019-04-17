package uk.co.saiman.messaging;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import uk.co.saiman.observable.Observable;

public interface DataChannel extends DataSender, DataReceiver {
  static DataChannel over(DataSender sender, DataReceiver receiver) {
    return new DataChannel() {
      @Override
      public int sendData(ByteBuffer message) throws IOException {
        return sender.sendData(message);
      }

      @Override
      public Observable<ByteBuffer> receiveData() {
        return receiver.receiveData();
      }

      @Override
      public DataBuffer openDataBuffer(int size) throws IOException {
        return receiver.openDataBuffer(size);
      }

      @Override
      public DataChannel packeting(int size) {
        return over(sender, receiver.packeting(size));
      }
    };
  }

  @Override
  default ByteChannel openByteChannel(int bufferSize) throws IOException {
    return DataByteChannel.over(this, bufferSize);
  }
}
