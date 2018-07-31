package uk.co.saiman.eclipse.treeview.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javafx.scene.input.DataFormat;
import uk.co.saiman.collection.StreamUtilities;
import uk.co.saiman.eclipse.treeview.DragHandler;
import uk.co.saiman.eclipse.treeview.DropHandler;
import uk.co.saiman.eclipse.ui.FormatConverter;
import uk.co.saiman.eclipse.ui.ListItemConfiguration;
import uk.co.saiman.eclipse.ui.TransferIn;
import uk.co.saiman.eclipse.ui.TransferMode;
import uk.co.saiman.eclipse.ui.TransferOut;

public class ListItemConfigurationImpl<T> implements ListItemConfiguration<T> {
  private List<T> objects;
  private FormatConverter<T> formatConverter;
  private List<DragHandler<T>> dragHandlers = emptyList();
  private List<DropHandler<T>> dropHandlers = emptyList();

  public Stream<Item<T>> items() {
    return null; // TODO
  }

  public ItemGroup<T> itemGroup() {
    return null; // TODO
  }

  @Override
  public ListItemConfiguration<T> setObjects(List<? extends T> objects) {
    this.objects = new ArrayList<>(objects);
    return this;
  }

  @Override
  public ListItemConfiguration<T> setUpdateFunction(Consumer<? super T> update) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ListItemConfiguration<T> setUpdateFunction(BiConsumer<? super Integer, ? super T> update) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ListItemConfiguration<T> setAnonymousContribution(Object anonymous) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ListItemConfiguration<T> setDataFormat(
      DataFormat format,
      Function<T, String> encode,
      Function<String, T> decode) {
    this.formatConverter = new FormatConverterImpl<>(format, encode, decode);
    return this;
  }

  @Override
  public ListItemConfiguration<T> setDragHandlers(
      Predicate<? super TransferOut<? extends T>> checkDrag,
      Consumer<? super TransferOut<? extends T>> handleDrag,
      TransferMode... transferModes) {
    DragHandler<T> dragHandler = new DragHandler<>(asList(transferModes), checkDrag, handleDrag);

    dragHandlers = dragHandlers
        .stream()
        .map(h -> removeTransferModes(h, asList(transferModes)))
        .flatMap(StreamUtilities::streamOptional)
        .collect(toList());

    for (TransferMode transferMode : transferModes) {
      dragHandlers.add(dragHandler);
    }
    return this;
  }

  private Optional<DragHandler<T>> removeTransferModes(
      DragHandler<T> dragHandler,
      Collection<? extends TransferMode> transferModes) {
    return Optional
        .of(
            new DragHandler<>(
                dragHandler.getTransferModes().filter(transferModes::contains).collect(toList()),
                dragHandler::checkDrag,
                dragHandler::handleDrag))
        .filter(h -> h.getTransferModes().findAny().isPresent());
  }

  @Override
  public ListItemConfiguration<T> setDropHandlers(
      Predicate<? super TransferIn<? extends T>> checkDrop,
      Consumer<? super TransferIn<? extends T>> handleDrop,
      TransferMode... transferModes) {
    DropHandler<T> dropHandler = new DropHandler<>(asList(transferModes), checkDrop, handleDrop);

    dropHandlers = dropHandlers
        .stream()
        .map(h -> removeTransferModes(h, asList(transferModes)))
        .flatMap(StreamUtilities::streamOptional)
        .collect(toList());

    for (TransferMode transferMode : transferModes) {
      dropHandlers.add(dropHandler);
    }
    return this;
  }

  private Optional<DropHandler<T>> removeTransferModes(
      DropHandler<T> dropHandler,
      Collection<? extends TransferMode> transferModes) {
    return Optional
        .of(
            new DropHandler<>(
                dropHandler.getTransferModes().filter(transferModes::contains).collect(toList()),
                dropHandler::checkDrop,
                dropHandler::handleDrop))
        .filter(h -> h.getTransferModes().findAny().isPresent());
  }
}
