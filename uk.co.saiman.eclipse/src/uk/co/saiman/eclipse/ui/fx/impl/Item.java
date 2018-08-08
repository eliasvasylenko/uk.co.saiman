package uk.co.saiman.eclipse.ui.fx.impl;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import uk.co.saiman.eclipse.ui.TransferFormat;
import uk.co.saiman.eclipse.ui.model.MCell;

public class Item<T> {
  private final ItemList<T> owner;
  private final T object;
  private final Optional<Consumer<? super T>> update;

  public Item(ItemList<T> owner, T object, Consumer<? super T> update) {
    this.owner = requireNonNull(owner);
    this.object = object;
    this.update = Optional.of(update);
  }

  public Item(ItemList<T> owner, T object) {
    this.owner = requireNonNull(owner);
    this.object = object;
    this.update = Optional.empty();
  }

  public T getObject() {
    return object;
  }

  public Optional<Consumer<? super T>> getUpdate() {
    return update;
  }

  public Optional<Runnable> getRemove() {
    return getList()
        .getUpdate()
        .map(
            u -> () -> u
                .accept(
                    getList()
                        .getItems()
                        .filter(i -> i != this)
                        .map(Item::getObject)
                        .collect(toList())));
  }

  public MCell getModel() {
    return getList().getModel();
  }

  public ItemList<T> getList() {
    return owner;
  }

  public Stream<TransferFormat<T>> getTransferFormats() {
    return getList().getTransferFormats();
  }
}
