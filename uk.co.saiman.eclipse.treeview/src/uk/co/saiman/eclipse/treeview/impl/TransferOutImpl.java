package uk.co.saiman.eclipse.treeview.impl;

import static uk.co.saiman.collection.StreamUtilities.entriesToMap;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Set;

import javafx.scene.input.ClipboardContent;
import uk.co.saiman.eclipse.treeview.DragHandler;
import uk.co.saiman.eclipse.ui.TransferMode;
import uk.co.saiman.eclipse.ui.TransferOut;

class TransferOutImpl<T> implements TransferOut<T> {
  private final Item<T> item;
  private final ClipboardContent clipboardContent;
  private final Map<TransferMode, DragHandler<? super T>> handlers;

  public TransferOutImpl(Item<T> item) {
    this.item = item;

    // populate clipboard
    this.clipboardContent = new ClipboardContent();
    item.group().formatConverters().forEach(e -> {
      clipboardContent.put(e.format(), e.encode(item.object()));
    });

    // find highest priority applicable handlers for each transfer mode
    this.handlers = item
        .group()
        .dragHandlers()
        .filter(d -> d.checkDrag(this))
        .flatMap(d -> d.getTransferModes().map(m -> new SimpleEntry<>(m, d)))
        .collect(entriesToMap());
  }

  @Override
  public T data() {
    return item.object();
  }

  public Set<TransferMode> getTransferModes() {
    return handlers.keySet();
  }

  public boolean isValid() {
    return !handlers.isEmpty();
  }

  public void handleDrag(TransferMode transferMode) {
    handlers.get(transferMode).handleDrag(this);
  }

  public ClipboardContent getClipboardContent() {
    if (!isValid())
      throw new NullPointerException();
    return clipboardContent;
  }
}