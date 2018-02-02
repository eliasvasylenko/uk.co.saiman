package uk.co.saiman.eclipse.treeview;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.scene.input.DataFormat;
import uk.co.saiman.reflection.token.TypeToken;

class TreeClipboardImpl<T> implements TreeClipboard<T> {
  static class DragHandler<T> {
    private final TreeClipboardImpl<T> source;
    private final Predicate<? super TreeDragCandidate<? extends T>> checkDrag;
    private final Consumer<? super TreeDragCandidate<? extends T>> handleDrag;
    private final Set<TreeTransferMode> transferModes;

    public DragHandler(
        TreeClipboardImpl<T> source,
        Predicate<? super TreeDragCandidate<? extends T>> checkDrag,
        Consumer<? super TreeDragCandidate<? extends T>> handleDrag,
        TreeTransferMode[] transferMode) {
      this.source = source;
      this.checkDrag = checkDrag;
      this.handleDrag = handleDrag;
      this.transferModes = unmodifiableSet(new HashSet<>(asList(transferMode)));
    }

    public TypeToken<T> getType() {
      return source.type;
    }

    public Set<TreeTransferMode> getTransferModes() {
      return transferModes;
    }

    public Map<DataFormat, FormatConverter<T>> getConverters() {
      return unmodifiableMap(source.converters);
    }

    public boolean checkDrag(TreeDragCandidate<? extends T> candidate) {
      return checkDrag.test(candidate);
    }

    public void handleDrag(TreeDragCandidate<? extends T> candidate) {
      handleDrag.accept(candidate);
    }
  }

  static class DropHandler<T> {
    private final TreeClipboardImpl<T> source;
    private final Predicate<? super TreeDropCandidate<? extends T>> checkDrop;
    private final Consumer<? super TreeDropCandidate<? extends T>> handleDrop;
    private final Set<TreeTransferMode> transferModes;

    public DropHandler(
        TreeClipboardImpl<T> source,
        Predicate<? super TreeDropCandidate<? extends T>> checkDrop,
        Consumer<? super TreeDropCandidate<? extends T>> handleDrop,
        TreeTransferMode[] transferMode) {
      this.source = source;
      this.checkDrop = checkDrop;
      this.handleDrop = handleDrop;
      this.transferModes = unmodifiableSet(new HashSet<>(asList(transferMode)));
    }

    public TypeToken<T> getType() {
      return source.type;
    }

    public Set<TreeTransferMode> getTransferModes() {
      return transferModes;
    }

    public Map<DataFormat, FormatConverter<T>> getConverters() {
      return unmodifiableMap(source.converters);
    }

    public boolean checkDrop(TreeDropCandidate<? extends T> candidate) {
      return checkDrop.test(candidate);
    }

    public void handleDrop(TreeDropCandidate<? extends T> candidate) {
      handleDrop.accept(candidate);
    }
  }

  static class FormatConverter<T> {
    public final Function<? super T, String> encode;
    public final Function<String, ? extends T> decode;

    public FormatConverter(
        Function<? super T, String> encode,
        Function<String, ? extends T> decode) {
      this.encode = encode;
      this.decode = decode;
    }
  }

  private final TypeToken<T> type;
  private final Consumer<? super DragHandler<T>> dragConsumer;
  private final Consumer<? super DropHandler<T>> dropConsumer;
  private final Map<DataFormat, FormatConverter<T>> converters;

  public TreeClipboardImpl(
      TypeToken<T> type,
      Consumer<? super DragHandler<T>> dragConsumer,
      Consumer<? super DropHandler<T>> dropConsumer) {
    this.type = type;
    this.dragConsumer = dragConsumer;
    this.dropConsumer = dropConsumer;
    this.converters = new LinkedHashMap<>();
  }

  @Override
  public void addDragHandler(
      Predicate<? super TreeDragCandidate<? extends T>> checkDrag,
      Consumer<? super TreeDragCandidate<? extends T>> handleDrag,
      TreeTransferMode... transferMode) {
    dragConsumer.accept(new DragHandler<>(this, checkDrag, handleDrag, transferMode));
  }

  @Override
  public void addDropHandler(
      Predicate<? super TreeDropCandidate<? extends T>> checkDrop,
      Consumer<? super TreeDropCandidate<? extends T>> handleDrop,
      TreeTransferMode... transferMode) {
    dropConsumer.accept(new DropHandler<>(this, checkDrop, handleDrop, transferMode));
  }

  @Override
  public void addDataFormat(
      DataFormat format,
      Function<T, String> encode,
      Function<String, T> decode) {
    converters.put(format, new FormatConverter<>(encode, decode));
  }
}