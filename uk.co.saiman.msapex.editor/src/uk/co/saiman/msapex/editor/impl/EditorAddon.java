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
import static org.eclipse.e4.ui.workbench.UIEvents.UIElement.TOPIC_TOBERENDERED;

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
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.UIEvents;
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

public class EditorAddon implements EditorService {
  private static final String PART_STACK_ID = "uk.co.saiman.msapex.partstack.editor";
  public static final String PROVIDER_ID = "uk.co.saiman.msapex.editor.provider";

  @Inject
  private MApplication application;
  @Inject
  private EModelService modelService;
  @Inject
  private EPartService partService;
  @Inject
  private Log log;

  private final Set<EditorDescriptor> editorPrecedence = new LinkedHashSet<>();

  private final Map<String, List<MPart>> orphanedEditors = new HashMap<>();
  private final Map<String, EditorProvider> editorProviders = new LinkedHashMap<>();

  private final Map<MPart, Object> partResources = new HashMap<>();
  private final Map<Object, MPart> resourceParts = new HashMap<>();

  @PostConstruct
  void initialize(IEclipseContext context) {
    List<MPart> persistedParts = modelService
        .findElements(
            application,
            MPart.class,
            EModelService.ANYWHERE,
            part -> isEditor((MPart) part));

    persistedParts.forEach(part -> {
      String provider = part.getPersistedState().get(PROVIDER_ID);
      orphanedEditors.computeIfAbsent(provider, k -> new ArrayList<>()).add(part);
    });

    context.set(EditorService.class, this);
  }

  protected <T> MPart createEditor(EditorDescriptor descriptor, Object data) {
    MPart editorPart = descriptor.getProvider().createEditorPart(descriptor.getPartId(), data);

    editorPart.setCloseable(true);
    editorPart.getPersistedState().put(PROVIDER_ID, descriptor.getProvider().getId());

    partResources.put(editorPart, data);

    ((MPartStack) modelService.find(PART_STACK_ID, application)).getChildren().add(editorPart);

    partService.showPart(editorPart, PartState.CREATE);

    return editorPart;
  }

  /**
   * Watch for part close events so we can clean up after the editors.
   */
  @Inject
  @Optional
  private synchronized void partCloseListener(@UIEventTopic(TOPIC_TOBERENDERED) Event event) {
    Object part = event.getProperty(UIEvents.EventTags.ELEMENT);
    boolean toBeRendered = (Boolean) event.getProperty(UIEvents.EventTags.NEW_VALUE);
    if (part instanceof MPart && !toBeRendered && isEditor((MPart) part)) {
      resourceParts.remove(partResources.remove(part));
    }
  }

  /**
   * Watch for context creation events so we can inject into the part contexts
   * before the UI is created.
   */
  @Inject
  @Optional
  private synchronized void partContextListener(@UIEventTopic(TOPIC_CONTEXT) Event event) {
    try {
      Object value = event.getProperty(NEW_VALUE);
      if (event.getProperty(ELEMENT) instanceof MHandlerContainer
          && value instanceof IEclipseContext
          && SET.equals(event.getProperty(TYPE))) {
        IEclipseContext context = (IEclipseContext) value;

        MPart part = context.get(MPart.class);
        MPart parentPart = context.getParent().get(MPart.class);

        if (isEditor(part)) {
          prepareEditorPartContext(part);
        } else if (isEditor(parentPart)) {
          prepareEditorChildPartContext(context);
        }
      }
    } catch (Exception e) {
      log.log(Level.ERROR, e);
    }
  }

  private void prepareEditorPartContext(MPart part) {
    Object resource = partResources.get(part);
    EditorProvider provider = editorProviders.get(part.getPersistedState().get(PROVIDER_ID));
    provider.initializeEditorPart(part, resource);
  }

  private void prepareEditorChildPartContext(IEclipseContext context) {
    /*
     * We don't want the child part to dirty itself, we want it to dirty the
     * container.
     */
    context.set(MDirtyable.class, context.getParent().get(MDirtyable.class));
  }

  @Override
  public Stream<EditorPrototype> getApplicableEditors(Object resource) {
    List<EditorDescriptor> existingPrecedence = new ArrayList<>(editorPrecedence);

    /*
     * TODO deal with precedence
     */
    return getEditors().filter(e -> e.isApplicable(resource)).map(e -> e.getPrototype(resource));
  }

  @Override
  public Stream<EditorDescriptor> getEditors() {
    return editorProviders
        .values()
        .stream()
        .flatMap(e -> e.getEditorPartIds().map(p -> new EditorDescriptorImpl(e, p)));
  }

  @Override
  public void registerProvider(EditorProvider provider) {
    editorProviders.put(provider.getId(), provider);
    List<MPart> orphans = orphanedEditors.remove(provider.getId());
    if (orphans != null)
      for (MPart part : orphans) {
        Object resource = provider.loadEditorResource(part);
        partResources.put(part, resource);
        resourceParts.put(resource, part);
      }
  }

  @Override
  public void unregisterProvider(EditorProvider provider) {
    editorProviders.remove(provider.getId());
  }

  @Override
  public boolean isEditor(MPart part) {
    return part != null && part.getPersistedState().containsKey(PROVIDER_ID);
  }

  @Override
  public Object getResource(MPart part) {
    return partResources.get(part);
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

    @Override
    public String toString() {
      return getProvider().getId() + "/" + getPartId();
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
    public MPart openEditor() {
      /*
       * TODO this should only move the editor preference before other editors which
       * were actually applicable to the resource!!!
       * 
       * editorPrecedence.remove(getDescriptor());
       * editorPrecedence.add(getDescriptor());
       */

      MPart editorPart = getEditor();
      partService.activate(editorPart);
      return editorPart;
    }

    MPart getEditor() {
      return resourceParts.computeIfAbsent(resource, r -> createEditor(getDescriptor(), resource));
    }
  }
}
