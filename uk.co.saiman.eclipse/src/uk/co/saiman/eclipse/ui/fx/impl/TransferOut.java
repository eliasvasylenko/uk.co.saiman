package uk.co.saiman.eclipse.ui.fx.impl;

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.Set;
import java.util.stream.Stream;

import javafx.scene.input.ClipboardContent;
import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.format.TextFormat;
import uk.co.saiman.eclipse.ui.TransferMode;
import uk.co.saiman.eclipse.ui.fx.MediaTypes;

class TransferOut<T> {
  private final Item<T> item;

  private final Set<TransferMode> transferModes;
  private final ClipboardContent clipboardContent;

  public TransferOut(Item<T> item) {
    this.item = item;

    // determine available transfer modes
    this.transferModes = unmodifiableSet(
        Stream
            .of(TransferMode.values())
            .filter(mode -> item.getRemove().isPresent() || !mode.isDestructive())
            .collect(toSet()));

    // populate clipboard
    this.clipboardContent = new ClipboardContent();

    item.getTransferFormats().forEach(transferFormat -> {
      DataFormat<T> dataFormat = (DataFormat<T>) transferFormat.dataFormat();
      Object data;
      Payload<T> payload = new Payload<>(item.getObject());
      if (dataFormat instanceof TextFormat<?>) {
        data = ((TextFormat<T>) dataFormat).encodeString(payload);

      } else {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
          dataFormat.save(Channels.newChannel(bytes), payload);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        data = bytes.toByteArray();
      }
      dataFormat
          .getMediaTypes()
          .map(MediaTypes::toDataFormat)
          .distinct()
          .forEach(format -> clipboardContent.put(format, data));
    });
  }

  public T getData() {
    return item.getObject();
  }

  public Set<TransferMode> getTransferModes() {
    return transferModes;
  }

  public void handleDrag(TransferMode transferMode) {
    if (!transferModes.contains(transferMode)) {
      throw new IllegalArgumentException("Unsupported transfer mode " + transferMode);
    }
    if (transferMode.isDestructive()) {
      item.getRemove().get().run();
    }
  }

  public ClipboardContent getClipboardContent() {
    return clipboardContent;
  }
}
