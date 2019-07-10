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
 * This file is part of uk.co.saiman.maldi.stage.
 *
 * uk.co.saiman.maldi.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.stage.msapex;

import static uk.co.saiman.measurement.Units.metre;

import java.util.stream.Stream;

import javax.measure.quantity.Length;

import javafx.geometry.BoundingBox;
import uk.co.saiman.instrument.stage.msapex.StageDiagram;
import uk.co.saiman.instrument.stage.msapex.StageDiagramSampleConfiguration;
import uk.co.saiman.maldi.stage.SampleArea;
import uk.co.saiman.maldi.stage.SamplePlateStage;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

public class MaldiStageDiagram extends StageDiagram<SampleArea> {
  private final SamplePlateStage stage;

  public MaldiStageDiagram(SamplePlateStage stage) {
    this.stage = stage;

    SamplePlateStage stageDevice = getStageDevice();
    initialize(metre().micro().getUnit());

    XYCoordinate<Length> lower = getCoordinatesAtLocation(stageDevice.getLowerBound());
    XYCoordinate<Length> upper = getCoordinatesAtLocation(stageDevice.getUpperBound());

    getAnnotationLayer()
        .setMeasurementBounds(
            new BoundingBox(
                lower.getX().getValue().doubleValue(),
                lower.getY().getValue().doubleValue(),
                upper.getX().getValue().doubleValue() - lower.getX().getValue().doubleValue(),
                upper.getY().getValue().doubleValue() - lower.getY().getValue().doubleValue()));
  }

  @Override
  public SamplePlateStage getStageDevice() {
    return stage;
  }

  @Override
  public Stream<? extends StageDiagramSampleConfiguration> getSampleConfigurations() {
    return Stream.empty();
  }

  @Override
  public SampleArea getStageLocationAtCoordinates(XYCoordinate<Length> coordinates) {
    throw new UnsupportedOperationException();
  }

  @Override
  public XYCoordinate<Length> getCoordinatesAtStageLocation(SampleArea location) {
    return getCoordinatesAtLocation(location.center());
  }

  public XYCoordinate<Length> getCoordinatesAtLocation(XYCoordinate<Length> location) {
    return location;
  }
}
