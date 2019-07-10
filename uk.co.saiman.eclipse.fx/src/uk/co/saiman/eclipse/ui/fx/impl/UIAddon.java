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

import static org.eclipse.e4.core.contexts.ContextInjectionFactory.make;
import static org.eclipse.e4.ui.workbench.UIEvents.Context.TOPIC_CONTEXT;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.ELEMENT;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.NEW_VALUE;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.TYPE;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTypes.SET;
import static org.eclipse.e4.ui.workbench.UIEvents.UIElement.TOPIC_TOBERENDERED;
import static org.eclipse.e4.ui.workbench.UIEvents.UIElement.TOPIC_WIDGET;
import static uk.co.saiman.eclipse.ui.SaiUiModel.NULLABLE;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.event.Event;

import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.model.ui.MTree;
import uk.co.saiman.eclipse.ui.ChildrenService;
import uk.co.saiman.eclipse.ui.SaiUiModel;
import uk.co.saiman.eclipse.ui.fx.ClipboardService;
import uk.co.saiman.eclipse.ui.fx.EditableCellText;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

public class UIAddon {
  private static final String VISIBILITY_AUTO_HIDDEN = "VisibilityAutoHidden";

  @Inject
  private Log log;

  @PostConstruct
  public void initialize(IEclipseContext context, ClipboardServiceImpl clipboardService) {
    context.set(ClipboardService.class, clipboardService);
    context
        .set(
            EditableCellText.class.getName(),
            (IContextFunction) (c, k) -> make(EditableCellText.class, c));
    context
        .set(
            ChildrenService.class.getName(),
            (IContextFunction) (c, k) -> make(ChildrenServiceImpl.class, c));
  }

  /**
   * Watch for cell/tree close events so we can clean up after things
   */
  @Inject
  @Optional
  private synchronized void cellCloseListener(@UIEventTopic(TOPIC_TOBERENDERED) Event event) {
    Object element = event.getProperty(UIEvents.EventTags.ELEMENT);
    boolean toBeRendered = (Boolean) event.getProperty(UIEvents.EventTags.NEW_VALUE);
    if (!toBeRendered) {
      if (element instanceof MCell) {
        // resourceParts.remove(partResources.remove(part));
      } else if (element instanceof MTree) {

      }
    }
  }

  /**
   * Watch for context creation events so we can inject into the cell contexts
   * before the UI is created.
   */
  @Inject
  @Optional
  private synchronized void contextCreationListener(@UIEventTopic(TOPIC_CONTEXT) Event event) {
    try {
      Object value = event.getProperty(NEW_VALUE);
      Object element = event.getProperty(ELEMENT);

      if (value instanceof IEclipseContext && SET.equals(event.getProperty(TYPE))) {
        IEclipseContext context = (IEclipseContext) value;

        if (element instanceof MTree) {
          ChildrenServiceImpl.prepareChildContainer(context);

        } else if (element instanceof MCell) {
          ChildrenServiceImpl.prepareChildContainer(context);
          ChildrenServiceImpl.prepareChild(context, (MCell) element);
        }
      }
    } catch (Exception e) {
      log.log(Level.ERROR, e);
    }
  }

  @Inject
  @Optional
  private synchronized void widgetCreationListener(@UIEventTopic(TOPIC_WIDGET) Event event) {
    try {
      Object element = event.getProperty(ELEMENT);

      if (SET.equals(event.getProperty(TYPE))
          && element instanceof MContext
          && element instanceof MUIElement) {
        prepareTransferContextValue((MContext) element, (MUIElement) element);
      }
    } catch (Exception e) {
      log.log(Level.ERROR, e);
    }
  }

  protected static <T extends MContext & MUIElement> void prepareTransferContextValue(
      MContext context,
      MUIElement element) {
    if (context.getContext() == null) {
      return;
    }

    String key = context.getProperties().get(SaiUiModel.PRIMARY_CONTEXT_KEY);
    if (key == null || key.isEmpty()) {
      return;
    }

    context.getContext().runAndTrack(new RunAndTrack() {
      @Override
      public boolean changed(IEclipseContext context) {
        boolean isPresent = element.getTags().contains(NULLABLE)
            ? context.containsKey(key)
            : context.get(key) == null;

        if (isPresent && element.getTags().contains(SaiUiModel.HIDE_ON_NULL)) {
          if (element.getTags().contains(EPartService.REMOVE_ON_HIDE_TAG)) {
            element.setToBeRendered(false);
            element.setParent(null);

          } else if (element.isVisible()) {
            element.setVisible(false);
            element.getTags().add(VISIBILITY_AUTO_HIDDEN);
          }
        } else if (element.getTags().remove(VISIBILITY_AUTO_HIDDEN)) {
          element.setVisible(true);
        }

        return true;
      }
    });
  }
}
