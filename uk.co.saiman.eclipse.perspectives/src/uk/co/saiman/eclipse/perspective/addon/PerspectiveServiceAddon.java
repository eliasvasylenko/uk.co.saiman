/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.eclipse.perspectives.
 *
 * uk.co.saiman.eclipse.perspectives is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.perspectives is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.perspective.addon;

import static org.eclipse.e4.ui.workbench.modeling.EModelService.ANYWHERE;
import static uk.co.saiman.eclipse.perspective.EPerspectiveService.PERSPECTIVE_SOURCE_SNIPPET;
import static uk.co.saiman.eclipse.perspective.EPerspectiveService.PERSPECTIVE_TARGET_STACK;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MSnippetContainer;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import uk.co.saiman.eclipse.perspective.EPerspectiveService;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

public class PerspectiveServiceAddon {
  private static final String CLONE_SUFFIX = ".clone";

  @Inject
  IEclipseContext context;

  @Inject
  EModelService modelService;

  @Inject
  MApplication application;

  @Inject
  Log log;

  @PostConstruct
  public void initialize() {
    context.set(EPerspectiveService.class.getName(), new ContextFunction() {
      @Override
      public EPerspectiveService compute(IEclipseContext context, String contextKey) {
        return ContextInjectionFactory.make(PerspectiveServiceImpl.class, context);
      }
    });

    var perspectiveStacks = modelService
        .findElements(application, MPerspectiveStack.class, ANYWHERE, element -> true);

    for (var perspectiveStack : perspectiveStacks) {
      MSnippetContainer snippets = getSnippetContainer(perspectiveStack);

      if (snippets != null) {
        for (var snippet : snippets.getSnippets()) {
          if (snippet != null && snippet instanceof MPerspective) {
            try {
              clonePerspectiveSnippet(
                  modelService,
                  snippets,
                  snippet.getElementId(),
                  perspectiveStack,
                  false);
            } catch (Exception e) {
              log.log(Level.ERROR, "Failed to clone perspective snippet " + snippet, e);
              return;
            }
          }
        }
      }

      if (perspectiveStack.getSelectedElement() == null) {
        perspectiveStack.setSelectedElement(perspectiveStack.getChildren().get(0));
      }
    }
  }

  public static void clonePerspectiveSnippet(
      EModelService modelService,
      MSnippetContainer snippets,
      String snippetId,
      MPerspectiveStack perspectiveStack,
      boolean replace) {
    if (snippets == null || snippetId == null || perspectiveStack == null) {
      return;
    }

    var existingIndex = -1;
    for (int i = 0; i < perspectiveStack.getChildren().size(); i++) {
      var existingPerspective = perspectiveStack.getChildren().get(i);
      if (Objects
          .equals(
              snippetId,
              existingPerspective.getPersistedState().get(PERSPECTIVE_SOURCE_SNIPPET))) {
        existingIndex = i;
        break;
      }
    }

    if (existingIndex >= 0 && !replace) {
      return;
    }

    MPerspective perspective = (MPerspective) modelService
        .cloneSnippet(snippets, snippetId, modelService.getTopLevelWindowFor(perspectiveStack));

    perspective.getPersistedState().put(PERSPECTIVE_SOURCE_SNIPPET, snippetId);
    perspective.setElementId(snippetId + CLONE_SUFFIX);

    if (perspective == null
        || !Objects
            .equals(
                perspectiveStack.getElementId(),
                perspective.getPersistedState().get(PERSPECTIVE_TARGET_STACK))) {
      return;
    }

    if (existingIndex >= 0) {
      boolean reselect = perspectiveStack
          .getSelectedElement() == perspectiveStack.getChildren().get(existingIndex);
      perspectiveStack.getChildren().set(existingIndex, perspective);
      if (reselect) {
        perspectiveStack.setSelectedElement(perspective);
      }
    } else {
      perspectiveStack.getChildren().add(perspective);
    }
  }

  public static MPerspectiveStack getPerspectiveStack(MPerspective perspective) {
    if (perspective == null) {
      return null;
    }
    MElementContainer<?> container = perspective.getParent();
    while (!(container instanceof MPerspectiveStack) && container != null) {
      container = container.getParent();
    }
    return (MPerspectiveStack) container;
  }

  public static MSnippetContainer getSnippetContainer(MPerspectiveStack perspectiveStack) {
    if (perspectiveStack == null) {
      return null;
    }
    MElementContainer<?> container = perspectiveStack.getParent();
    while (!(container instanceof MSnippetContainer) && container != null) {
      container = container.getParent();
    }
    return (MSnippetContainer) container;
  }
}
