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
 * This file is part of uk.co.saiman.fx.
 *
 * uk.co.saiman.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.fx is distributed in the hope that it will be useful,
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
import javafx.scene.layout.HBox;
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
      updateItem(item);
    }
  }

  protected void clearItem() {
    setGraphic(null);
  }

  protected <T> void updateItem(TreeEntry<T> item) {
    HBox node = (HBox) ((ModularTreeItem<?>.TreeEntryImpl) item).getNode();
    setGraphic(node);
  }
}
