package uk.co.saiman.eclipse.ui.fx.impl;

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.eclipse.ui.TransferDestination.BEFORE_CHILD;
import static uk.co.saiman.eclipse.ui.TransferDestination.OVER;

import java.util.List;
import java.util.stream.Stream;

class TransferIn<T> {
  private final ItemList<T> itemList;
  private final T object;
  private final TransfersIn owner;

  public TransferIn(ItemList<T> itemList, T object, TransfersIn owner) {
    this.itemList = itemList;
    this.object = object;
    this.owner = owner;
  }

  public void handleDrop() {
    List<T> items;
    if (owner.position() == OVER) {
      items = itemList.getItems().map(Item::getObject).collect(toList());
    } else {
      items = itemList.getItems().flatMap(i -> {
        if (i == owner.adjacentItem()) {
          if (owner.position() == BEFORE_CHILD) {
            return Stream.of(object, i.getObject());
          } else {
            return Stream.of(i.getObject(), object);
          }
        } else {
          return Stream.of(i.getObject());
        }
      }).collect(toList());
    }
    itemList.getUpdate().get().accept(items);
  }
}