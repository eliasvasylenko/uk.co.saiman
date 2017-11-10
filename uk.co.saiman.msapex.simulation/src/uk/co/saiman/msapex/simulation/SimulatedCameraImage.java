/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.msapex.simulation.
 *
 * uk.co.saiman.msapex.simulation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.simulation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
