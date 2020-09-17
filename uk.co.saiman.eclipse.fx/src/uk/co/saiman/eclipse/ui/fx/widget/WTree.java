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
package uk.co.saiman.eclipse.ui.fx.widget;

import org.eclipse.fx.ui.workbench.renderers.base.widget.WWidget;

import uk.co.saiman.eclipse.model.ui.MTree;

/**
 * Abstraction of tree widget
 * 
 * @param <N>
 *          the native widget
 */
public interface WTree<N> extends WWidget<MTree> {
  /**
   * Append a widget
   * 
   * @param widget
   *          the widget
   */
  void addCell(WCell<?> widget);

  /**
   * Insert a widget at the given index
   * 
   * @param idx
   *          the index
   * @param widget
   *          the widget
   */
  void addCell(int idx, WCell<?> widget);

  /**
   * Remove the widget
   * 
   * @param widget
   *          the widget to remove
   */
  void removeCell(WCell<?> widget);

  @Override
  N getWidget();
}
