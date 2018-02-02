package uk.co.saiman.eclipse.treeview;

import static java.util.function.Function.identity;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.scene.input.DataFormat;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.format.TextFormat;

/**
 * An interface for defining clipboard transfer behaviour for e.g.
 * cut/copy/paste or drag-and-drop.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 */
public interface TreeClipboard<T> {
  void addDataFormat(DataFormat format, Function<T, String> encode, Function<String, T> decode);

  default <U> void addDataFormat(
      TextFormat<U> format,
      Function<T, U> encode,
      Function<U, T> decode) {
    addDataFormat(
        DataFormat.PLAIN_TEXT, // TODO lookupMimeType(format.getMimeType())
        data -> format.encodeString(new Payload<>(encode.apply(data))),
        string -> decode.apply(format.decodeString(string).data));
  }

  default void addDataFormat(TextFormat<T> format) {
    addDataFormat(format, identity(), identity());
  }

  /**
   * If multiple drag handlers are added, higher priority is given to those added
   * later. If a drag handler is not applicable to a transfer, handling falls back
   * to the handler with the next highest priority.
   * 
   * @param checkDrag
   * @param handleDrag
   * @param transferMode
   */
  void addDragHandler(
      Predicate<? super TreeDragCandidate<? extends T>> checkDrag,
      Consumer<? super TreeDragCandidate<? extends T>> handleDrag,
      TreeTransferMode... transferMode);

  default void addDragHandler(
      Consumer<? super TreeDragCandidate<? extends T>> handleDrag,
      TreeTransferMode... transferMode) {
    addDragHandler(c -> true, handleDrag, transferMode);
  }

  void addDropHandler(
      Predicate<? super TreeDropCandidate<? extends T>> checkDrop,
      Consumer<? super TreeDropCandidate<? extends T>> handleDrop,
      TreeTransferMode... transferMode);

  default void addDropHandler(
      Consumer<? super TreeDropCandidate<? extends T>> handleDrop,
      TreeTransferMode... transferMode) {
    addDropHandler(c -> true, handleDrop, transferMode);
  }
}
