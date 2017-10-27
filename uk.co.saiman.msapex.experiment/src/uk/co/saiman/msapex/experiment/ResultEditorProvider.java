package uk.co.saiman.msapex.experiment;

import java.nio.file.Path;
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

import uk.co.saiman.experiment.Result;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.msapex.editor.EditorProvider;
import uk.co.saiman.msapex.editor.EditorService;

public class ResultEditorProvider implements EditorProvider {
  public static final String EDITOR_RESULT_CLASS = "uk.co.saiman.msapex.editor.result.class";
  public static final String EDITOR_RESULT_TYPE = "uk.co.saiman.msapex.editor.result.type";
  public static final String EDITOR_RESULT_PATH = "uk.co.saiman.msapex.editor.result.path";

  private final Map<String, MPart> editorParts = new LinkedHashMap<>();

  @Inject
  EditorService editorService;
  @Inject
  EModelService modelService;
  @Inject
  MApplication application;
  @Inject
  Workspace workspace;

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
  public Stream<String> getEditorPartIds() {
    return editorParts.values().stream().map(MPart::getElementId);
  }

  @Override
  public boolean isEditorApplicable(String editorId, Object resource) {
    MPart part = editorParts.get(editorId);
    if (part != null && resource instanceof Result<?>) {
      Result<?> result = (Result<?>) resource;
      String classLocation = part.getPersistedState().get(EDITOR_RESULT_CLASS);

      if (classLocation.startsWith("bundleclass://")) {
        System.out.println(classLocation);
        Class<?> editorClass = null;// TODO new URI(classLocation);
        return editorClass.isInstance(resource);
      }
    }
    return false;
  }

  @Override
  public MPart getEditorPart(String ID) {
    MPart part = (MPart) modelService.cloneSnippet(application, ID, null);

    return part;
  }

  @Override
  public Map<String, String> persistResource(Object resource) {
    Map<String, String> data = new HashMap<>();
    Result<?> result = (Result<?>) resource;
    data.put(EDITOR_RESULT_TYPE, result.getType().getId());
    data.put(EDITOR_RESULT_PATH, result.getDataPath().toString());
    return data;
  }

  @Override
  public Object resolveResource(Map<String, String> persistentData) {
    String id = persistentData.get(EDITOR_RESULT_TYPE);
    Path path = Paths.get(persistentData.get(EDITOR_RESULT_PATH));
    return workspace
        .resolveNode(path)
        .getResults()
        .filter(r -> r.getType().getId().equals(id))
        .findAny()
        .get();
  }
}
