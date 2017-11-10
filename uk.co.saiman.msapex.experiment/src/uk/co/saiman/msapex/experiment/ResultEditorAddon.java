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

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.services.adapter.Adapter;
import org.eclipse.e4.ui.model.application.MAddon;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import uk.co.saiman.experiment.Result;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.msapex.editor.EditorProvider;
import uk.co.saiman.msapex.editor.EditorService;

public class ResultEditorAddon implements EditorProvider {
  public static final String EDITOR_RESULT_CLASS = "uk.co.saiman.msapex.editor.result.class";
  public static final String EDITOR_RESULT_TYPE = "uk.co.saiman.msapex.editor.result.type";
  public static final String EDITOR_RESULT_PATH = "uk.co.saiman.msapex.editor.result.path";

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
  private Adapter adapter;
  @Inject
  private MAddon addon;

  @PostConstruct
  void create() {
    System.out.println("create ResultEditorProvider");
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
    System.out.println(" isEditorApplicable? " + editorId + " for " + resource);

    MPart part = editorParts.get(editorId);
    Result<?> result = adapter.adapt(resource, Result.class);

    if (part != null && result != null) {
      String classLocation = part.getPersistedState().get(EDITOR_RESULT_CLASS);

      if (classLocation.startsWith("bundleclass://")) {
        System.out.println(classLocation);
        Class<?> editorClass = null; // TODO new URI(classLocation);
        return result.getType().isAssignableTo(editorClass);
      }
    }
    return false;
  }

  @Override
  public MPart createEditorPart(String ID) {
    return (MPart) modelService.cloneSnippet(application, ID, null);
  }

  @Override
  public Result<?> initializeEditorPart(MPart part, Object resource) {
    Result<?> result;
    Map<String, String> state = part.getPersistedState();
    if (resource == null) {
      String id = state.get(EDITOR_RESULT_TYPE);
      Path path = workspace.getRootPath().getFileSystem().getPath(state.get(EDITOR_RESULT_PATH));
      result = workspace
          .resolveNode(path)
          .getResult()
          .filter(r -> r.getType().getId().equals(id))
          .findAny()
          .get();
      System.out.println("   RESULT: " + result);
    } else {
      result = adapter.adapt(resource, Result.class);
      state.put(EDITOR_RESULT_TYPE, result.getType().getId());
      state.put(EDITOR_RESULT_PATH, result.getDataPath().toString());
    }

    // inject result and result data changes into context
    part.getContext().set(Result.class, result);
    Class<?> resultType = result.getType().getErasedType();
    result.observe(o -> System.out.println("o " + o + " == " + result.tryGet().orElse(null)));
    result
        .observe(o -> part.getContext().modify(resultType.getName(), result.tryGet().orElse(null)));

    return result;
  }
}
