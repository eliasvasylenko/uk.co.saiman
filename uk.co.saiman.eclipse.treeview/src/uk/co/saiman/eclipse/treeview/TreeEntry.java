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

import java.util.Optional;

import javafx.scene.control.TreeItem;
import uk.co.saiman.reflection.token.TypeToken;
import uk.co.saiman.reflection.token.TypedReference;

/**
 * This interface defines the type of a {@link TreeItem} for a
 * {@link ModularTreeView}. It provides access to the actual typed data of each
 * node, as well as to the contributions which apply to that node, and the
 * {@link TreeEntry item data} of the parent.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of the data
 */
public interface TreeEntry<T> {
  /**
   * @return the typed data of a tree node
   */
  TypedReference<? extends T> typedData();

  /**
   * @return the actual data of a tree node
   */
  default T data() {
    return typedData().getObject();
  }

  /**
   * @return the type of the actual data of a tree node
   */
  default TypeToken<?> type() {
    return typedData().getTypeToken();
  }

  /**
   * @return a {@link TreeEntry} interface over the parent node
   */
  Optional<TreeEntry<?>> parent();

  /**
   * Refresh the tree cell associated with this tree item.
   * 
   * @param recursive
   *          true if the item's children should also be refreshed recursively,
   *          false otherwise
   */
  void refresh(boolean recursive);
}
