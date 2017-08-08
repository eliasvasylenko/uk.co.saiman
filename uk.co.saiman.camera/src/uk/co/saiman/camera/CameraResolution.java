package uk.co.saiman.camera;

public interface CameraResolution {
  int getWidth();

  int getHeight();

  CameraDevice getCameraDevice();

  void selectResolution();
}
