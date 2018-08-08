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
package uk.co.saiman.eclipse.ui;

import java.util.List;
import java.util.function.Consumer;

public interface ListItems {
  /*
   * TODO drag and drop handlers automatically by update function??
   * 
   * Update simply passes back a new List containing the dropped-in item and/or
   * without the dragged-out item.
   * 
   * How can this encode copy/move semantics? Doesn't always matter. Copying is
   * done through by serialising/marshalling via the data format.
   * 
   * Case study for complex custom move/copy implementation requirements,
   * ExperimentNode children:
   * 
   * the data format can simply encode as a location in the tree (as well as a
   * full serialization). If it comes from the same tree, the object dropped is
   * fetched from that location, otherwise it's fully deserialized. Presumably the
   * mechanism of serialization is a combination of node type id & persisted state
   * json.
   * 
   * The drop logic then knows whether it needs to copy or move based on whether
   * the object still has a parent node (as drop is resolved first), or whether it
   * exists multiple times in the new list (if it's dropped into the same set of
   * children).
   * 
   */

  <T> void addItem(String id, T child);

  <T> void addItem(String id, T child, Consumer<? super T> update);

  <T> void addItems(String id, List<? extends T> children);

  <T> void addItems(
      String id,
      List<? extends T> children,
      Consumer<? super List<? extends T>> update);
}
