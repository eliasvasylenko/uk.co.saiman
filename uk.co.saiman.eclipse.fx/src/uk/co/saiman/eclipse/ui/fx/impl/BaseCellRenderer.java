package uk.co.saiman.eclipse.ui.fx.impl;

import java.util.Collection;

import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.fx.ui.workbench.renderers.base.BaseItemContainerRenderer;

import uk.co.saiman.eclipse.model.ui.Cell;
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

  @Override
  public void do_init(IEventBroker broker) {
    registerEventListener(broker, UIEvents.UILabel.TOPIC_ICONURI);

    registerEventListener(broker, UIEvents.UILabel.TOPIC_LABEL);
    registerEventListener(broker, UIEvents.UILabel.TOPIC_LOCALIZED_LABEL);

    registerEventListener(broker, UIEvents.UILabel.TOPIC_TOOLTIP);
    registerEventListener(broker, UIEvents.UILabel.TOPIC_LOCALIZED_TOOLTIP);

    registerEventListener(broker, UIEvents.Item.TOPIC_SELECTED);
    registerEventListener(broker, UIEvents.Item.TOPIC_ENABLED);

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
