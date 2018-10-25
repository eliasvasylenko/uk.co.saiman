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

import static uk.co.saiman.eclipse.ui.SaiUiModel.PRIMARY_CONTEXT_KEY;

import java.util.stream.Stream;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.services.adapter.Adapter;

import javafx.scene.control.TreeItem;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.model.ui.EditableCell;
import uk.co.saiman.eclipse.ui.SaiUiModel;
import uk.co.saiman.eclipse.ui.TransferDestination;
import uk.co.saiman.eclipse.ui.fx.TransferCellHandler;
import uk.co.saiman.eclipse.ui.fx.TransferCellIn;
import uk.co.saiman.eclipse.ui.fx.TransferCellOut;

/*
 * TODO Cells need a "context value" property to define which their main value
 * coming from the context is. This way we can know which object needs to have
 * the cells {@link Cell#getTransferFormats() transfer formats} applied to it
 * for drag/drop/copy/paste etc.
 * 
 * When we drag/copy it's easy to know what to do. Serialize the value of the
 * selected cell.
 * 
 * When we drop it's more difficult.
 * 
 * When we drop OVER a cell, find the first child the clipboard is compatible
 * with and for which the context value is modifiable, and set it (and set it to
 * be rendered if necessary).
 * 
 * Failing that, if the cell we drop over itself is compatible and modifiable
 * ... maybe set it or maybe do nothing.
 * 
 * What about dropping BEFORE or AFTER an item? same behavior as dropping over
 * the parent, or nothing. Can't really think of any useful way to apply the
 * drop target semantics.
 * 
 * What about dropping OVER an item with no existing compatible children? i.e. a
 * situation where we can add new children. How do we know what kind of child to
 * turn the clipboard data into? We could add some mechanism to the model itself
 * such as child prototypes / child contributions (in the manner of
 * menu-contributions) but the behavior would be very complex (much moreso than
 * menu-contributions) and it wouldn't nicely map to the nice
 * {@link ChildrenService} API. It may be simpler just to allow manual addition
 * of transfer handlers.
 * 
 * Should these also be considered part of the model api? Should they be FX
 * specific? Should they be accessed through the context?
 * 
 * How does this tie in with copy/paste {@link Handler handlers}? Add handlers
 * which just forward to this system, or use handlers as the mechanism to
 * implement the system?
 */
/**
 * @author Elias N Vasylenko
 */
public class TreeItemImpl extends TreeItem<Cell> implements IAdaptable {
  // ui container
  private final BorderPane container;

  public TreeItemImpl(Cell domElement) {
    container = new BorderPane();
    setGraphic(container);
    setValue(domElement);
  }

  void setWidget(Pane widget) {
    container.setCenter(widget);
  }

  public Object getData() {
    String contextValue = getValue().getProperties().get(PRIMARY_CONTEXT_KEY);
    if (contextValue == null) {
      return null;
    }
    return getValue().getContext().get(contextValue);
  }

  public Stream<TreeItemImpl> getModularChildren() {
    return getChildren().stream().map(c -> (TreeItemImpl) c);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <U> U getAdapter(Class<U> adapter) {
    if (adapter == TreeItem.class) {
      return (U) this;
    }

    if (adapter.isAssignableFrom(Cell.class)) {
      return (U) getValue();
    }

    Object data = getData();
    return getValue().getContext().get(Adapter.class).adapt(data, adapter);
  }

  public void editingStarted() {
    EditableCell cell = (EditableCell) getValue();
    cell.getTags().remove(SaiUiModel.EDIT_CANCELED);
    cell.setEditing(true);
  }

  public void editingComplete() {
    EditableCell cell = (EditableCell) getValue();
    cell.setEditing(false);
  }

  public void editingCancelled() {
    EditableCell cell = (EditableCell) getValue();
    cell.getTags().add(SaiUiModel.EDIT_CANCELED);
    cell.setEditing(false);
  }

  TransferCellOut transferOut() {
    return getValue().getContext().get(TransferCellHandler.class).transferOut(getValue());
  }

  TransferCellIn transferIn(Dragboard clipboard, TransferDestination position) {
    return getValue()
        .getContext()
        .get(TransferCellHandler.class)
        .transferIn(getValue(), clipboard, position);
  }
}
