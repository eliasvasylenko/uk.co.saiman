/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static org.eclipse.e4.ui.workbench.IPresentationEngine.NO_AUTO_COLLAPSE;

import java.util.Collection;

import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.fx.ui.workbench.renderers.base.BaseItemContainerRenderer;

import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.model.ui.MTree;
import uk.co.saiman.eclipse.ui.fx.widget.WCell;
import uk.co.saiman.eclipse.ui.fx.widget.WTree;

/**
 * Base renderer of {@link MTree}
 * 
 * @param <N> the native widget type
 */
public abstract class BaseTreeRenderer<N> extends BaseItemContainerRenderer<MTree, MCell, WTree<N>> {
  @Override
  protected void do_init(IEventBroker broker) {
    registerEventListener(broker, UIEvents.UILabel.TOPIC_ICONURI);

    registerEventListener(broker, UIEvents.UILabel.TOPIC_LABEL);
    registerEventListener(broker, UIEvents.UILabel.TOPIC_LOCALIZED_LABEL);

    registerEventListener(broker, UIEvents.UILabel.TOPIC_TOOLTIP);
    registerEventListener(broker, UIEvents.UILabel.TOPIC_LOCALIZED_TOOLTIP);
  }

  @Override
  protected void doProcessContent(MTree element) {
    WTree<N> tree = getWidget(element);

    if (tree == null) {
      getLogger().error("No widget found for '" + element + "'");
      return;
    }

    element.getTags().add(NO_AUTO_COLLAPSE);

    Class<?> cl = tree.getWidget().getClass();
    do {
      element.getContext().set(cl.getName(), tree.getWidget());
      cl = cl.getSuperclass();
    } while (!cl.equals(Object.class)); // $NON-NLS-1$

    IContributionFactory contributionFactory = element.getContext().get(IContributionFactory.class);
    Object newCell = contributionFactory.create(element.getContributionURI(), element.getContext());
    element.setObject(newCell);

    for (MCell child : element.getChildren()) {
      if (child.isToBeRendered()) {
        Object widget = child.getWidget();
        if (widget == null) {
          widget = engineCreateWidget(child);
        }
        if (widget != null && isChildRenderedAndVisible(child)) {
          tree.addCell((WCell<?>) widget);
        }
      }
    }
  }

  @Override
  public void handleChildrenRemove(MTree parent, Collection<MCell> cells) {
    for (MCell cell : cells) {
      if (cell.isToBeRendered() && cell.getWidget() != null) {
        hideChild(parent, cell);
      }
    }
  }

  @Override
  public void handleChildrenAddition(MTree parent, Collection<MCell> cells) {
    for (MCell cell : cells) {
      if (cell.isToBeRendered()) {
        if (cell.getWidget() == null) {
          engineCreateWidget(cell);
        } else {
          childRendered(parent, cell);
        }
      }
    }
  }

  @Override
  public void do_childRendered(MTree parentElement, MUIElement element) {
    if (inContentProcessing(parentElement) || !isChildRenderedAndVisible(element)) {
      return;
    }

    int idx = getRenderedIndex(parentElement, element);
    WTree<N> tree = getWidget(parentElement);
    if (tree == null) {
      getLogger().error("No widget found for '" + parentElement + "'");
      return;
    }

    WCell<?> cell = (WCell<?>) element.getWidget();
    if (cell != null) {
      tree.addCell(idx, cell);
    } else {
      getLogger().error("The widget of the element '" + element + "' is null");
    }
  }

  @Override
  public void do_hideChild(MTree container, MUIElement cell) {
    WTree<N> tree = getWidget(container);

    if (tree == null) {
      return;
    }

    WCell<?> widget = (WCell<?>) cell.getWidget();
    if (widget != null) {
      tree.removeCell(widget);
    }
  }
}
