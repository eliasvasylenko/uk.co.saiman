package uk.co.saiman.eclipse.treeview;

import static java.util.Collections.reverse;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.collection.StreamUtilities.distinctByKey;
import static uk.co.saiman.collection.StreamUtilities.reverse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import uk.co.saiman.eclipse.treeview.TreeClipboardImpl.DragHandler;
import uk.co.saiman.eclipse.treeview.TreeClipboardImpl.DropHandler;
import uk.co.saiman.reflection.token.TypeToken;

public class TreeClipboardManager {
  static class TreeDragCandidateImpl<T> implements TreeDragCandidate<T> {
    private final TreeEntry<T> entry;
    private final ClipboardContent clipboardContent;
    private final Map<TreeTransferMode, DragHandler<? super T>> handlers;

    public TreeDragCandidateImpl(List<DragHandler<?>> handlers, TreeEntry<T> entry) {
      this.entry = entry;
      this.clipboardContent = new ClipboardContent();
      Map<TreeTransferMode, DragHandler<? super T>> applicableHanders = new HashMap<>();
      this.handlers = unmodifiableMap(applicableHanders);

      @SuppressWarnings("unchecked")
      List<DragHandler<? super T>> typedHandlers = reverse(handlers.stream())
          .filter(d -> d.getType().isAssignableFrom(entry.type()))
          .map(d -> (DragHandler<? super T>) d)
          .collect(toList());

      // find highest priority applicable handlers for each transfer mode
      for (DragHandler<? super T> handler : typedHandlers) {
        Set<TreeTransferMode> handlerTransferModes = new HashSet<>(handler.getTransferModes());
        handlerTransferModes.removeAll(applicableHanders.keySet());
        if (!handlerTransferModes.isEmpty() && handler.checkDrag(this)) {
          for (TreeTransferMode handlerTransferMode : handlerTransferModes) {
            applicableHanders.put(handlerTransferMode, handler);
          }
        }
      }

      // populate clipboard
      this.handlers
          .values()
          .stream()
          .distinct()
          .flatMap(h -> h.getConverters().entrySet().stream())
          .filter(distinctByKey(Entry::getKey))
          .forEach(e -> {
            String string = e.getValue().encode.apply(entry.data());
            clipboardContent.put(e.getKey(), string);
          });
    }

    @Override
    public TreeEntry<T> entry() {
      return entry;
    }

    public Set<TreeTransferMode> getTransferModes() {
      return handlers.keySet();
    }

    public boolean isValid() {
      return !handlers.isEmpty();
    }

    public void handleDrag(TreeTransferMode transferMode) {
      handlers.get(transferMode).handleDrag(this);
    }

    public ClipboardContent getClipboardContent() {
      if (!isValid())
        throw new NullPointerException();
      return clipboardContent;
    }
  }

  static class TreeDropCandidates {
    private final Clipboard clipboard;
    private final TreeDropPosition position;
    private final TreeEntry<?> adjacentEntry;
    private final Map<TreeTransferMode, TreeDropCandidateImpl<?>> dropCandidates;

    public TreeDropCandidates(
        List<DropHandler<?>> handlers,
        Clipboard clipboard,
        TreeDropPosition position,
        TreeEntry<?> adjacentEntry) {
      this.clipboard = clipboard;
      this.position = position;
      this.adjacentEntry = adjacentEntry;
      Map<TreeTransferMode, TreeDropCandidateImpl<?>> applicableDropCandidates = new HashMap<>();
      this.dropCandidates = unmodifiableMap(applicableDropCandidates);

      handlers = new ArrayList<>(handlers);
      reverse(handlers);

      for (DropHandler<?> handler : handlers) {
        Set<TreeTransferMode> handlerTransferModes = new HashSet<>(handler.getTransferModes());
        handlerTransferModes.removeAll(applicableDropCandidates.keySet());
        if (!handlerTransferModes.isEmpty()) {
          TreeDropCandidateImpl<?> candidate = new TreeDropCandidateImpl<>(handler, this);
          if (candidate.isValid()) {
            for (TreeTransferMode handlerTransferMode : handlerTransferModes) {
              applicableDropCandidates.put(handlerTransferMode, candidate);
            }
          }
        }
      }
    }

    public TreeDropPosition position() {
      return position;
    }

    public TreeEntry<?> adjacentEntry() {
      return adjacentEntry;
    }

    Set<TreeTransferMode> getCandidateTransferModes() {
      return unmodifiableSet(dropCandidates.keySet());
    }

    TreeDropCandidateImpl<?> getCandidate(TreeTransferMode transferMode) {
      return dropCandidates.get(transferMode);
    }
  }

  static class TreeDropCandidateImpl<T> implements TreeDropCandidate<T> {
    private final DropHandler<T> handler;
    private final T data;
    private final boolean valid;
    private final TreeDropCandidates owner;

    public TreeDropCandidateImpl(DropHandler<T> handler, TreeDropCandidates owner) {
      this.handler = handler;
      this.owner = owner;
      T data = null;
      for (DataFormat format : handler.getConverters().keySet()) {
        try {
          String content = (String) owner.clipboard.getContent(format);
          if (content != null) {
            data = handler.getConverters().get(format).decode.apply(content);
            break;
          }
        } catch (Exception e) {}
      }
      this.data = data;

      valid = handler.checkDrop(this);
    }

    @Override
    public Stream<T> data() {
      return Stream.of(data);
    }

    @Override
    public TreeDropPosition position() {
      return owner.position();
    }

    public boolean isValid() {
      return valid;
    }

    @Override
    public TreeEntry<?> adjacentEntry() {
      return owner.adjacentEntry();
    }

    public void handleDrop() {
      handler.handleDrop(this);
    }
  }

  private final List<DragHandler<?>> dragHandlers;
  private final List<DropHandler<?>> dropHandlers;

  public TreeClipboardManager() {
    dragHandlers = new ArrayList<>();
    dropHandlers = new ArrayList<>();
  }

  public <T> TreeDragCandidateImpl<T> getDragCandidate(TreeEntry<T> entry) {
    return new TreeDragCandidateImpl<>(dragHandlers, entry);
  }

  public TreeDropCandidates getDropCandidates(
      Clipboard clipboard,
      TreeDropPosition position,
      TreeEntry<?> adjacentEntry) {
    return new TreeDropCandidates(dropHandlers, clipboard, position, adjacentEntry);
  }

  public <T> TreeClipboardImpl<T> getForType(TypeToken<T> type) {
    return new TreeClipboardImpl<>(type, dragHandlers::add, dropHandlers::add);
  }
}
