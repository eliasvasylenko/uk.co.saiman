package uk.co.saiman.msapex.camera;

import org.eclipse.e4.core.contexts.IEclipseContext;

import uk.co.saiman.camera.CameraConnection;
import uk.co.saiman.camera.CameraDevice;

public class CameraSelectionHelper {
  private CameraSelectionHelper() {}

  public static void selectCamera(IEclipseContext context, CameraDevice selection) {
    CameraDevice currentDevice = context.get(CameraDevice.class);
    CameraConnection currentConnection = context.get(CameraConnection.class);

    if (currentDevice == selection)
      return;

    context.modify(CameraConnection.class, null);
    context.modify(CameraDevice.class, selection);

    if (currentConnection != null && !currentConnection.isDisposed()) {
      currentConnection.dispose();
      context.modify(CameraConnection.class, selection.openConnection());
    }
  }
}
