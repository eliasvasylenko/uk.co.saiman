package uk.co.saiman.messaging;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface MessageSender extends DataSender {
  @Override
  default int sendData(ByteBuffer message) throws IOException {
    sendMessage(message);
    return 0;
  }

  void sendMessage(ByteBuffer message) throws IOException;
}
