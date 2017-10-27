package uk.co.saiman.msapex.editor.impl;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

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
  EditorService editorService;
  @Inject
  EModelService modelService;
  @Inject
  MApplication application;

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
  public MPart getEditorPart(String ID) {
    MPart part = (MPart) modelService.cloneSnippet(application, ID, null);
    part.getPersistedState().remove(EDITOR_FILE_PATH_PATTERN);
    return part;
  }

  @Override
  public Map<String, String> persistResource(Object resource) {
    Map<String, String> data = new HashMap<>();
    data.put(EDITOR_FILE_NAME, ((Path) resource).toString());
    return data;
  }

  @Override
  public Object resolveResource(Map<String, String> persistentData) {
    return Paths.get(persistentData.get(EDITOR_FILE_NAME));
  }
}
