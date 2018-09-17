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

import static java.util.Arrays.asList;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MAddon;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.Result;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.msapex.editor.EditorProvider;
import uk.co.saiman.msapex.editor.EditorService;
import uk.co.saiman.reflection.token.TypeToken;

/**
 * An editor addon which only provides a single type of composite editor for
 * experiment nodes.
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentEditorAddon implements EditorProvider {
  public static final String EDITOR_RESULT_CLASS = "uk.co.saiman.msapex.editor.result.class";
  public static final String EDITOR_STATE_CLASS = "uk.co.saiman.msapex.editor.state.class";
  public static final String EDITOR_EXPERIMENT_PATH = "uk.co.saiman.msapex.editor.experiment.path";

  private static final String BUNDLE_CLASS = "bundleclass://";
  private static final String BUNDLE_CLASS_SEPARATOR = "/";

  private static final String REMOVE_ON_HIDE = "removeOnHide";
  private static final String SEGREGATED_DND = "segregatedDnD";
  private static final String TABS_LOCATION = "fx.stack.tabslocation";
  private static final String TABS_LOCATION_BOTTOM = "BOTTOM";

  class EditorSnippet {
    final String id;
    final String stateClass;
    final String resultClass;

    public EditorSnippet(String id, String stateClass, String resultClass) {
      this.id = id;
      this.stateClass = stateClass;
      this.resultClass = resultClass;
    }

    public boolean isCompatible(ExperimentNode<?, ?> experiment) {
      if (!isClassNameCompatible(stateClass, experiment.getType().getStateType())
          || !isClassNameCompatible(resultClass, experiment.getType().getResultType()))
        return false;

      return true;
    }

    private boolean isClassNameCompatible(String className, TypeToken<?> type) {
      if (className == null)
        return true;

      Class<?> erasedType = type.getErasedType();
      ClassLoader classLoader = erasedType.getClassLoader();

      if (classLoader == null)
        return false;

      try {
        Class<?> superClass = classLoader.loadClass(className);
        return superClass.isAssignableFrom(erasedType);
      } catch (ClassNotFoundException e) {
        return false;
      }
    }
  }

  private final Set<EditorSnippet> editorSnippets = new LinkedHashSet<>();

  @Inject
  private EditorService editorService;
  @Inject
  private EModelService modelService;
  @Inject
  private MApplication application;
  @Inject
  private Workspace workspace;
  @Inject
  private MAddon addon;
  @Inject
  @Localize
  private ExperimentProperties text;

  @PostConstruct
  void create() {
    for (MUIElement snippet : application.getSnippets()) {
      if (snippet instanceof MPart) {
        MPart part = (MPart) snippet;
        String stateClass = part.getPersistedState().get(EDITOR_STATE_CLASS);
        String resultClass = part.getPersistedState().get(EDITOR_RESULT_CLASS);

        stateClass = parseClassLocation(stateClass);
        resultClass = parseClassLocation(resultClass);

        if (stateClass != null || resultClass != null)
          editorSnippets.add(new EditorSnippet(part.getElementId(), stateClass, resultClass));
      }
    }

    editorService.registerProvider(this);
  }

  @PreDestroy
  void destroy() {
    editorService.unregisterProvider(this);
  }

  @Override
  public String getId() {
    return addon.getElementId();
  }

  @Override
  public Stream<String> getEditorPartIds() {
    return Stream.of(getId());
  }

  @Override
  public boolean isEditorApplicable(String editorId, Object resource) {
    if (!editorId.equals(getId()))
      return false; // I don't think this should ever happen though
    if (!(resource instanceof ExperimentNode<?, ?>))
      return false;

    ExperimentNode<?, ?> experiment = (ExperimentNode<?, ?>) resource;

    for (EditorSnippet part : editorSnippets)
      if (part.isCompatible(experiment))
        return true;

    return false;
  }

  private String parseClassLocation(String classLocation) {
    if (classLocation == null || !classLocation.startsWith(BUNDLE_CLASS))
      return null;

    String[] classLocationElements = classLocation
        .substring(BUNDLE_CLASS.length())
        .split(BUNDLE_CLASS_SEPARATOR);

    if (classLocationElements.length != 2)
      return null;

    return classLocationElements[1];
  }

  @Override
  public MPart createEditorPart(String id, Object resource) {
    MCompositePart part = modelService.createModelElement(MCompositePart.class);
    part.getTags().add(REMOVE_ON_HIDE);
    part.getTags().add(SEGREGATED_DND);
    part.setDirty(true);

    MPartStack partStack = modelService.createModelElement(MPartStack.class);
    partStack.getPersistedState().put(TABS_LOCATION, TABS_LOCATION_BOTTOM);
    part.getChildren().add(partStack);

    ExperimentNode<?, ?> experiment = (ExperimentNode<?, ?>) resource;
    for (EditorSnippet component : editorSnippets) {
      if (component.isCompatible(experiment)) {
        partStack.getChildren().add(loadSnippet(component.id, resource));
      }
    }

    return part;
  }

  private MPart loadSnippet(String id, Object resource) {
    MPart part = (MPart) modelService.cloneSnippet(application, id, null);
    modelService
        .findElements(part, null, MApplicationElement.class, asList("renameOnClone"))
        .stream()
        .forEach(e -> e.setElementId(e.getElementId() + ".clone"));
    return part;
  }

  @Override
  public Object loadEditorResource(MPart part) {
    String pathString = part.getPersistedState().get(EDITOR_EXPERIMENT_PATH);
    return ExperimentPath.fromString(pathString).resolve(workspace);
  }

  @Override
  public void initializeEditorPart(MPart part, Object resource) {
    ExperimentNode<?, ?> experiment = (ExperimentNode<?, ?>) resource;
    part.setLabel(experiment.getId());

    part.getPersistedState().put(EDITOR_EXPERIMENT_PATH, ExperimentPath.of(experiment).toString());

    // inject result and result data changes into context
    IEclipseContext context = part.getContext();
    context.set(ExperimentNode.class, experiment);
    context.set(Result.class, experiment.getResult());
    Class<?> resultType = experiment.getResult().getType().getErasedType();
    context.declareModifiable(resultType);

    experiment
        .getResult()
        .updates()
        .observe(
            o -> context
                .modify(
                    resultType.getName(),
                    (IContextFunction) (c, k) -> o.getValue().orElse(null)));
  }

  @Override
  public void initializeMissingResourceEditorPart(MPart part) {
    String pathString = part.getPersistedState().get(EDITOR_EXPERIMENT_PATH);
    if (pathString != null) {
      part.setLabel("Missing resource: " + pathString);
    } else {
      part.setLabel("Unknown resource");
    }
  }
}
