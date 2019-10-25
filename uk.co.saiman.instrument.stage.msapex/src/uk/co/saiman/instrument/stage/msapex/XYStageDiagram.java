/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.instrument.stage.msapex.
 *
 * uk.co.saiman.instrument.stage.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.stage.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.stage.msapex;

import static uk.co.saiman.measurement.Units.metre;

import javax.measure.Unit;
import javax.measure.quantity.Length;

import javafx.geometry.BoundingBox;
import uk.co.saiman.instrument.stage.XYStage;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

public abstract class XYStageDiagram extends StageDiagram<XYCoordinate<Length>> {
  public XYStageDiagram(XYStage<?> stage) {
    this(stage, metre().micro().getUnit());
  }

  public XYStageDiagram(XYStage<?> stage, Unit<Length> unit) {
    super(stage, unit);

    XYCoordinate<Length> lower = getCoordinatesAtStageLocation(stage.getLowerBound()).to(unit);
    XYCoordinate<Length> upper = getCoordinatesAtStageLocation(stage.getUpperBound()).to(unit);

    getAnnotationLayer()
        .setMeasurementBounds(
            new BoundingBox(
                lower.getX().getValue().doubleValue(),
                lower.getY().getValue().doubleValue(),
                upper.getX().getValue().doubleValue() - lower.getX().getValue().doubleValue(),
                upper.getY().getValue().doubleValue() - lower.getY().getValue().doubleValue()));
  }

  @Override
  public XYStage<?> getStageDevice() {
    return (XYStage<?>) super.getStageDevice();
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
