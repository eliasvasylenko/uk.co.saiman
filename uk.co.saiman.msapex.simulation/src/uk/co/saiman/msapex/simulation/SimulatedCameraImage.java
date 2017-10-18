package uk.co.saiman.msapex.simulation;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.function.BiFunction;

import uk.co.saiman.camera.CameraImage;
import uk.co.saiman.camera.CameraResolution;
import uk.co.saiman.simulation.instrument.SimulatedSampleImage;

final class SimulatedCameraImage implements CameraImage {
  private final SimulatedSampleImage image;
  private final CameraResolution resolution;
  private final int imageX;
  private final int imageY;
  private final boolean tickTock;
  private final double zoomX;
  private final double zoomY;

  public SimulatedCameraImage(
      SimulatedSampleImage image,
      CameraResolution resolution,
      int imageX,
      int imageY,
      double zoomX,
      double zoomY) {
    this.image = image;
    this.resolution = resolution;
    this.imageX = imageX;
    this.imageY = imageY;
    this.tickTock = isEvenSecond();
    this.zoomX = zoomX;
    this.zoomY = zoomY;
  }

  @Override
  public int getWidth() {
    return resolution.getWidth();
  }

  @Override
  public int getHeight() {
    return resolution.getHeight();
  }

  private double getColor(int viewX, int viewY, BiFunction<Integer, Integer, Double> color) {
    double offsetX = (viewX - resolution.getWidth() / 2) * zoomX;
    double offsetY = (viewY - resolution.getHeight() / 2) * zoomY;

    int sampleImageX = imageX + (int) Math.floor(offsetX);
    int sampleImageY = imageY + (int) Math.floor(offsetY);

    if (sampleImageX < image.getWidth() && sampleImageX >= 0 && sampleImageY < image.getHeight()
        && sampleImageY >= 0)
      return color.apply(sampleImageX, sampleImageY);
    else
      return 0.5;
  }

  @Override
  public double getRed(int x, int y) {
    if (tickTock && isInCircle(x, y))
      return 1;
    else
      return getColor(x, y, image::getRed);
  }

  private boolean isInCircle(int x, int y) {
    return sqrt(
        pow(x - (resolution.getWidth() / 2), 2) + pow(y - (resolution.getHeight() / 2), 2)) < 25;
  }

  private boolean isEvenSecond() {
    return (System.currentTimeMillis() / 1000) % 2 == 0;
  }

  @Override
  public double getGreen(int x, int y) {
    return getColor(x, y, image::getGreen);
  }

  @Override
  public double getBlue(int x, int y) {
    return getColor(x, y, image::getBlue);
  }
}
