package uk.co.saiman.eclipse.treeview.impl;

import java.util.stream.Stream;

import uk.co.saiman.eclipse.treeview.DropHandler;
import uk.co.saiman.eclipse.ui.TransferIn;
import uk.co.saiman.eclipse.ui.TransferInPosition;

class TransferInImpl<T> implements TransferIn<T> {
  private final DropHandler<T> handler;
  private final T data;
  private final TransfersIn owner;

  public TransferInImpl(DropHandler<T> handler, T data, TransfersIn owner) {
    this.handler = handler;
    this.data = data;
    this.owner = owner;
  }

  @Override
  public Stream<T> data() {
    return Stream.of(data);
  }

  @Override
  public TransferInPosition position() {
    return owner.position();
  }

  @SuppressWarnings("unchecked")
  @Override
  public T adjacentItem() {
    return (T) owner.adjacentItem();
  }

  public void handleDrop() {
    handler.handleDrop(this);
  }
}