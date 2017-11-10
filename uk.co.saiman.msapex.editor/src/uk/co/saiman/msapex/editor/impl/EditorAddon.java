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
 * This file is part of uk.co.saiman.msapex.editor.
 *
 * uk.co.saiman.msapex.editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.editor.impl;

import static java.util.Objects.requireNonNull;
import static org.eclipse.e4.ui.workbench.UIEvents.Context.TOPIC_CONTEXT;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.ELEMENT;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.NEW_VALUE;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.TYPE;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTypes.SET;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
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

import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.msapex.editor.EditorDescriptor;
import uk.co.saiman.msapex.editor.EditorPrototype;
import uk.co.saiman.msapex.editor.EditorProvider;
import uk.co.saiman.msapex.editor.EditorService;

public class EditorAddon {
  private static final String PART_STACK_ID = "uk.co.saiman.msapex.partstack.editor";
  public static final String PROVIDER_ID = "uk.co.saiman.msapex.editor.provider";

  @Inject
  private EPartService partService;

  @Inject
  private MApplication application;
  @Inject
  private EModelService modelService;

  @Inject
  private Log log;

  private final Map<String, EditorProvider> editorProviders = new LinkedHashMap<>();
  private final Map<MPart, Object> partResources = new HashMap<>();
  private final Map<Object, MPart> resourceEditorParts = new HashMap<>();
  private final Set<EditorDescriptor> editorPrecedence = new LinkedHashSet<>();

  @PostConstruct
  void initialize(IEclipseContext context) {
    context.set(EditorService.class, new EditorService() {
      @Override
      public Stream<EditorPrototype> getApplicableEditors(Object resource) {
        List<EditorDescriptor> existingPrecedence = new ArrayList<>(editorPrecedence);

        /*
         * TODO deal with precedence
         */

        return getEditors().filter(e -> e.isApplicable(resource)).map(
            e -> e.getPrototype(resource));
      }

      @Override
      public Stream<EditorDescriptor> getEditors() {
        return editorProviders.values().stream().flatMap(
            e -> e.getEditorPartIds().map(p -> new EditorDescriptorImpl(e, p)));
      }

      @Override
      public void registerProvider(EditorProvider provider) {
        editorProviders.put(provider.getId(), provider);
      }

      @Override
      public void unregisterProvider(EditorProvider provider) {
        editorProviders.remove(provider.getId());
      }
    });
  }

  protected void removeEditor(MCompositePart controller) {
    resourceEditorParts.remove(partResources.remove(controller));
  }

  protected <T> MPart createEditor(EditorDescriptor descriptor, Object data) {
    MPart editorPart = descriptor.getProvider().createEditorPart(descriptor.getPartId());

    editorPart.setDirty(true);
    editorPart.setCloseable(true);
    editorPart.getPersistedState().put(PROVIDER_ID, descriptor.getProvider().getId());

    partResources.put(editorPart, data);

    ((MPartStack) modelService.find(PART_STACK_ID, application)).getChildren().add(editorPart);

    partService.showPart(editorPart, PartState.CREATE);

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
        MPart parentPart = context.getParent().get(MPart.class);

        if (part != null && part.getPersistedState().containsKey(PROVIDER_ID)) {
          Object resource = partResources.get(part);
          EditorProvider provider = editorProviders.get(part.getPersistedState().get(PROVIDER_ID));
          resource = provider.initializeEditorPart(part, resource);
          resourceEditorParts.put(resource, part);
        } else if (parentPart != null && parentPart.getPersistedState().containsKey(PROVIDER_ID)) {
          prepareEditorChildPartContext(context);
        }
      }
    } catch (Exception e) {
      log.log(Level.ERROR, e);
    }
  }

  private void prepareEditorChildPartContext(IEclipseContext context) {
    /*
     * We don't want the child part to dirty itself, we want it to dirty the
     * container.
     */
    context.set(MDirtyable.class, context.getParent().get(MDirtyable.class));
  }

  public class EditorDescriptorImpl implements EditorDescriptor {
    private final EditorProvider provider;
    private final String partId;

    public EditorDescriptorImpl(EditorProvider provider, String partId) {
      this.provider = requireNonNull(provider);
      this.partId = requireNonNull(partId);
    }

    @Override
    public EditorProvider getProvider() {
      return provider;
    }

    @Override
    public String getIconUri() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public String getPartId() {
      return partId;
    }

    @Override
    public boolean equals(Object other) {
      if (other == this)
        return true;
      if (!(other instanceof EditorDescriptorImpl))
        return false;

      EditorDescriptorImpl that = (EditorDescriptorImpl) other;

      return this.provider == that.provider && Objects.equals(this.partId, that.partId);
    }

    @Override
    public int hashCode() {
      return provider.hashCode() ^ partId.hashCode();
    }

    @Override
    public boolean isApplicable(Object resource) {
      return provider.isEditorApplicable(partId, resource);
    }

    @Override
    public EditorPrototype getPrototype(Object resource) {
      return new EditorPrototypeImpl(resource, this);
    }
  }

  public class EditorPrototypeImpl implements EditorPrototype {
    private final Object resource;
    private final EditorDescriptor descriptor;

    public EditorPrototypeImpl(Object resource, EditorDescriptor descriptor) {
      this.resource = resource;
      this.descriptor = descriptor;
    }

    @Override
    public EditorDescriptor getDescriptor() {
      return descriptor;
    }

    @Override
    public Object getResource() {
      return resource;
    }

    @Override
    public MPart showPart() {
      /*
       * TODO this should only move the editor preference before other editors
       * which were actually applicable to the resource!!!
       * 
       * editorPrecedence.remove(getDescriptor());
       * editorPrecedence.add(getDescriptor());
       */

      MPart editorPart = getEditor();
      partService.activate(editorPart);
      return editorPart;
    }

    MPart getEditor() {
      return resourceEditorParts
          .computeIfAbsent(resource, r -> createEditor(getDescriptor(), resource));
    }
  }
}
