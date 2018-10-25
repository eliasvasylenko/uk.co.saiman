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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.Result;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.msapex.editor.Editor;
import uk.co.saiman.msapex.editor.EditorProvider;
import uk.co.saiman.msapex.editor.EditorService;
import uk.co.saiman.msapex.experiment.i18n.ExperimentProperties;

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

  private static final String SEGREGATED_DND = "segregatedDnD";
  private static final String TABS_LOCATION = "fx.stack.tabslocation";
  private static final String TABS_LOCATION_BOTTOM = "BOTTOM";

  @Inject
  private EditorService editorService;
  @Inject
  private EModelService modelService;
  @Inject
  private MApplication application;
  @Inject
  private Workspace workspace;
  @Inject
  @Localize
  private ExperimentProperties text;

  @PostConstruct
  void create() {
    for (MUIElement snippet : application.getSnippets()) {
      if (snippet instanceof MPart) {
        MPart part = (MPart) snippet;

      }
    }

    editorService.registerProvider(this);
  }

  @PreDestroy
  void destroy() {
    editorService.unregisterProvider(this);
  }

  public MPart getEditorPart(String id, Object resource) {
    MCompositePart part = modelService.createModelElement(MCompositePart.class);
    part.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
    part.getTags().add(SEGREGATED_DND);
    part.setDirty(true);

    MPartStack partStack = modelService.createModelElement(MPartStack.class);
    partStack.getPersistedState().put(TABS_LOCATION, TABS_LOCATION_BOTTOM);
    part.getChildren().add(partStack);

    ExperimentNode<?, ?> experiment = (ExperimentNode<?, ?>) resource;

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

  public Object loadEditorResource(MPart part) {
    String pathString = part.getPersistedState().get(EDITOR_EXPERIMENT_PATH);
    return ExperimentPath.fromString(pathString).resolve(workspace);
  }

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
  public String getContextKey() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isApplicable(Object contextValue) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Editor getEditorPart(Object contextValue) {
    // TODO Auto-generated method stub
    return null;
  }
}
