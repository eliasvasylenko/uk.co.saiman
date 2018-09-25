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

import static org.eclipse.e4.ui.workbench.UIEvents.Context.TOPIC_CONTEXT;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.ELEMENT;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.NEW_VALUE;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.TYPE;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTypes.SET;
import static org.eclipse.e4.ui.workbench.UIEvents.UIElement.TOPIC_TOBERENDERED;

import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.event.Event;

import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.model.ui.Tree;
import uk.co.saiman.eclipse.ui.ChildrenService;
import uk.co.saiman.eclipse.ui.fx.ClipboardService;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

public class UIAddon {
  static final String CHILD_CONTEXT_VALUE = "uk.co.saiman.eclipse.model.ui.child.context.value";
  static final String CHILD_CONTEXT_VALUE_SET = "uk.co.saiman.eclipse.model.ui.child.context.value.set";
  static final String CHILD_CONTEXT_VALUES_SET = "uk.co.saiman.eclipse.model.ui.child.context.values.set";

  @Inject
  private Log log;

  @PostConstruct
  public void initialize(IEclipseContext context, ClipboardServiceImpl clipboardService) {
    context.set(ClipboardService.class, clipboardService);
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
      if (element instanceof Cell) {
        // resourceParts.remove(partResources.remove(part));
      } else if (element instanceof Tree) {

      }
    }
  }

  /**
   * Watch for context creation events so we can inject into the cell contexts
   * before the UI is created.
   */
  @Inject
  @Optional
  private synchronized void cellContextListener(@UIEventTopic(TOPIC_CONTEXT) Event event) {
    try {
      Object value = event.getProperty(NEW_VALUE);
      Object element = event.getProperty(ELEMENT);

      if (value instanceof IEclipseContext && SET.equals(event.getProperty(TYPE))) {
        IEclipseContext context = (IEclipseContext) value;

        if (element instanceof Cell || element instanceof Tree) {
          prepareChildContainer(context);
        }
        if (element instanceof Cell) {
          prepareChild(context, (Cell) element);
        }
      }
    } catch (Exception e) {
      log.log(Level.ERROR, e);
    }
  }

  private void prepareChild(IEclipseContext context, Cell cell) {
    String key = cell.getContextValue();
    if (key != null) {
      Object value = cell.getTransientData().remove(CHILD_CONTEXT_VALUE);
      if (value != null) {
        context.set(key, value);

        @SuppressWarnings("unchecked")
        Consumer<Object> valueSet = (Consumer<Object>) cell
            .getTransientData()
            .remove(CHILD_CONTEXT_VALUE_SET);
        if (valueSet != null) {
          context.declareModifiable(cell.getContextValue());
          context.runAndTrack(new RunAndTrack() {
            boolean firstTry = true;

            @Override
            public boolean changed(IEclipseContext context) {
              if (firstTry) {
                firstTry = false;
                context.get(key);
              } else if (!firstTry) {

                valueSet.accept(context.get(key));
                return false;
              }
              return true;
            }
          });
        }
      } else {
        cell.setParent(null);
      }
    }
  }

  private void prepareChildContainer(IEclipseContext context) {
    try {
      context
          .set(
              ChildrenService.class,
              ContextInjectionFactory.make(ChildrenServiceImpl.class, context));
      context.declareModifiable(ChildrenService.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
