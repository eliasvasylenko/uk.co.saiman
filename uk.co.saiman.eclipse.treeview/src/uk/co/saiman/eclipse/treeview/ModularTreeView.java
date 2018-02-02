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

import static java.util.Collections.reverse;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.PickResult;
import uk.co.saiman.fx.FxmlLoadBuilder;

/**
 * An implementation of {@link TreeView} which allows for modular and extensible
 * specification of table structure.
 * 
 * @author Elias N Vasylenko
 */
public class ModularTreeView extends TreeView<TreeEntry<?>> {
  /**
   * Instantiate an empty tree view containing the {@link DefaultContribution
   * default cell contribution} over a cell factory which instantiates an empty
   * {@link ModularTreeCell}.
   */
  public ModularTreeView() {
    FxmlLoadBuilder.build().object(this).load();
    setCellFactory(v -> new ModularTreeCell(this));

    setMinWidth(0);
    prefWidth(0);
    setEditable(true);

    addEventHandler(KeyEvent.ANY, event -> {
      switch (event.getCode()) {
      case DELETE:
        event.consume();
        if (event.getEventType() == KeyEvent.KEY_PRESSED) {
          List<TreeItem<?>> selection = new ArrayList<>(getSelectionModel().getSelectedItems());
          reverse(selection);
          for (TreeItem<?> treeItem : selection) {
            ((ModularTreeItem<?>) treeItem).delete();
          }
        }
      case CONTEXT_MENU:
        event.consume();
        if (event.getEventType() == KeyEvent.KEY_RELEASED) {
          Node selectionBounds = this;

          Bounds sceneBounds = selectionBounds.localToScene(selectionBounds.getLayoutBounds());
          Bounds screenBounds = selectionBounds.localToScreen(selectionBounds.getLayoutBounds());

          PickResult pickResult = new PickResult(
              selectionBounds,
              sceneBounds.getMaxX(),
              sceneBounds.getMaxY());

          fireEvent(
              new ContextMenuEvent(
                  ContextMenuEvent.CONTEXT_MENU_REQUESTED,
                  sceneBounds.getMaxX(),
                  sceneBounds.getMaxY(),
                  screenBounds.getMaxX(),
                  screenBounds.getMaxY(),
                  true,
                  pickResult));
        }
        break;
      default:
        break;
      }
    });
  }
}
