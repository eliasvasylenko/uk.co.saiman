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

import static org.eclipse.e4.ui.workbench.IPresentationEngine.NO_AUTO_COLLAPSE;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;
import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.menu.MPopupMenu;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.fx.ui.workbench.renderers.base.BaseItemContainerRenderer;
import org.eclipse.fx.ui.workbench.renderers.base.widget.WPopupMenu;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.ui.SaiUiEvents;
import uk.co.saiman.eclipse.ui.SaiUiModel;
import uk.co.saiman.eclipse.ui.fx.widget.WCell;

/**
 * Base renderer of {@link Cell}
 * 
 * @param <N>
 *          the native widget type
 */
public abstract class BaseCellRenderer<N> extends BaseItemContainerRenderer<Cell, Cell, WCell<N>> {
  private static final String VISIBILITY_AUTO_HIDDEN = "VisibilityAutoHidden";

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
              (Cell) parent,
              (MPopupMenu) event.getProperty(UIEvents.EventTags.NEW_VALUE));
        }
      }
    });

    broker.subscribe(SaiUiEvents.Cell.TOPIC_NULLABLE, new EventHandler() {
      @Override
      public void handleEvent(Event event) {
        Object changedObj = event.getProperty(UIEvents.EventTags.ELEMENT);
        MUIElement parent = (MUIElement) changedObj;

        if (parent.getRenderer() == this) {
          prepareContextValue((Cell) parent);
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

    getRenderingContext(element).runAndTrack(new RunAndTrack() {
      @Override
      public boolean changed(IEclipseContext context) {
        Object contextValue = context.get(SaiUiEvents.Cell.CONTEXT_VALUE);
        if (contextValue != null && contextValue instanceof String) {
          context.get((String) contextValue);
          prepareContextValue(element);
        }
        return element.isToBeRendered();
      }
    });
    if (!element.isToBeRendered()) {
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
    element.setObject(newCell);

    if (element.getPopupMenu() != null) {
      handleSetPopupMenu(element, element.getPopupMenu());
    }

    for (Cell child : new ArrayList<>(element.getChildren())) {
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

  protected static void prepareContextValue(Cell element) {
    IEclipseContext context = element.getContext();
    String key = element.getContextValue();

    if (context != null && key != null && !key.isEmpty()) {
      // we have a context value specified

      Object value = context.get(key);

      if (value == null) {
        if (!element.isNullable()) {
          throw new NullPointerException();

        } else if (!element.getTags().contains(SaiUiModel.NO_AUTO_REMOVE)) {
          element.setToBeRendered(false);
          element.setParent(null);

        } else if (!element.getTags().contains(SaiUiModel.NO_AUTO_HIDE) && element.isVisible()) {
          element.setVisible(false);
          element.getTags().add(VISIBILITY_AUTO_HIDDEN);
        }
      } else if (element.getTags().remove(VISIBILITY_AUTO_HIDDEN)) {
        element.setVisible(true);
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
  public void do_hideChild(Cell container, MUIElement child) {
    WCell<N> cell = getWidget(container);

    if (cell == null) {
      return;
    }

    if (child instanceof Cell) {
      WCell<?> widget = (WCell<?>) child.getWidget();
      if (widget != null) {
        cell.removeCell(widget);
      }
    }
  }

  static boolean isModifiable(Cell cell) {
    if (!cell.getContext().containsKey(cell.getContextValue())) {
      return false;
    }

    try {
      cell
          .getContext()
          .modify(cell.getContextValue(), cell.getContext().get(cell.getContextValue()));
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
