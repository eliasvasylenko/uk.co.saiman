package uk.co.saiman.eclipse.ui.fx.widget;

import org.eclipse.fx.ui.workbench.renderers.base.widget.WWidget;

import uk.co.saiman.eclipse.model.ui.Cell;

/**
 * Abstraction of cell widget
 * 
 * @param <N>
 *          the native widget
 */
public interface WCell<N> extends WWidget<Cell> {
  /**
   * Callback to invoke when the item is executed
   * 
   * @param runnable
   *          the runnable to invoke
   */
  void setOnActionCallback(Runnable runnable);

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
