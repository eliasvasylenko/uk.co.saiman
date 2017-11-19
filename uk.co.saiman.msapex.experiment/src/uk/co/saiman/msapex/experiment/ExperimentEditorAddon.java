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

import static java.util.Arrays.asList;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.MAddon;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentPath;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.Result;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.msapex.editor.EditorProvider;
import uk.co.saiman.msapex.editor.EditorService;

public class ExperimentEditorAddon implements EditorProvider {
  public static final String EDITOR_RESULT_CLASS = "uk.co.saiman.msapex.editor.result.class";
  public static final String EDITOR_EXPERIMENT_PATH = "uk.co.saiman.msapex.editor.experiment.path";
  private static final String BUNDLE_CLASS = "bundleclass://";
  private static final String BUNDLE_CLASS_SEPARATOR = "/";

  private final Map<String, MPart> editorParts = new LinkedHashMap<>();

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
        if (part.getPersistedState().containsKey(EDITOR_RESULT_CLASS)) {
          editorParts.put(part.getElementId(), part);
        }
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
    return editorParts.values().stream().map(MPart::getElementId);
  }

  @Override
  public boolean isEditorApplicable(String editorId, Object resource) {
    if (!(resource instanceof ExperimentNode<?, ?>))
      return false;

    MPart part = editorParts.get(editorId);
    ExperimentNode<?, ?> experiment = (ExperimentNode<?, ?>) resource;

    if (part != null && experiment != null) {
      String classLocation = part.getPersistedState().get(EDITOR_RESULT_CLASS);

      if (classLocation.startsWith(BUNDLE_CLASS)) {
        String[] classLocationElements = classLocation.substring(BUNDLE_CLASS.length()).split(
            BUNDLE_CLASS_SEPARATOR);

        if (classLocationElements.length == 2) {
          /*
           * If it is a superclass then we'll be able to find it from the same class
           * loader.
           */
          ClassLoader classLoader = experiment
              .getResult()
              .getType()
              .getErasedType()
              .getClassLoader();

          if (classLoader != null) {
            try {
              Class<?> superClass = classLoader.loadClass(classLocationElements[1]);
              return experiment.getResult().getType().isAssignableTo(superClass);
            } catch (ClassNotFoundException e) {}
          }
        }
      }
    }
    return false;
  }

  @Override
  public MPart createEditorPart(String id) {
    MPart part = (MPart) modelService.cloneSnippet(application, id, null);
    modelService
        .findElements(part, null, MApplicationElement.class, asList("renameOnClone"))
        .stream()
        .forEach(e -> e.setElementId(e.getElementId() + ".clone"));
    part.setDirty(true);
    return part;
  }

  public Object loadEditorResource(MPart part) {
    String pathString = part.getPersistedState().get(EDITOR_EXPERIMENT_PATH);
    return ExperimentPath.fromString(pathString).resolve(workspace);
  }

  @Override
  public void initializeEditorPart(MPart part, Object resource) {
    ExperimentNode<?, ?> experiment;
    experiment = (ExperimentNode<?, ?>) resource;
    part.getPersistedState().put(EDITOR_EXPERIMENT_PATH, ExperimentPath.of(experiment).toString());

    // inject result and result data changes into context
    part.getContext().set(ExperimentNode.class, experiment);
    Result<?> result = experiment.getResult();
    part.getContext().set(Result.class, experiment.getResult());
    Class<?> resultType = experiment.getResult().getType().getErasedType();
    result
        .observe(o -> part.getContext().modify(resultType.getName(), result.tryGet().orElse(null)));
  }
}
