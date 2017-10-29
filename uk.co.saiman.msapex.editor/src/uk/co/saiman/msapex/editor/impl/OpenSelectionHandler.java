package uk.co.saiman.msapex.editor.impl;

import static org.eclipse.e4.ui.services.IServiceConstants.ACTIVE_SELECTION;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;

import uk.co.saiman.eclipse.AdaptNamed;
import uk.co.saiman.eclipse.Localize;
import uk.co.saiman.msapex.editor.EditorProperties;
import uk.co.saiman.msapex.editor.EditorPrototype;
import uk.co.saiman.msapex.editor.EditorService;

public class OpenSelectionHandler {
  @CanExecute
  boolean canExecute(
      EditorService editorService,
      @Localize EditorProperties text,
      @Optional @AdaptNamed(ACTIVE_SELECTION) Object selection) {
    return selection != null && editorService.getApplicableEditors(selection).findAny().isPresent();
  }

  @Execute
  void execute(
      EditorService editorService,
      @Localize EditorProperties text,
      @AdaptNamed(ACTIVE_SELECTION) Object selection) {
    editorService.getApplicableEditors(selection).findFirst().ifPresent(EditorPrototype::showPart);
  }
}
