/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.msapex.experiment.
 *
 * uk.co.saiman.msapex.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment;

import static org.eclipse.e4.ui.workbench.UIEvents.Context.TOPIC_CONTEXT;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.ELEMENT;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.NEW_VALUE;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.TYPE;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTypes.SET;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MHandlerContainer;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.osgi.service.event.Event;

import uk.co.saiman.experiment.Result;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

public class ExperimentEditorAddon {
  static final String PART_ID = "uk.co.saiman.msapex.experiment.compositepart.resulteditor";
  static final String PART_STACK_ID = "uk.co.saiman.msapex.partstack.editor";
  private static final String MANAGED_EDITOR_RESOURCE = "uk.co.saiman.msapex.editor.resource";

  @Inject
  private EPartService partService;
  @Inject
  private IEventBroker eventBroker;

  @Inject
  private MApplication application;
  @Inject
  private EModelService modelService;

  @Inject
  private Log log;

  private final Map<MCompositePart, Result<?>> partResults = new HashMap<>();
  private final Map<Result<?>, MCompositePart> editorParts = new HashMap<>();

  @PostConstruct
  void initialize(IEclipseContext context) {
    context.set(ExperimentEditorManager.class, new ExperimentEditorManager() {
      @Override
      public synchronized <T> MCompositePart openEditor(Result<T> result) {
        MCompositePart editorPart = editorParts.computeIfAbsent(result, r -> createEditor(result));
        partService.activate(editorPart);
        return editorPart;
      }
    });
  }

  protected void removeEditor(MCompositePart controller) {
    editorParts.remove(partResults.remove(controller));
  }

  protected <T> MCompositePart createEditor(Result<T> data) {
    MCompositePart editorPart = (MCompositePart) modelService
        .cloneSnippet(application, PART_ID, null);
    editorPart.setDirty(true);
    editorPart.setCloseable(true);
    editorPart
        .getPersistedState()
        .put(MANAGED_EDITOR_RESOURCE, data.getResultDataPath().toString());

    partResults.put(editorPart, data);

    ((MPartStack) modelService.find(PART_STACK_ID, application)).getChildren().add(editorPart);

    partService.showPart(editorPart, PartState.CREATE);
    editorPart.getContext().set(Result.class, data);

    return editorPart;
  }

  /**
   * Watch for context creation events so we can inject into the part contexts
   * before the UI is created.
   * 
   * @param event
   *          the event which may be a context creation event
   */
  @Inject
  @Optional
  private synchronized void initializeEditorContext(@UIEventTopic(TOPIC_CONTEXT) Event event) {
    try {
      if (event.getProperty(ELEMENT) instanceof MHandlerContainer
          && SET.equals(event.getProperty(TYPE))) {
        IEclipseContext context = (IEclipseContext) event.getProperty(NEW_VALUE);

        MPart part = context.get(MPart.class);
        String resourceId = part.getPersistedState().get(MANAGED_EDITOR_RESOURCE);

        if (resourceId != null) {
          Result<?> result = partResults.get(part);

          System.out.println("[REM] initializing part: " + part.getElementId() + " " + resourceId);

          prepareContainerPartContext(context, result);
        } else {
          MPart parentPart = context.getParent().get(MPart.class);
          if (parentPart != null && PART_ID.equals(parentPart.getElementId())) {
            prepareContributionPartContext(context);
          }
        }
      }
    } catch (Exception e) {
      log.log(Level.ERROR, e);
    }
  }

  private void prepareContributionPartContext(IEclipseContext context) {
    /*
     * We don't want the child part to dirty itself, we want it to dirty the
     * container.
     */
    context.set(MDirtyable.class, context.getParent().get(MDirtyable.class));
  }

  private void prepareContainerPartContext(IEclipseContext context, Result<?> result) {
    context.set(Result.class, result);
  }
}
