package uk.co.saiman.eclipse.perspective;

import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;

public interface EPerspectiveService {
  public static final String PERSPECTIVE_SOURCE_SNIPPET = "PerspectiveSourceSnippet";
  public static final String PERSPECTIVE_TARGET_STACK = "PerspectiveTargetStack";

  /*
   * TODO when we drag a part out of a perspective into a new window with the dnd
   * addon, we need to remember which perspective the new window is associated
   * with and hide it when the perspective is changed. Ideally we want to find a
   * way to do this without any coupling between this and the dnd service...
   */

  IPerspectiveContainer getActiveContainer();

  IPerspectiveContainer findContainer(String perspectiveStackId);

  MPerspective findPerspective(String perspectiveId);

  void activatePerspective(MPerspective perspective);

  void resetPerspective(MPerspective perspective);
}
