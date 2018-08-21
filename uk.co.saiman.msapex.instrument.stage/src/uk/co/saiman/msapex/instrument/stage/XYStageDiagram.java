/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.msapex.instrument.stage.
 *
 * uk.co.saiman.msapex.instrument.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.instrument.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.instrument.stage;

import static uk.co.saiman.measurement.Units.metre;

import javax.measure.quantity.Length;

import javafx.geometry.BoundingBox;
import uk.co.saiman.instrument.stage.XYStage;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

/*-
 * 
 * Options:
 * 
 * - ONE stage diagram available per stage (highest rank is chosen, default provided with low rank)
 * - Stage device wired to a diagram via OSGi
 * 
 *     pros:
 *       straightforward to get the diagram for a stage
 *     cons:
 *       custom UI for selecting e.g. different MALDI plates
 * 
 */
public abstract class XYStageDiagram extends StageDiagram<XYCoordinate<Length>> {
  @Override
  public abstract XYStage getStageDevice();

  protected void initialize() {
    XYStage stageDevice = getStageDevice();
    initialize(metre().micro().getUnit());

    XYCoordinate<Length> lower = getCoordinatesAtStageLocation(stageDevice.getLowerBound());
    XYCoordinate<Length> upper = getCoordinatesAtStageLocation(stageDevice.getUpperBound());

    getAnnotationLayer()
        .setMeasurementBounds(
            new BoundingBox(
                lower.getX().getValue().doubleValue(),
                lower.getY().getValue().doubleValue(),
                upper.getX().getValue().doubleValue() - lower.getX().getValue().doubleValue(),
                upper.getY().getValue().doubleValue() - lower.getY().getValue().doubleValue()));
  }

  @Override
  public XYCoordinate<Length> getStageLocationAtCoordinates(XYCoordinate<Length> coordinates) {
    return coordinates;
  }

  @Override
  public XYCoordinate<Length> getCoordinatesAtStageLocation(XYCoordinate<Length> location) {
    return location;
  }
}
