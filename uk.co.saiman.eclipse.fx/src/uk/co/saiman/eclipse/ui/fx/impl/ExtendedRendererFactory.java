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

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.fx.ui.workbench.base.rendering.ElementRenderer;
import org.eclipse.fx.ui.workbench.renderers.fx.DefWorkbenchRendererFactory;

import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.model.ui.Tree;

public class ExtendedRendererFactory extends DefWorkbenchRendererFactory {
  private DefCellRenderer cellRenderer;
  private DefTreeRenderer treeRenderer;

  @Inject
  public ExtendedRendererFactory(IEclipseContext context) {
    super(context);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <R extends ElementRenderer<?, ?>> R getRenderer(MUIElement modelObject) {
    if (modelObject instanceof Cell) {
      if (this.cellRenderer == null) {
        this.cellRenderer = make(getCellRendererClass());
      }
      return (R) this.cellRenderer;
    } else if (modelObject instanceof Tree) {
      if (this.treeRenderer == null) {
        this.treeRenderer = make(getTreeRendererClass());
      }
      return (R) this.treeRenderer;
    }
    return super.getRenderer(modelObject);
  }

  protected Class<DefCellRenderer> getCellRendererClass() {
    return DefCellRenderer.class;
  }

  protected Class<DefTreeRenderer> getTreeRendererClass() {
    return DefTreeRenderer.class;
  }
}
