package uk.co.saiman.eclipse.perspective.command;

import static uk.co.saiman.eclipse.perspective.command.PerspectiveCommandConstants.PERSPECTIVE_ID_PARAMETER;
import static uk.co.saiman.eclipse.perspective.command.PerspectiveCommandConstants.PERSPECTIVE_STACK_ID_PARAMETER;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;

import uk.co.saiman.eclipse.perspective.EPerspectiveService;
import uk.co.saiman.eclipse.perspective.IPerspectiveContainer;

public class ShowPerspectiveHandler {
  @Inject
  EPerspectiveService perspectiveService;

  @Execute
  public void execute(
      @Optional @Named(PERSPECTIVE_ID_PARAMETER) String perspectiveId,
      @Optional @Named(PERSPECTIVE_STACK_ID_PARAMETER) String perspectiveStackId) {
    IPerspectiveContainer perspectiveContainer;
    if (perspectiveStackId == null || perspectiveStackId.isBlank()) {
      perspectiveContainer = perspectiveService.getActiveContainer();
    } else {
      perspectiveContainer = perspectiveService.findContainer(perspectiveStackId);
    }

    if (perspectiveContainer == null) {
      return;
    }

    MPerspective perspective;
    if (perspectiveId == null || perspectiveId.isBlank()) {
      perspective = perspectiveContainer.getActivePerspective();
    } else {
      perspective = perspectiveContainer.findPerspective(perspectiveId);
    }

    if (perspective == null) {
      return;
    }

    perspectiveService.activatePerspective(perspective);
  }
}
