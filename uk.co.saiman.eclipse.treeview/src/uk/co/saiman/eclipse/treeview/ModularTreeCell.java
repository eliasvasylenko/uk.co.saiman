/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
package uk.co.saiman.eclipse.treeview;

import static uk.co.saiman.fx.FxUtilities.getResource;
import static uk.co.saiman.fx.FxmlLoadBuilder.build;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import uk.co.saiman.fx.FxUtilities;

/**
 * A basic tree cell implementation for {@link TreeItem}. This class may be
 * extended to provide further functionality.
 * 
 * @author Elias N Vasylenko
 */
public class ModularTreeCell extends TreeCell<TreeEntry<?>> {
  /**
   * Load a new instance from the FXML located according to
   * {@link FxUtilities#getResource(Class)} for this class.
   * 
   * @param tree
   *          the owning tree view
   */
  public ModularTreeCell(ModularTreeView tree) {
    build().object(this).resource(getResource(ModularTreeCell.class)).load();
  }

  @Override
  protected void updateItem(TreeEntry<?> item, boolean empty) {
    super.updateItem(item, empty);

    if (empty || item == null) {
      clearItem();
    } else {
      updateItem();
    }
  }

  private void clearItem() {
    setGraphic(null);
    setEditable(false);
  }

  private <T> void updateItem() {
    setGraphic(getTreeItem().getGraphic());
    setEditable(((ModularTreeItem<?>) getTreeItem()).isEditable());
  }

  @Override
  public void startEdit() {
    if (!this.isEditable() || !this.getTreeView().isEditable()) {
      return;
    }
    super.startEdit();
    if (this.isEditing()) {
      ((ModularTreeItem<?>) getTreeItem()).refreshContributions(() -> commitEdit(getItem()));
    }
  }

  @Override
  public void commitEdit(TreeEntry<?> newValue) {
    super.commitEdit(newValue);
    if (getParent() != null) {
      ((ModularTreeItem<?>) getTreeItem().getParent()).refreshContributions(null);
    }
    ((ModularTreeItem<?>) getTreeItem()).refreshContributions(null);
  }

  @Override
  public void cancelEdit() {
    super.cancelEdit();
    ((ModularTreeItem<?>) getTreeItem()).refreshContributions(null);
  }
}
