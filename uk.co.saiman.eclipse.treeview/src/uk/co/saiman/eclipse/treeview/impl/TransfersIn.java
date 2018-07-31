package uk.co.saiman.eclipse.treeview.impl;

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.collection.StreamUtilities.entriesToMap;
import static uk.co.saiman.collection.StreamUtilities.reverse;
import static uk.co.saiman.collection.StreamUtilities.streamOptional;
import static uk.co.saiman.collection.StreamUtilities.tryOptional;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.scene.input.Clipboard;
import uk.co.saiman.eclipse.treeview.DropHandler;
import uk.co.saiman.eclipse.ui.TransferInPosition;
import uk.co.saiman.eclipse.ui.TransferMode;

class TransfersIn {
  private final TransferInPosition position;
  private final Object adjacentItem;
  private final Map<TransferMode, TransferInImpl<?>> dropCandidates;

  public TransfersIn(
      List<ItemGroup<?>> configurations,
      Clipboard clipboard,
      TransferInPosition position,
      Object adjacentItem) {
    this.position = position;
    this.adjacentItem = adjacentItem;

    reverse(configurations.stream())
        .flatMap(c -> c.dropHandlers())
        .flatMap(d -> d.getTransferModes().map(m -> new SimpleEntry<>(m, d)))
        .collect(entriesToMap());

    Map<TransferMode, TransferInImpl<?>> modes = new HashMap<>();
    for (ItemGroup<?> configuration : configurations) {
      loadConfiguration(configuration, clipboard, modes);
    }

    this.dropCandidates = modes;
  }

  private <T> void loadConfiguration(
      ItemGroup<T> configuration,
      Clipboard clipboard,
      Map<TransferMode, TransferInImpl<?>> modes) {

    List<DropHandler<T>> handlers = configuration
        .dropHandlers()
        .filter(handler -> handler.getTransferModes().allMatch(modes::containsKey))
        .collect(toList());

    if (handlers.isEmpty()) {
      return;
    }

    T data = configuration
        .formatConverters()
        .filter(format -> clipboard.hasContent(format.format()))
        .flatMap(
            format -> streamOptional(
                tryOptional(() -> format.decode((String) clipboard.getContent(format.format())))))
        .findFirst()
        .orElse(null);

    if (data == null) {
      return;
    }

    for (DropHandler<T> handler : handlers) {
      if (!handler.getTransferModes().allMatch(modes::containsKey)) {
        TransferInImpl<T> transfer = new TransferInImpl<>(handler, data, this);
        handler.checkDrop(transfer);
        handler.getTransferModes().forEach(mode -> modes.putIfAbsent(mode, transfer));
      }
    }
  }

  public TransferInPosition position() {
    return position;
  }

  public Object adjacentItem() {
    return adjacentItem;
  }

  Set<TransferMode> getCandidateTransferModes() {
    return unmodifiableSet(dropCandidates.keySet());
  }

  TransferInImpl<?> getCandidate(TransferMode transferMode) {
    return dropCandidates.get(transferMode);
  }
}
