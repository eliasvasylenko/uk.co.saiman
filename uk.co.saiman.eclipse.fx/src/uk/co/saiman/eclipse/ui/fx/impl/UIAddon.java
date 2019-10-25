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

import static java.util.stream.Collectors.toList;
import static org.eclipse.e4.core.contexts.ContextInjectionFactory.make;
import static org.eclipse.e4.ui.workbench.UIEvents.Context.TOPIC_CONTEXT;
import static org.eclipse.e4.ui.workbench.UIEvents.Contribution.TOPIC_OBJECT;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.ELEMENT;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.NEW_VALUE;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.TYPE;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTypes.SET;
import static org.eclipse.e4.ui.workbench.UIEvents.UIElement.TOPIC_TOBERENDERED;
import static org.eclipse.e4.ui.workbench.UIEvents.UIElement.TOPIC_VISIBLE;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.suppliers.PrimaryObjectSupplier;
import org.eclipse.e4.core.internal.contexts.ContextObjectSupplier;
import org.eclipse.e4.core.internal.di.InjectorImpl;
import org.eclipse.e4.core.internal.di.MethodRequestor;
import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.emf.common.util.URI;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.event.Event;

import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.model.ui.MTree;
import uk.co.saiman.eclipse.ui.CallbackMethodRequestor;
import uk.co.saiman.eclipse.ui.Children;
import uk.co.saiman.eclipse.ui.StaticMethodRequestor;
import uk.co.saiman.eclipse.ui.ToBeRendered;
import uk.co.saiman.eclipse.ui.fx.ClipboardService;
import uk.co.saiman.eclipse.ui.fx.EditableCellText;
import uk.co.saiman.eclipse.utilities.ContextBuffer;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

public class UIAddon {
  static final InjectorImpl INJECTOR = new InjectorImpl();

  @Inject
  private Log log;

  @Inject
  private MApplication application;
  @Inject
  private EModelService models;

  @PostConstruct
  public void initialize(IEclipseContext context, ClipboardServiceImpl clipboardService) {
    context.set(ClipboardService.class, clipboardService);
    context
        .set(
            EditableCellText.class.getName(),
            (IContextFunction) (c, k) -> make(EditableCellText.class, c));

  }

