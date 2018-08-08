package uk.co.saiman.eclipse.ui.fx.impl;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import uk.co.saiman.eclipse.ui.TransferFormat;
import uk.co.saiman.eclipse.ui.model.MCell;

public class ItemList<T> {
  private final MCell model;
  private final List<Item<T>> items;
  private final Optional<Consumer<? super List<? extends T>>> update;

  public ItemList(MCell model, T object) {
    this.model = requireNonNull(model);
    this.items = asList(new Item<>(this, object));
    this.update = Optional.empty();
  }

  public ItemList(MCell model, T object, Consumer<? super T> update) {
    this.model = requireNonNull(model);
    this.items = asList(new Item<>(this, object, update));
    this.update = Optional.empty();
  }

  public ItemList(MCell model, List<? extends T> objects) {
    this.model = requireNonNull(model);
    this.items = objects.stream().map(object -> new Item<>(this, object)).collect(toList());
    this.update = Optional.empty();
  }

  public ItemList(
      MCell model,
      List<? extends T> objects,
      Consumer<? super List<? extends T>> update) {
    this.model = requireNonNull(model);
    this.items = IntStream
        .range(0, objects.size())
        .mapToObj(i -> new Item<>(this, objects.get(i), object -> {
          List<T> newObjects = new ArrayList<>(objects);
          newObjects.set(i, object);
          update.accept(newObjects);
        }))
        .collect(toList());
    this.update = Optional.of(update);
  }

  public Stream<Item<T>> getItems() {
    return items.stream();
  }

  public Optional<Consumer<? super List<? extends T>>> getUpdate() {
    return update;
  }

  public MCell getModel() {
    return model;
  }

  @SuppressWarnings("unchecked")
  public Stream<TransferFormat<T>> getTransferFormats() {
    return getModel().getTransferFormats().stream().map(format -> (TransferFormat<T>) format);
  }
}