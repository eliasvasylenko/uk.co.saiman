/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import javax.measure.Unit;
import javax.measure.quantity.Length;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.BoundingBox;
import uk.co.saiman.experiment.sampleplate.SamplePlate;
import uk.co.saiman.instrument.stage.sampleplate.SamplePlateStage;
import uk.co.saiman.measurement.coordinate.XYCoordinate;
import uk.co.saiman.msapex.annotations.AnnotatedImage;

/**
 * A visualization of a sample plate.
 * 
 * @author Elias N Vasylenko
 */
public abstract class SamplePlateDiagram extends AnnotatedImage<Length, Length> {
  private final SamplePlateStage<?, ?> stage;
  private final Unit<Length> unit;

  private final ObjectProperty<SamplePlate> samplePlate;

  protected SamplePlateDiagram(SamplePlateStage<?, ?> stage, Unit<Length> unit) {
    super(unit, unit);
    this.stage = stage;
    this.unit = unit;

    this.samplePlate = new SimpleObjectProperty<>();
    this.samplePlate.addListener(plate -> updateSamplePlate(this.samplePlate.get()));

    getAnnotationLayer()
        .getAnnotations()
        .add(new ActualXYPositionAnnotation(stage.underlyingStage()));
    getAnnotationLayer()
        .getAnnotations()
        .add(new RequestedXYPositionAnnotation(stage.underlyingStage()));
  }

  public SamplePlateStage<?, ?> getStage() {
    return stage;
  }

  public Unit<Length> getUnit() {
    return unit;
  }

  public ObjectProperty<SamplePlate> samplePlateProperty() {
    return samplePlate;
  }

  public SamplePlate getSamplePlate() {
    return samplePlate.get();
  }

  public void setSamplePlate(SamplePlate value) {
    samplePlate.set(value);
  }

  protected void updateSamplePlate(SamplePlate samplePlate) {
    if (samplePlate != null) {
      updateSamplePlateBounds(samplePlate.lowerBound().to(unit), samplePlate.upperBound().to(unit));
    }
  }

  protected void updateSamplePlateBounds(XYCoordinate<Length> lower, XYCoordinate<Length> upper) {
    getAnnotationLayer()
        .setMeasurementBounds(
            new BoundingBox(
                lower.getX().getValue().doubleValue(),
                lower.getY().getValue().doubleValue(),
                upper.getX().getValue().doubleValue() - lower.getX().getValue().doubleValue(),
                upper.getY().getValue().doubleValue() - lower.getY().getValue().doubleValue()));
  }
}
