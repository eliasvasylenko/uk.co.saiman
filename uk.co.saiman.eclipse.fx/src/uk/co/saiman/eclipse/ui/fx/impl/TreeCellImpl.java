/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
import static uk.co.saiman.eclipse.ui.fx.TransferModes.fromJavaFXTransferMode;
import static uk.co.saiman.eclipse.ui.fx.impl.TransferDestination.AFTER_CHILD;
import static uk.co.saiman.eclipse.ui.fx.impl.TransferDestination.BEFORE_CHILD;
import static uk.co.saiman.eclipse.ui.fx.impl.TransferDestination.OVER;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.e4.ui.model.application.ui.MContext;

import javafx.beans.InvalidationListener;
import javafx.css.PseudoClass;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import uk.co.saiman.collection.StreamUtilities;
import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.model.ui.MEditableCell;
import uk.co.saiman.eclipse.ui.SaiUiModel;
import uk.co.saiman.eclipse.ui.fx.TransferModes;
import uk.co.saiman.eclipse.ui.fx.widget.WCell;

/**
 * A basic tree cell implementation for use with {@link TreeItem}. This class
 * may be extended to provide further functionality.
 * <p>
 * This implementation provides generalized drag-and-drop/copy-and-paste
 * functionality, as well as editing and update support.
 * 
 * @author Elias N Vasylenko
 */
public class TreeCellImpl extends TreeCell<MCell> {
  private static final String STYLE_CLASS = "ModularTreeCell";
  private static final PseudoClass DRAG_OVER_CLASS = getPseudoClass("drag-over");
  private static final PseudoClass DRAG_BEFORE_CLASS = getPseudoClass("drag-before");
  private static final PseudoClass DRAG_AFTER_CLASS = getPseudoClass("drag-after");

  private TransferCellOut dragCandidate;
  private final Map<TransferDestination, TransferCellIn> dropCandidates = new HashMap<>();

  public TreeCellImpl() {
    getStyleClass().add(STYLE_CLASS);
    initializeEvents();
  }

  @Override
  protected void updateItem(MCell data, boolean empty) {
    MCell previousData = getItem();
    if (previousData != null && previousData.getWidget() != null) {
      ((WCell<?>) previousData.getWidget()).setIsEditingCallback(null);
    }
    super.updateItem(data, empty);
    if (data != null) {
      ((WCell<?>) data.getWidget()).setIsEditingCallback(this::editingChanged);
    }

    if (empty || data == null) {
      clearItem();
    } else {
      updateItem(data);
    }
  }

  private void editingChanged(boolean editing) {
    if (editing) {
      startEdit();
    } else {
      if (getItem().getTags().contains(SaiUiModel.EDIT_CANCELED)) {
        cancelEdit();
      } else {
        commitEdit(getItem());
      }
    }
  }

  private void clearItem() {
    setGraphic(null);
    setEditable(false);
  }

  private <T> void updateItem(MCell data) {
    setGraphic(getTreeItem().getGraphic());
    setEditable(data instanceof MEditableCell);
  }

  private void initializeEvents() {
    setOnDragDetected(this::dragDetected);
    setOnDragDone(this::dragDone);
    setOnDragOver(this::dragOver);
    setOnDragEntered(this::dragEntered);
    setOnDragExited(this::dragExited);
    setOnDragDropped(this::dragDropped);
  }

  public void dragDetected(MouseEvent event) {
    if (getTreeItem() == null) {
      return;
    }
    dragCandidate = transferOut();
    if (dragCandidate.supportedTransferModes().isEmpty()) {
      dragCandidate = null;
    } else {
      event.consume();

      Dragboard db = startDragAndDrop(
          dragCandidate
              .supportedTransferModes()
              .stream()
              .map(TransferModes::toJavaFXTransferMode)
              .flatMap(StreamUtilities::streamOptional)
              .toArray(javafx.scene.input.TransferMode[]::new));
      db.setContent(dragCandidate.getClipboardContent());
    }
  }

