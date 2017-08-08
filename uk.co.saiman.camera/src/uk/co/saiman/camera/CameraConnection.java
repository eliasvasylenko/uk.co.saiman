package uk.co.saiman.camera;

import uk.co.strangeskies.observable.Observable;

public interface CameraConnection {
  CameraDevice getDevice();

  void dispose();

  CameraImage getImage();

  Observable<CameraImage> getImageStream();

  CameraResolution[] getAvailableResolutions();

  CameraResolution getResolution();
}
