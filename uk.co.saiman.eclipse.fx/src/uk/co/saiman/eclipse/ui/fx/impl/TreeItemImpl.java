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

import java.util.stream.Stream;

import org.eclipse.core.runtime.IAdaptable;

import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.model.ui.MEditableCell;
import uk.co.saiman.eclipse.ui.SaiUiModel;

/**
 * @author Elias N Vasylenko
 */
public class TreeItemImpl extends TreeItem<MCell> implements IAdaptable {
  // ui container
  private final BorderPane container;

  public TreeItemImpl(MCell domElement) {
    container = new BorderPane();
    setGraphic(container);
    setValue(domElement);
  }

  void setWidget(Pane widget) {
    container.setCenter(widget);
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

    if (adapter.isAssignableFrom(MCell.class)) {
      return (U) getValue();
    }

    // Object data = getData();
    // return getValue().getContext().get(Adapter.class).adapt(data, adapter);

    return null;
  }

  public void editingStarted() {
    MEditableCell cell = (MEditableCell) getValue();
    cell.getTags().remove(SaiUiModel.EDIT_CANCELED);
    cell.setEditing(true);
  }

  public void editingComplete() {
    MEditableCell cell = (MEditableCell) getValue();
    cell.setEditing(false);
  }

  public void editingCancelled() {
    MEditableCell cell = (MEditableCell) getValue();
    cell.getTags().add(SaiUiModel.EDIT_CANCELED);
    cell.setEditing(false);
  }
}
