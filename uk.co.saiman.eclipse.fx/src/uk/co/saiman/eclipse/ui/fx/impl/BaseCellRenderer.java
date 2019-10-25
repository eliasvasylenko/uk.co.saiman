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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.menu.MPopupMenu;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.fx.ui.workbench.renderers.base.BaseItemContainerRenderer;
import org.eclipse.fx.ui.workbench.renderers.base.widget.WPopupMenu;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.ui.SaiUiEvents;
import uk.co.saiman.eclipse.ui.fx.widget.WCell;

/**
 * Base renderer of {@link MCell}
 * 
 * @param <N>
 *          the native widget type
 */
public abstract class BaseCellRenderer<N>
    extends BaseItemContainerRenderer<MCell, MCell, WCell<N>> {
  /**
   * Eventbroker to use
   */
  protected IEventBroker eventBroker;

  @Override
  public void do_init(IEventBroker broker) {
    registerEventListener(broker, UIEvents.UILabel.TOPIC_ICONURI);

    registerEventListener(broker, UIEvents.UILabel.TOPIC_LABEL);
    registerEventListener(broker, UIEvents.UILabel.TOPIC_LOCALIZED_LABEL);

    registerEventListener(broker, UIEvents.UILabel.TOPIC_TOOLTIP);
    registerEventListener(broker, UIEvents.UILabel.TOPIC_LOCALIZED_TOOLTIP);

    registerEventListener(broker, UIEvents.Item.TOPIC_SELECTED);
    registerEventListener(broker, UIEvents.Item.TOPIC_ENABLED);

    registerEventListener(broker, SaiUiEvents.EditableCell.TOPIC_EDITING);

    registerEventListener(broker, SaiUiEvents.Cell.TOPIC_NULLABLE);
    registerEventListener(broker, SaiUiEvents.Cell.TOPIC_EXPANDED);
    registerEventListener(broker, SaiUiEvents.Cell.TOPIC_CONTEXT_VALUE);

    broker.subscribe(SaiUiEvents.Cell.TOPIC_POPUP_MENU, new EventHandler() {
      @Override
      public void handleEvent(Event event) {
        Object changedObj = event.getProperty(UIEvents.EventTags.ELEMENT);
        MUIElement parent = (MUIElement) changedObj;

        if (parent.getRenderer() == this && UIEvents.isSET(event)) {
          handleSetPopupMenu(
              (MCell) parent,
              (MPopupMenu) event.getProperty(UIEvents.EventTags.NEW_VALUE));
        }
      }
    });

    this.eventBroker = broker;
  }

  @Override
  protected void doProcessContent(MCell element) {
    WCell<N> cell = getWidget(element);

    if (cell == null) {
      getLogger().error("No widget found for '" + element + "'");
      return;
    }

    element.getTags().add(NO_AUTO_COLLAPSE);

    Class<?> cl = cell.getWidget().getClass();
    do {
      element.getContext().set(cl.getName(), cell.getWidget());
      cl = cl.getSuperclass();
    } while (!cl.equals(Object.class));

    IContributionFactory contributionFactory = element.getContext().get(IContributionFactory.class);
    Object newCell = contributionFactory.create(element.getContributionURI(), element.getContext());

    if (!element.isToBeRendered()) {
      return;
    }

    element.setObject(newCell);

    if (element.getPopupMenu() != null) {
      handleSetPopupMenu(element, element.getPopupMenu());
    }

    for (MCell child : new ArrayList<>(element.getChildren())) {
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
  public void handleChildrenRemove(MCell parent, Collection<MCell> cells) {
    for (MCell cell : cells) {
      if (cell.isToBeRendered() && cell.getWidget() != null) {
        hideChild(parent, cell);
      }
    }
  }

  @Override
  public void handleChildrenAddition(MCell parent, Collection<MCell> cells) {
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

  public void handleSetPopupMenu(MCell parent, MPopupMenu property) {
    if (property == null) {
      return;
    }

    if (property.getWidget() == null) {
      engineCreateWidget(property);
    }

    ((WCell<?>) parent.getWidget()).setPopupMenu((WPopupMenu<?>) property.getWidget());
  }

  @Override
  public void do_childRendered(MCell parentElement, MUIElement element) {
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
  public void do_hideChild(MCell container, MUIElement child) {
    WCell<N> cell = getWidget(container);

    if (cell == null) {
      return;
    }

    if (child instanceof MCell) {
      WCell<?> widget = (WCell<?>) child.getWidget();
      if (widget != null) {
        cell.removeCell(widget);
      }
    }
  }
}
