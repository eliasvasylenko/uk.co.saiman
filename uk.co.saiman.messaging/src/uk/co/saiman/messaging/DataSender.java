package uk.co.saiman.messaging;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public interface DataSender {
  int sendData(ByteBuffer message) throws IOException;

  default WritableByteChannel asByteChannel() {
    return DataByteChannel.over(this);
  }
}
