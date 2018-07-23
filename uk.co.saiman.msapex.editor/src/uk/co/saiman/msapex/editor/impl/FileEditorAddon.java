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

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
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

import uk.co.saiman.msapex.editor.EditorProvider;
import uk.co.saiman.msapex.editor.EditorService;

public class FileEditorAddon implements EditorProvider {
  /**
   * The persisted file name of a resource for an active editor.
   */
  public static final String EDITOR_FILE_NAME = "uk.co.saiman.msapex.editor.file.name";
  /**
   * The persistent data key for the Unix-style glob pattern which determines
   * applicability of an editor to a resource path.
   */
  public static final String EDITOR_FILE_PATH_PATTERN = "uk.co.saiman.msapex.editor.file.path.pattern";

  private final Map<String, MPart> editorParts = new LinkedHashMap<>();

  @Inject
  private EditorService editorService;
  @Inject
  private EModelService modelService;
  @Inject
  private MApplication application;
  @Inject
  private Adapter adapter;
  @Inject
  private MAddon addon;

  @PostConstruct
  void create() {
    for (MUIElement snippet : application.getSnippets()) {
      if (snippet instanceof MPart) {
        MPart part = (MPart) snippet;
        if (part.getPersistedState().containsKey(EDITOR_FILE_PATH_PATTERN)) {
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
    MPart part = editorParts.get(editorId);
    if (part != null && resource instanceof Path) {
      Path path = (Path) resource;
      String pattern = part.getPersistedState().get(EDITOR_FILE_PATH_PATTERN);

      PathMatcher matcher = path.getFileSystem().getPathMatcher("glob:" + pattern);

      return matcher.matches(path);
    }
    return false;
  }

  @Override
  public MPart createEditorPart(String id, Object resource) {
    MPart part = (MPart) modelService.cloneSnippet(application, id, null);
    part.getPersistedState().remove(EDITOR_FILE_PATH_PATTERN);
    return part;
  }

  @Override
  public Object loadEditorResource(MPart part) {
    return Paths.get(part.getPersistedState().get(EDITOR_FILE_NAME));
  }

  @Override
  public void initializeEditorPart(MPart part, Object resource) {
    Path path = adapter.adapt(resource, Path.class);
    part.getPersistedState().put(EDITOR_FILE_NAME, path.toString());
  }

  @Override
  public void initializeMissingResourceEditorPart(MPart part) {
    String missingResource = part.getPersistedState().get(EDITOR_FILE_NAME);
  }
}
