package uk.co.saiman.camera;

public interface CameraImage {
  int getWidth();

  int getHeight();

  double getRed(int x, int y);

  double getGreen(int x, int y);

  double getBlue(int x, int y);
}
