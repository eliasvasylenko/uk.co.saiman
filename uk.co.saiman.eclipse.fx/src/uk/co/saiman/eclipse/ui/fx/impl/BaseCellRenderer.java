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

import java.util.Collection;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.menu.MPopupMenu;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.fx.ui.workbench.renderers.base.BaseItemContainerRenderer;
import org.eclipse.fx.ui.workbench.renderers.base.Util;
import org.eclipse.fx.ui.workbench.renderers.base.widget.WPopupMenu;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.model.ui.CellContribution;
import uk.co.saiman.eclipse.ui.SaiUiEvents;
import uk.co.saiman.eclipse.ui.fx.widget.WCell;

/**
 * Base renderer of {@link Cell}
 * 
 * @param <N>
 *          the native widget type
 */
public abstract class BaseCellRenderer<N> extends BaseItemContainerRenderer<Cell, Cell, WCell<N>> {
  /**
   * Eventbroker to use
   */
  protected IEventBroker eventBroker;

  private CellContributionHandler cellContributionHandler;

  @Override
  public void do_init(IEventBroker broker) {
    registerEventListener(broker, UIEvents.UILabel.TOPIC_ICONURI);

    registerEventListener(broker, UIEvents.UILabel.TOPIC_LABEL);
    registerEventListener(broker, UIEvents.UILabel.TOPIC_LOCALIZED_LABEL);

    registerEventListener(broker, UIEvents.UILabel.TOPIC_TOOLTIP);
    registerEventListener(broker, UIEvents.UILabel.TOPIC_LOCALIZED_TOOLTIP);

    registerEventListener(broker, UIEvents.Item.TOPIC_SELECTED);
    registerEventListener(broker, UIEvents.Item.TOPIC_ENABLED);

    registerEventListener(broker, UIEvents.Item.TOPIC_ENABLED);

    broker.subscribe(SaiUiEvents.Cell.TOPIC_POPUP_MENU, new EventHandler() {
      @Override
      public void handleEvent(Event event) {
        Object changedObj = event.getProperty(UIEvents.EventTags.ELEMENT);
        MUIElement parent = (MUIElement) changedObj;

        if (parent.getRenderer() == this && UIEvents.isSET(event)) {
          handleSetPopupMenu(
              (Cell) parent,
              (MPopupMenu) event.getProperty(UIEvents.EventTags.NEW_VALUE));
        }
      }
    });

    broker.subscribe(SaiUiEvents.Cell.TOPIC_CONTRIBUTIONS, new EventHandler() {
      @Override
      public void handleEvent(Event event) {
        Object changedObj = event.getProperty(UIEvents.EventTags.ELEMENT);
        MUIElement parent = (MUIElement) changedObj;

        if (parent.getRenderer() == this) {
          if (UIEvents.isADD(event))
            handleContributionAddition(
                (Cell) parent,
                Util.<CellContribution>asCollection(event, UIEvents.EventTags.NEW_VALUE));
          else if (UIEvents.isREMOVE(event))
            handleContributionRemove(
                (Cell) parent,
                Util.<CellContribution>asCollection(event, UIEvents.EventTags.OLD_VALUE));
        }
      }
    });

    this.eventBroker = broker;
  }

  @Override
  protected void doProcessContent(Cell element) {
    WCell<N> cell = getWidget(element);

    if (cell == null) {
      getLogger().error("No widget found for '" + element + "'");
      return;
    }

    Class<?> cl = cell.getWidget().getClass();
    do {
      element.getContext().set(cl.getName(), cell.getWidget());
      cl = cl.getSuperclass();
    } while (!cl.equals(Object.class)); // $NON-NLS-1$

    IContributionFactory contributionFactory = element.getContext().get(IContributionFactory.class);
    Object newCell = contributionFactory.create(element.getContributionURI(), element.getContext());
    element.setObject(newCell);

    cellContributionHandler = ContextInjectionFactory
        .make(CellContributionHandler.class, element.getContext());
    handleContributionAddition(element, element.getContributions());

    if (element.getPopupMenu() != null) {
      handleSetPopupMenu(element, element.getPopupMenu());
    }

    for (Cell child : element.getChildren()) {
      if (child.isToBeRendered()) {
        Object widget = child.getWidget();
        if (widget == null) {
          widget = engineCreateWidget(child);
        }
        if (widget != null && isChildRenderedAndVisible(child)) {
          cell.addCell((WCell<?>) widget);
        }
      }
    }
  }

  @Override
  public void handleChildrenRemove(Cell parent, Collection<Cell> cells) {
    for (Cell cell : cells) {
      if (cell.isToBeRendered() && cell.getWidget() != null) {
        hideChild(parent, cell);
      }
    }
  }

  @Override
  public void handleChildrenAddition(Cell parent, Collection<Cell> cells) {
    for (Cell cell : cells) {
      if (cell.isToBeRendered()) {
        if (cell.getWidget() == null) {
          engineCreateWidget(cell);
        } else {
          childRendered(parent, cell);
        }
      }
    }
  }

  public void handleSetPopupMenu(Cell parent, MPopupMenu property) {
    if (property == null) {
      return;
    }

    if (property.getWidget() == null) {
      engineCreateWidget(property);
    }

    ((WCell<?>) parent.getWidget()).setPopupMenu((WPopupMenu<?>) property.getWidget());
  }

  public void handleContributionRemove(Cell parent, Collection<CellContribution> contributions) {
    cellContributionHandler.handleContributionRemove(parent, contributions);
  }

  public void handleContributionAddition(Cell parent, Collection<CellContribution> contributions) {
    cellContributionHandler.handleContributionAddition(parent, contributions);
  }

  @Override
  public void do_childRendered(Cell parentElement, MUIElement element) {
    if (inContentProcessing(parentElement) || !isChildRenderedAndVisible(element)) {
      return;
    }

    int idx = getRenderedIndex(parentElement, element);
    WCell<N> tree = getWidget(parentElement);
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
  public void do_hideChild(Cell container, MUIElement cell) {
    WCell<N> tree = getWidget(container);

    if (tree == null) {
      return;
    }

    WCell<?> widget = (WCell<?>) cell.getWidget();
    if (widget != null) {
      tree.removeCell(widget);
    }
  }
}
