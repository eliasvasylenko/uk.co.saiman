package uk.co.saiman.eclipse.perspective;

import java.util.List;

import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;

public interface IPerspectiveContainer {
  MPerspectiveStack getPerspectiveStack();

  List<MPerspective> getPerspectives();

  MPerspective getActivePerspective();

  MPerspective findPerspective(String perspectiveId);

  void resetPerspective(String perspectiveId);

  void activatePerspective(String perspectiveId);
}
