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
 * This file is part of uk.co.saiman.saint.
 *
 * uk.co.saiman.saint is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.saint is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.saint;

import java.util.Collection;
import java.util.stream.Stream;

import javax.measure.Quantity;

import uk.co.saiman.msapex.instrument.stage.StageDiagramSampleConfiguration;
import uk.co.saiman.msapex.instrument.stage.XYStageDiagram;

public class SaintStageDiagram extends XYStageDiagram {
  

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
