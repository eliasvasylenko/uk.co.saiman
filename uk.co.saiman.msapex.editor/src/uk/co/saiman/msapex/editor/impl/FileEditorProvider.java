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

import static uk.co.saiman.msapex.editor.impl.FileEditorAddon.EDITOR_FILE_NAME;
import static uk.co.saiman.msapex.editor.impl.FileEditorAddon.EDITOR_FILE_PATH_PATTERN;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import uk.co.saiman.msapex.editor.ClassEditorProvider;
import uk.co.saiman.msapex.editor.Editor;

public class FileEditorProvider implements ClassEditorProvider<Path> {
  private final MApplication application;
  private final MPart snippet;

  private final Map<Path, Editor> editors = new HashMap<>();

  public FileEditorProvider(MApplication application, MPart snippet) {
    this.application = application;
    this.snippet = snippet;
  }

  public String getId() {
    return snippet.getElementId();
  }

  @Override
  public Class<Path> getContextType() {
    return Path.class;
  }

  @Override
  public boolean isApplicableTyped(Path path) {
    String pattern = snippet.getPersistedState().get(EDITOR_FILE_PATH_PATTERN);
    PathMatcher matcher = path.getFileSystem().getPathMatcher("glob:" + pattern);
    return matcher.matches(path);
  }

  @Override
  public synchronized Editor getEditorPartTyped(Path path) {
    Editor editor = editors.get(path);
    if (editor == null || !editor.getPart().isToBeRendered()) {
      editor = loadNewEditor(path);
      editors.put(path, editor);
    }
    return editor;
  }

  private Editor loadNewEditor(Path path) {
    MPart editor = Editor.cloneSnippet(snippet, application);
    editor.getPersistedState().put(EDITOR_FILE_NAME, path.toString());
    editor.getTransientData().put(EDITOR_FILE_NAME, path);
    return new PartEditor(editor);
  }

  synchronized void addEditor(MPart part) {
    Path path = Paths.get(part.getPersistedState().get(EDITOR_FILE_NAME));
    part.getTransientData().put(EDITOR_FILE_NAME, path);
    editors.put(path, new PartEditor(part));
  }

  synchronized void removeEditor(MPart part) {
    editors.remove(part.getTransientData().get(EDITOR_FILE_NAME));
  }
}
