/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.eclipse.treeview.
 *
 * uk.co.saiman.eclipse.treeview is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.treeview is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.treeview.impl;

import static javafx.css.PseudoClass.getPseudoClass;
import static uk.co.saiman.eclipse.treeview.TreeDropPosition.AFTER_CHILD;
import static uk.co.saiman.eclipse.treeview.TreeDropPosition.BEFORE_CHILD;
import static uk.co.saiman.eclipse.treeview.TreeDropPosition.OVER;
import static uk.co.saiman.fx.FxUtilities.getResource;
import static uk.co.saiman.fx.FxmlLoadBuilder.build;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javafx.css.PseudoClass;
import javafx.geometry.Point2D;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import uk.co.saiman.collection.StreamUtilities;
import uk.co.saiman.eclipse.treeview.ModularTreeView;
import uk.co.saiman.eclipse.treeview.TreeDropPosition;
import uk.co.saiman.eclipse.treeview.TreeEntry;
import uk.co.saiman.eclipse.treeview.TreeTransferMode;
import uk.co.saiman.eclipse.treeview.impl.TreeClipboardManager.TreeDragCandidateImpl;
import uk.co.saiman.eclipse.treeview.impl.TreeClipboardManager.TreeDropCandidates;
import uk.co.saiman.fx.FxUtilities;

/**
 * A basic tree cell implementation for use with {@link TreeItem}. This class
 * may be extended to provide further functionality.
 * <p>
 * This implementation provides generalized drag-and-drop/copy-and-paste
 * functionality, as well as editing and update support.
 * 
 * @author Elias N Vasylenko
 */
public class ModularTreeCell extends TreeCell<TreeEntry<?>> {
  private static final String STYLE_CLASS = "ModularTreeCell";
  private static final PseudoClass DRAG_OVER_CLASS = getPseudoClass("drag-over");
  private static final PseudoClass DRAG_BEFORE_CLASS = getPseudoClass("drag-before");
  private static final PseudoClass DRAG_AFTER_CLASS = getPseudoClass("drag-after");

  private TreeDragCandidateImpl<?> dragCandidate;
  private final Map<TreeDropPosition, TreeDropCandidates> dropCandidates = new HashMap<>();

  /**
   * Load a new instance from the FXML located according to
   * {@link FxUtilities#getResource(Class)} for this class.
   * 
   * @param tree
   *          the owning tree view
   */
  public ModularTreeCell(ModularTreeView tree) {
    getStyleClass().add(STYLE_CLASS);
    initializeEvents();
  }

  @Override
  protected void updateItem(TreeEntry<?> item, boolean empty) {
    super.updateItem(item, empty);

    if (empty || item == null) {
      clearItem();
    } else {
      updateItem();
      commitEdit(item);
    }
  }

  private void clearItem() {
    setGraphic(null);
    setEditable(false);
  }

  private <T> void updateItem() {
    setGraphic(getTreeItem().getGraphic());
    setEditable(getModularTreeItem().isEditable());
  }

