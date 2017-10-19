package uk.co.saiman.msapex.editor;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public interface EditorService {
  MPart openEditor(String partId, Object data);
}
