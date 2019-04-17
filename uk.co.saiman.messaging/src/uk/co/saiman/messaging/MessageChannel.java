package uk.co.saiman.messaging;

import java.io.IOException;
import java.nio.ByteBuffer;

import uk.co.saiman.observable.Observable;

public interface MessageChannel extends DataChannel, MessageSender, MessageReceiver {
  static MessageChannel over(MessageSender sender, MessageReceiver receiver) {
    return new MessageChannel() {
      @Override
      public void sendMessage(ByteBuffer message) throws IOException {
        sender.sendMessage(message);
      }

      @Override
      public Observable<ByteBuffer> receiveMessages() {
        return receiver.receiveMessages();
      }

      @Override
      public DataBuffer openDataBuffer(int size) throws IOException {
        return receiver.openDataBuffer(size);
      }

      @Override
      public DataChannel packeting(int size) {
        return DataChannel.over(sender, receiver.packeting(size));
      }
    };
  }
}