  private void initializeEvents() {
    setOnDragDetected(event -> {
      if (dragCandidate == null) {
        dragCandidate = getModularTreeItem().getDragCandidate();
      }
      if (dragCandidate.isValid()) {
        event.consume();

        Dragboard db = startDragAndDrop(
            dragCandidate
                .getTransferModes()
                .stream()
                .map(this::toJavaFXTransferMode)
                .flatMap(StreamUtilities::streamOptional)
                .toArray(TransferMode[]::new));
        db.setContent(dragCandidate.getClipboardContent());
      }
    });
    setOnDragDone(event -> {
      if (event.isAccepted()) {
        event.consume();

        ModularTreeItem<?> parent = getModularTreeItem().getModularParent().get();
        dragCandidate.handleDrag(fromJavaFXTransferMode(event.getTransferMode()));
        dragCandidate = null;
        parent.getEntry().refresh(true);
      }
    });

    setOnDragOver(event -> {
      if (getModularTreeItem() != null && event.getGestureSource() != this) {
        TreeDropPosition position = getDropPosition(event);
        if (position != null) {
          event.consume();
          event
              .acceptTransferModes(
                  dropCandidates
                      .get(position)
                      .getCandidateTransferModes()
                      .stream()
                      .map(this::toJavaFXTransferMode)
                      .flatMap(StreamUtilities::streamOptional)
                      .toArray(TransferMode[]::new));

          decorateDrag(position);
        } else {
          undecorateDrag();
        }
      }
    });
    setOnDragEntered(event -> {
      if (getModularTreeItem() != null) {
        for (TreeDropPosition position : TreeDropPosition.values()) {
          TreeDropCandidates candidate = getModularTreeItem()
              .getDropCandidates(event.getDragboard(), position);
          if (!candidate.getCandidateTransferModes().isEmpty()) {
            dropCandidates.put(position, candidate);
          }
        }
        if (!dropCandidates.isEmpty()) {
          event.consume();
        }
      }
    });
    setOnDragExited(event -> {
      if (!dropCandidates.isEmpty()) {
        dropCandidates.clear();
        event.consume();
        undecorateDrag();
      }
    });
    setOnDragDropped(event -> {
      if (event.isAccepted() && event.getGestureTarget() == this) {
        event.consume();
        event.setDropCompleted(true);

        TreeDropPosition position = getDropPosition(event);
        dropCandidates
            .get(position)
            .getCandidate(fromJavaFXTransferMode(event.getAcceptedTransferMode()))
            .handleDrop();
        if (position == OVER) {
          getModularTreeItem().getEntry().refresh(true);
        } else {
          getModularTreeItem().getModularParent().get().getEntry().refresh(true);
        }
      }
    });
  }

  private void decorateDrag(TreeDropPosition position) {
    pseudoClassStateChanged(DRAG_OVER_CLASS, position == OVER);
    pseudoClassStateChanged(DRAG_BEFORE_CLASS, position == BEFORE_CHILD);
    pseudoClassStateChanged(DRAG_AFTER_CLASS, position == AFTER_CHILD);
  }

  private void undecorateDrag() {
    pseudoClassStateChanged(DRAG_OVER_CLASS, false);
    pseudoClassStateChanged(DRAG_BEFORE_CLASS, false);
    pseudoClassStateChanged(DRAG_AFTER_CLASS, false);
  }

  private Optional<TransferMode> toJavaFXTransferMode(TreeTransferMode from) {
    switch (from) {
    case COPY:
      return Optional.of(TransferMode.COPY);
    case LINK:
      return Optional.of(TransferMode.LINK);
    case MOVE:
      return Optional.of(TransferMode.MOVE);
    case DISCARD:
      return Optional.empty();
    default:
      throw new AssertionError();
    }
  }

  private TreeTransferMode fromJavaFXTransferMode(TransferMode from) {
    switch (from) {
    case COPY:
      return TreeTransferMode.COPY;
    case LINK:
      return TreeTransferMode.LINK;
    case MOVE:
      return TreeTransferMode.MOVE;
    default:
      throw new AssertionError();
    }
  }

  private TreeDropPosition getDropPosition(DragEvent event) {
    Point2D sceneCoordinates = localToScene(0d, 0d);
    double height = getHeight();
    double y = event.getSceneY() - (sceneCoordinates.getY());

    if (dropCandidates.containsKey(OVER)) {
      if (y > height * 0.75) {
        if (dropCandidates.containsKey(AFTER_CHILD)) {
          return AFTER_CHILD;
        }
      } else if (y < height * 0.25) {
        if (dropCandidates.containsKey(BEFORE_CHILD)) {
          return BEFORE_CHILD;
        }
      }
      return OVER;
    } else if (y > height * 0.5) {
      if (dropCandidates.containsKey(AFTER_CHILD)) {
        return AFTER_CHILD;
      }
    } else if (dropCandidates.containsKey(BEFORE_CHILD)) {
      return BEFORE_CHILD;
    }

    return null;
  }

  ModularTreeItem<?> getModularTreeItem() {
    return (ModularTreeItem<?>) getTreeItem();
  }

  @Override
  public void startEdit() {
    super.startEdit();
    if (this.isEditing()) {
      getModularTreeItem().refreshContributions();
    }
  }

  @Override
  public void commitEdit(TreeEntry<?> newValue) {
    boolean editing = this.isEditing();
    super.commitEdit(newValue);
    if (editing) {
      getModularTreeItem().refreshContributions();
    }
  }

  @Override
  public void cancelEdit() {
    boolean editing = this.isEditing();
    super.cancelEdit();
    if (editing) {
      getModularTreeItem().refreshContributions();
    }
  }
}
