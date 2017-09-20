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
package uk.co.saiman.fx;

import static javafx.css.PseudoClass.getPseudoClass;

import javafx.scene.Node;

/**
 * A type of contribution for items in a {@link ModularTreeView}.
 * 
 * Loosely based on ideas from Eclipse CNF
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of the tree item data
 */
public interface TreeCellContribution<T> extends TreeContribution<T> {
  /**
   * Used to change the default cell configuration strategy.
   * <p>
   * Here is also a good place to mark a cell with a pseudo-class to flag for
   * custom css styling.
   * 
   * @param <U>
   *          the specific type of the tree item
   * @param data
   *          the data contents of the tree item
   * @param content
   *          the current cell content for the tree item
   * @return the new cell content for the tree item
   */
  <U extends T> Node configureCell(TreeItemData<U> data, Node content);

  default Node configurePseudoClass(Node content) {
    return configurePseudoClass(content, getClass().getSimpleName());
  }

  default Node configurePseudoClass(Node content, String name) {
    content.pseudoClassStateChanged(getPseudoClass(name), true);
    return content;
  }
}
