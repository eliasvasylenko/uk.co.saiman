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
 * This file is part of uk.co.saiman.eclipse.fx.
 *
 * uk.co.saiman.eclipse.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.fx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.ui.fx.impl;

import static javafx.css.PseudoClass.getPseudoClass;
import static uk.co.saiman.eclipse.ui.TransferDestination.AFTER_CHILD;
import static uk.co.saiman.eclipse.ui.TransferDestination.BEFORE_CHILD;
import static uk.co.saiman.eclipse.ui.TransferDestination.OVER;
import static uk.co.saiman.eclipse.ui.fx.TransferModes.fromJavaFXTransferMode;

import java.util.HashMap;
import java.util.Map;

import javafx.css.PseudoClass;
import javafx.geometry.Point2D;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import uk.co.saiman.collection.StreamUtilities;
import uk.co.saiman.eclipse.ui.TransferDestination;
import uk.co.saiman.eclipse.ui.fx.TransferModes;
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
public class ModularTreeCell extends TreeCell<Object> {
  private static final String STYLE_CLASS = "ModularTreeCell";
  private static final PseudoClass DRAG_OVER_CLASS = getPseudoClass("drag-over");
  private static final PseudoClass DRAG_BEFORE_CLASS = getPseudoClass("drag-before");
  private static final PseudoClass DRAG_AFTER_CLASS = getPseudoClass("drag-after");

  private TransferOut<?> dragCandidate;
  private final Map<TransferDestination, TransfersIn> dropCandidates = new HashMap<>();

  /**
   * Load a new instance from the FXML located according to
   * {@link FxUtilities#getResource(Class)} for this class.
   */
  public ModularTreeCell() {
    getStyleClass().add(STYLE_CLASS);
    initializeEvents();
  }

  @Override
  protected void updateItem(Object data, boolean empty) {
    super.updateItem(data, empty);

    if (empty || data == null) {
      clearItem();
    } else {
      updateItem();
      commitEdit(data);
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
      if (!dragCandidate.getTransferModes().isEmpty()) {
        event.consume();

        Dragboard db = startDragAndDrop(
            dragCandidate
                .getTransferModes()
                .stream()
                .map(TransferModes::toJavaFXTransferMode)
                .flatMap(StreamUtilities::streamOptional)
                .toArray(javafx.scene.input.TransferMode[]::new));
        db.setContent(dragCandidate.getClipboardContent());
      }
    });
    setOnDragDone(event -> {
      if (event.isAccepted()) {
        event.consume();

        TreeItemImpl<?> parent = getModularTreeItem().getModularParent().get();
        dragCandidate.handleDrag(fromJavaFXTransferMode(event.getTransferMode()));
        dragCandidate = null;
        parent.requestRefresh();
      }
    });

    setOnDragOver(event -> {
      if (getModularTreeItem() != null && event.getGestureSource() != this) {
        TransferDestination position = getDropPosition(event);
        if (position != null) {
          event.consume();
          event
              .acceptTransferModes(
                  dropCandidates
                      .get(position)
                      .getCandidateTransferModes()
                      .stream()
                      .map(TransferModes::toJavaFXTransferMode)
                      .flatMap(StreamUtilities::streamOptional)
                      .toArray(javafx.scene.input.TransferMode[]::new));

          decorateDrag(position);
        } else {
          undecorateDrag();
        }
      }
    });
    setOnDragEntered(event -> {
      if (getModularTreeItem() != null) {
        for (TransferDestination position : TransferDestination.values()) {
          TransfersIn candidate = getModularTreeItem()
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

        TransferDestination position = getDropPosition(event);
        dropCandidates
            .get(position)
            .getCandidate(fromJavaFXTransferMode(event.getAcceptedTransferMode()))
            .handleDrop();
        if (position == OVER) {
          getModularTreeItem().requestRefresh();
        } else {
          getModularTreeItem().getModularParent().get().requestRefresh();
        }
      }
    });
  }

  private void decorateDrag(TransferDestination position) {
    pseudoClassStateChanged(DRAG_OVER_CLASS, position == OVER);
    pseudoClassStateChanged(DRAG_BEFORE_CLASS, position == BEFORE_CHILD);
    pseudoClassStateChanged(DRAG_AFTER_CLASS, position == AFTER_CHILD);
  }

  private void undecorateDrag() {
    pseudoClassStateChanged(DRAG_OVER_CLASS, false);
    pseudoClassStateChanged(DRAG_BEFORE_CLASS, false);
    pseudoClassStateChanged(DRAG_AFTER_CLASS, false);
  }

  private TransferDestination getDropPosition(DragEvent event) {
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

  TreeItemImpl<?> getModularTreeItem() {
    return (TreeItemImpl<?>) getTreeItem();
  }

  @Override
  public void startEdit() {
    super.startEdit();
    if (this.isEditing()) {
      getModularTreeItem().refreshContributions();
    }
  }

  @Override
  public void commitEdit(Object newValue) {
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