  public void dragDone(DragEvent event) {
    if (getTreeItem() == null) {
      return;
    }
    if (event.isAccepted()) {
      event.consume();
      handleDrag(event.getTransferMode());
    }
  }

  private void handleDrag(javafx.scene.input.TransferMode transferMode) {
    if (dragCandidate != null) {
      dragCandidate.handle(fromJavaFXTransferMode(transferMode));
      dragCandidate = null;
    }
  }

  public void dragOver(DragEvent event) {
    if (getModularTreeItem() != null && event.getGestureSource() != this) {
      TransferDestination position = getDropPosition(event);
      if (position != null) {
        event.consume();
        event
            .acceptTransferModes(
                dropCandidates
                    .get(position)
                    .supportedTransferModes()
                    .stream()
                    .map(TransferModes::toJavaFXTransferMode)
                    .flatMap(StreamUtilities::streamOptional)
                    .toArray(javafx.scene.input.TransferMode[]::new));

        decorateDrag(position);
      } else {
        undecorateDrag();
      }
    }
  }

  public void dragEntered(DragEvent event) {
    if (getModularTreeItem() != null && getItem().isToBeRendered()) {
      for (TransferDestination position : TransferDestination.values()) {
        TransferCellIn candidate = transferIn(event.getDragboard(), position);
        if (!candidate.supportedTransferModes().isEmpty()) {
          dropCandidates.put(position, candidate);
        }
      }
      if (!dropCandidates.isEmpty()) {
        event.consume();
      }
    }
  }

  public void dragExited(DragEvent event) {
    if (!dropCandidates.isEmpty()) {
      dropCandidates.clear();
      event.consume();
      undecorateDrag();
    }
  }

  public void dragDropped(DragEvent event) {
    if (event.isAccepted() && event.getGestureTarget() == this) {
      var source = event.getGestureSource();
      if (source instanceof TreeCellImpl) {
        var node = (TreeCellImpl) source;
        /*
         * TODO we have to handle the removal from the source before we handle the drop,
         * otherwise it's possible that there will be a spurious collision between the
         * dropped item and itself, since it might already exist in the same container.
         */
        node.handleDrag(event.getAcceptedTransferMode());
      }

      event.consume();
      event.setDropCompleted(true);

      TransferDestination position = getDropPosition(event);
      dropCandidates.get(position).handle(fromJavaFXTransferMode(event.getAcceptedTransferMode()));
    }
  }

  public TransferCellOut transferOut() {
    return new TransferCellOut(getItem().getObject(), getItem().getContext());
  }

  public TransferCellIn transferIn(Dragboard clipboard, TransferDestination position) {
    switch (position) {
    case BEFORE_CHILD:
    case AFTER_CHILD:
      var parent = getItem().getParent();
      return new TransferCellIn(
          ((MContribution) parent).getObject(),
          ((MContext) parent).getContext(),
          clipboard,
          position,
          getItem());

    default:
      return new TransferCellIn(getItem().getObject(), getItem().getContext(), clipboard);
    }
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

  TreeItemImpl getModularTreeItem() {
    return (TreeItemImpl) getTreeItem();
  }

  private InvalidationListener defocusListener = i -> {
    if (!getTreeView().isFocused()) {
      Node node = getTreeView().getScene().getFocusOwner();

      while (node != null) {
        if (node == TreeCellImpl.this) {
          return;
        }
        node = node.getParent();
      }

      cancelEdit();
    }
  };

  @Override
  public void startEdit() {
    if (isEditing())
      return;

    super.startEdit();

    if (isEditing()) {
      getScene().focusOwnerProperty().addListener(defocusListener);
      getModularTreeItem().editingStarted();
    }
  }

  @Override
  public void commitEdit(MCell newValue) {
    if (!isEditing())
      return;

    super.commitEdit(newValue);
    getScene().focusOwnerProperty().removeListener(defocusListener);
    getModularTreeItem().editingComplete();
  }

  @Override
  public void cancelEdit() {
    if (!isEditing())
      return;

    super.cancelEdit();
    getScene().focusOwnerProperty().removeListener(defocusListener);
    getModularTreeItem().editingCancelled();
  }
}