  /**
   * Watch for cell/tree close events so we can clean up after things
   */
  @Inject
  @Optional
  private synchronized void toBeRenderedListener(@UIEventTopic(TOPIC_TOBERENDERED) Event event) {
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
  private synchronized void contextCreationListener(
      @UIEventTopic(TOPIC_CONTEXT) Event event,
      IContributionFactory contributionFactory) {
    try {
      Object value = event.getProperty(NEW_VALUE);
      Object element = event.getProperty(ELEMENT);

      if (value instanceof IEclipseContext
          && SET.equals(event.getProperty(TYPE))
          && element instanceof MUIElement) {
        IEclipseContext context = (IEclipseContext) value;
        ChildrenContainer childrenContainer = context.get(ChildrenContainer.class);
        if (childrenContainer != null) {
          childrenContainer.getContextBuffer((MUIElement) element).ifPresent(buffer -> {
            buffer.keys().forEach(s -> {
              context.set(s, buffer.get(s));
            });
          });
        }

        if (element instanceof MContribution) {
          prepareVisibleAnnotation((MContribution) element, contributionFactory, context);
        }
      }
    } catch (Exception e) {
      log.log(Level.ERROR, e);
    }
  }

  private void prepareVisibleAnnotation(
      MContribution element,
      IContributionFactory contributionFactory,
      IEclipseContext context) {
    if (!(element instanceof MUIElement)
        && element.getContributionURI() != null
        && !element.getContributionURI().isBlank()) {
      return;
    }

    try {
      Bundle bundle = element.getContributionURI() == null
          ? null
          : contributionFactory.getBundle(element.getContributionURI());

      if (bundle != null) {
        URI uri = URI.createURI(element.getContributionURI());
        if (uri != null) {
          String path = uri.path();
          if (path.startsWith("/")) {
            path = path.substring(1);
          }
          Class<?> contributionClass = bundle
              .adapt(BundleWiring.class)
              .getClassLoader()
              .loadClass(path);

          var method = findMethod(contributionClass, ToBeRendered.class, true);
          var requestor = resolveStaticRequestor(context, method, null, result -> {});

          if (requestor != null) {
            Object toBeRendered = requestor.execute();

            if (!(toBeRendered instanceof Boolean) || !((boolean) toBeRendered)) {
              ((MUIElement) element).setToBeRendered(false);
            }
          }
        }
      }
    } catch (Exception e) {
      log
          .log(
              Level.ERROR,
              "Cannot load class for MContribution "
                  + element.getElementId()
                  + " / "
                  + element.getContributionURI());
      e.printStackTrace();
    }
  }

  @Inject
  @Optional
  private void objectCreationListener(@UIEventTopic(TOPIC_OBJECT) Event event) {
    try {
      Object element = event.getProperty(ELEMENT);

      if (SET.equals(event.getProperty(TYPE))) {
        updateChildren(element);
      }
    } catch (Exception e) {
      log.log(Level.ERROR, e);
    }
  }

  @Inject
  @Optional
  private void visibilityListener(@UIEventTopic(TOPIC_VISIBLE) Event event) {
    try {
      Object element = event.getProperty(ELEMENT);

      if (SET.equals(event.getProperty(TYPE))) {
        updateChildren(element);
      }
    } catch (Exception e) {
      log.log(Level.ERROR, e);
    }
  }

  private void updateChildren(Object element) {
    if ((element instanceof MTree || element instanceof MCell)
        && ((MUIElement) element).isVisible()) {
      IEclipseContext context = ((MContext) element).getContext();
      Object object = ((MContribution) element).getObject();

      synchronized (element) {
        if (object != null && context.getLocal(ChildrenContainer.class) == null) {
          @SuppressWarnings("unchecked")
          var container = (MElementContainer<MUIElement>) element;
          var childrenContainer = new ChildrenContainer(application, models, container);
          context.set(ChildrenContainer.class, childrenContainer);

          findMethods(object.getClass(), Children.class, false)
              .map(
                  method -> resolveRequestor(
                      object,
                      context,
                      method,
                      null,
                      true,
                      true,
                      result -> addChildren(result, method, childrenContainer)))
              .filter(Objects::nonNull)
              .filter(MethodRequestor::isResolved)
              .forEach(requestor -> {
                try {
                  requestor.execute();
                } catch (Exception e) {
                  log.log(Level.ERROR, e);
                }
              });
        }
      }
    }
  }

  private void addChildren(Object result, Method method, ChildrenContainer childrenContainer) {
    var snippetId = method.getAnnotation(Children.class).snippetId();

    try {
      List<ContextBuffer> children;

      if (result instanceof Stream<?>) {
        children = ((Stream<?>) result)
            .filter(ContextBuffer.class::isInstance)
            .map(ContextBuffer.class::cast)
            .collect(toList());

      } else if (result instanceof Collection<?>) {
        children = ((Collection<?>) result)
            .stream()
            .filter(ContextBuffer.class::isInstance)
            .map(ContextBuffer.class::cast)
            .collect(toList());

      } else {
        return;
      }

      childrenContainer.updateChildren(snippetId, children);
    } catch (Exception e) {
      log
          .log(
              Level.ERROR,
              new IllegalArgumentException("Failed to create children for " + snippetId, e));
    }
  }

  static Method findMethod(
      Class<?> currentClass,
      Class<? extends Annotation> qualifier,
      boolean statics) {
    return findMethods(currentClass, qualifier, statics).findFirst().orElse(null);
  }

  static Stream<Method> findMethods(
      Class<?> currentClass,
      Class<? extends Annotation> qualifier,
      boolean statics) {
    var stream = Arrays
        .stream(currentClass.getDeclaredMethods())
        .filter(
            method -> method.getAnnotation(qualifier) != null
                && Modifier.isStatic(method.getModifiers()) == statics);

    Class<?> superClass = currentClass.getSuperclass();
    if (superClass == null) {
      return stream;
    } else {
      return Stream.concat(stream, findMethods(superClass, qualifier, statics));
    }
  }

  static MethodRequestor resolveRequestor(
      Object receiver,
      IEclipseContext context,
      Method method,
      PrimaryObjectSupplier tempSupplier,
      boolean initial,
      boolean track) {
    return resolveRequestor(receiver, context, method, tempSupplier, initial, track, result -> {});
  }

  static MethodRequestor resolveRequestor(
      Object receiver,
      IEclipseContext context,
      Method method,
      PrimaryObjectSupplier tempSupplier,
      boolean initial,
      boolean track,
      Consumer<Object> invocationAction) {
    if (method == null) {
      return null;
    }

    PrimaryObjectSupplier objectSupplier = ContextObjectSupplier
        .getObjectSupplier(context, INJECTOR);

    MethodRequestor requestor = new CallbackMethodRequestor(
        method,
        INJECTOR,
        objectSupplier,
        tempSupplier,
        receiver,
        track,
        invocationAction);

    INJECTOR.resolveArguments(requestor, initial);
    if (!requestor.isResolved()) {
      return null;
    }

    return requestor;
  }

  static MethodRequestor resolveStaticRequestor(
      IEclipseContext context,
      Method method,
      PrimaryObjectSupplier tempSupplier,
      Consumer<Object> invocationAction) {
    if (method == null) {
      return null;
    }

    PrimaryObjectSupplier objectSupplier = ContextObjectSupplier
        .getObjectSupplier(context, INJECTOR);

    MethodRequestor requestor = new StaticMethodRequestor(
        method,
        INJECTOR,
        objectSupplier,
        tempSupplier,
        null,
        invocationAction);

    INJECTOR.resolveArguments(requestor, true);

    return requestor;
  }
}
