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

import static java.lang.String.format;
import static uk.co.saiman.log.Log.Level.ERROR;
import static uk.co.saiman.msapex.editor.Editor.cloneSnippet;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.log.Log;
import uk.co.saiman.msapex.editor.Editor;
import uk.co.saiman.msapex.editor.EditorProvider;
import uk.co.saiman.msapex.editor.EditorService;
import uk.co.saiman.msapex.experiment.i18n.ExperimentProperties;
import uk.co.saiman.observable.OwnedMessage;

/**
 * An editor addon which only provides a single type of composite editor for
 * experiment nodes.
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentEditorAddon implements EditorProvider {
  public static final String EDITOR_EXPERIMENT_PATH = "uk.co.saiman.msapex.editor.experiment.path";
  public static final String EDITOR_EXPERIMENT_NODE = "uk.co.saiman.msapex.editor.experiment.node";

  @Inject
  private Workspace workspace;
  @Inject
  private EditorService editorService;
  @Inject
  private EModelService modelService;
  @Inject
  private MApplication application;
  @Inject
  @Localize
  private ExperimentProperties text;
  @Inject
  private Log log;

  private final Map<ExperimentNode<?, ?>, Editor> editors = new HashMap<>();

  @PostConstruct
  synchronized void create() {
    modelService
        .findElements(application, MPart.class, EModelService.ANYWHERE, this::isExperimentEditor)
        .forEach(this::addEditor);

    editorService.registerProvider(this);

    workspace
        .events()
        .weakReference(this)
        .map(OwnedMessage::owner)
        .observe(ExperimentEditorAddon::updatePaths);
  }

  private synchronized void updatePaths() {
    editors.values().stream().map(Editor::getPart).forEach(this::updatePath);
  }

  private synchronized void updatePath(MPart editor) {
    ExperimentNode<?, ?> node = (ExperimentNode<?, ?>) editor
        .getTransientData()
        .get(EDITOR_EXPERIMENT_NODE);

    if (node.getWorkspace().filter(workspace::equals).isPresent()) {
      editor.getPersistedState().put(EDITOR_EXPERIMENT_PATH, ExperimentPath.of(node).toString());
    } else {
      editor.getPersistedState().remove(EDITOR_EXPERIMENT_PATH);
    }
  }

  private synchronized void addEditor(MPart part) {
    ExperimentPath path = ExperimentPath
        .fromString(part.getPersistedState().get(EDITOR_EXPERIMENT_PATH));

    ExperimentNode<?, ?> node;
    try {
      node = path.resolve(workspace);
    } catch (Exception e) {
      log.log(ERROR, format("Cannot load persisted editor at path %s", path), e);
      return;
    }

    part.getTransientData().put(EDITOR_EXPERIMENT_NODE, node);
    editors.put(node, Editor.overPart(part));
  }

  private boolean isExperimentEditor(MApplicationElement element) {
    return element instanceof MPart
        && ((MPart) element).getPersistedState().containsKey(EDITOR_EXPERIMENT_PATH);
  }

  @PreDestroy
  synchronized void destroy() {
    editorService.unregisterProvider(this);
  }

  @Override
  public String getContextKey() {
    return ExperimentNode.class.getName();
  }

  @Override
  public boolean isApplicable(Object contextValue) {
    return contextValue instanceof ExperimentNode<?, ?>;
  }

  @Override
  public synchronized Editor getEditorPart(Object contextValue) {
    ExperimentNode<?, ?> node = (ExperimentNode<?, ?>) contextValue;
    Editor editor = editors.get(node);
    if (editor == null || !editor.getPart().isToBeRendered()) {
      editor = loadNewEditor(node);
      editors.put(node, editor);
    }
    return editor;
  }

  private Editor loadNewEditor(ExperimentNode<?, ?> node) {
    MPart editor = cloneSnippet(ExperimentEditorPart.ID, modelService, application);
    editor.getPersistedState().put(EDITOR_EXPERIMENT_PATH, ExperimentPath.of(node).toString());
    editor.getTransientData().put(EDITOR_EXPERIMENT_NODE, node);
    return Editor.overPart(editor);
  }
}
