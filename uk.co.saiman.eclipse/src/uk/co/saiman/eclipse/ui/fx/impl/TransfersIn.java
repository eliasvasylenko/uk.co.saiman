package uk.co.saiman.eclipse.ui.fx.impl;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javafx.scene.input.Dragboard;
import uk.co.saiman.eclipse.ui.TransferDestination;
import uk.co.saiman.eclipse.ui.TransferFormat;
import uk.co.saiman.eclipse.ui.TransferMode;
import uk.co.saiman.eclipse.ui.fx.ClipboardCache;
import uk.co.saiman.eclipse.ui.fx.ClipboardService;
import uk.co.saiman.eclipse.ui.fx.TransferModes;

class TransfersIn {
  private final TransferDestination position;
  private final Item<?> adjacentItem;
  private final Map<TransferMode, Optional<? extends TransferIn<?>>> dropCandidates;

  public TransfersIn(
      List<ItemList<?>> itemLists,
      Dragboard clipboard,
      ClipboardService clipboardService,
      TransferDestination position,
      Item<?> adjacentItem) {
    this.position = position;
    this.adjacentItem = adjacentItem;

    this.dropCandidates = clipboard
        .getTransferModes()
        .stream()
        .map(TransferModes::fromJavaFXTransferMode)
        .collect(
            toMap(
                identity(),
                transferMode -> forItemLists(
                    itemLists,
                    clipboardService.getCache(clipboard),
                    transferMode)));
  }

  private Optional<? extends TransferIn<?>> forItemLists(
      List<ItemList<?>> itemLists,
      ClipboardCache clipboard,
      TransferMode transferMode) {
    return itemLists
        .stream()
        .map(itemList -> forItemList(itemList, clipboard, transferMode))
        .findFirst()
        .flatMap(t -> t);
  }

  private <T> Optional<TransferIn<T>> forItemList(
      ItemList<T> itemList,
      ClipboardCache clipboard,
      TransferMode transferMode) {
    return itemList
        .getTransferFormats()
        .filter(transferFormat -> transferFormat.transferModes().contains(transferMode))
        .map(TransferFormat::dataFormat)
        .map(clipboard::getData)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(object -> new TransferIn<>(itemList, object, this))
        .findFirst();
  }

  public TransferDestination position() {
    return position;
  }

  public Item<?> adjacentItem() {
    return adjacentItem;
  }

  Set<TransferMode> getCandidateTransferModes() {
    return dropCandidates
        .keySet()
        .stream()
        .filter(mode -> dropCandidates.get(mode).isPresent())
        .collect(toSet());
  }

  TransferIn<?> getCandidate(TransferMode transferMode) {
    return dropCandidates.get(transferMode).get();
  }
}
