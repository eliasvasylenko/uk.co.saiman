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

import java.util.Collection;
import java.util.stream.Stream;

import javax.measure.Quantity;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import uk.co.saiman.msapex.instrument.stage.StageDiagram;
import uk.co.saiman.msapex.instrument.stage.StageDiagramSampleConfiguration;

public class SimulatedXYStageDiagram extends StageDiagram {
  private static final WritableImage DEFAULT_IMAGE = new WritableImage(100, 100);
  static {
    PixelWriter writer = DEFAULT_IMAGE.getPixelWriter();
    Color color = Color.gray(0.5);
    for (int i = 0; i < 100; i++)
      for (int j = 0; j < 100; j++)
        writer.setColor(i, j, color);
  }

  public SimulatedXYStageDiagram() {
    resetImage();
  }

  public void resetImage() {
    setImage(DEFAULT_IMAGE);
  }

  @Override
  public Stream<Quantity<?>> getCoordinatesAtPixel(int pixelX, int pixelY) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getPixelXAtCoordinates(Collection<? extends Quantity<?>> coordinates) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getPixelYAtCoordinates(Collection<? extends Quantity<?>> coordinates) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Stream<? extends StageDiagramSampleConfiguration> getSampleConfigurations() {
    // TODO Auto-generated method stub
    return null;
  }
}
