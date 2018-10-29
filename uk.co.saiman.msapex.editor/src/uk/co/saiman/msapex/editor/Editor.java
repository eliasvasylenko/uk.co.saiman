package uk.co.saiman.msapex.editor;

import static java.util.Arrays.asList;

import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.ui.MSnippetContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import uk.co.saiman.msapex.editor.impl.PartEditor;

public interface Editor {
  String getLabel();

  String getDescription();

  String getIconURI();

  MPart getPart();

  static Editor overPart(MPart part) {
    return new PartEditor(part);
  }

  static Editor overSnippet(MPart part, MSnippetContainer container) {
    return overPart(cloneSnippet(part, container));
  }

  static Editor overSnippet(String id, EModelService modelService, MSnippetContainer container) {
    return overPart(cloneSnippet(id, modelService, container));
  }

  static MPart cloneSnippet(MPart snippet, MSnippetContainer container) {
    return cloneSnippet(
        snippet.getElementId(),
        snippet.getContext().get(EModelService.class),
        container);
  }

  static MPart cloneSnippet(String id, EModelService modelService, MSnippetContainer container) {
    MPart part = (MPart) modelService.cloneSnippet(container, id, null);
    modelService
        .findElements(part, null, MApplicationElement.class, asList("renameOnClone"))
        .stream()
        .forEach(e -> e.setElementId(e.getElementId() + ".clone"));
    return part;
  }
}
