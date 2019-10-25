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

import static javafx.beans.binding.Bindings.createObjectBinding;

import javax.measure.Unit;
import javax.measure.quantity.Length;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import uk.co.saiman.instrument.sample.Analysis;
import uk.co.saiman.instrument.sample.RequestedSampleState;
import uk.co.saiman.instrument.stage.Stage;
import uk.co.saiman.measurement.coordinate.XYCoordinate;
import uk.co.saiman.msapex.annotations.AnnotationLayer;

/**
 * A visualization of the sample area of a stage mapped into cartesian
 * coordinates.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the coordinate type of the stage
 */
public abstract class StageDiagram<T> extends StackPane {
  private final Stage<T, ?> stage;
  private final ImageView imageView;
  private final AnnotationLayer<Length, Length> annotationLayer;

  private StagePositionAnnotation requestedPosition;
  private StagePositionAnnotation actualPosition;
  private Unit<Length> unit;

  protected StageDiagram(Stage<T, ?> stage, Unit<Length> unit) {
    this.stage = stage;
    this.imageView = new ImageView();
    this.annotationLayer = new AnnotationLayer<>(unit, unit);
    this.unit = unit;

    getChildren().add(imageView);
    getChildren().add(annotationLayer);

    requestedPosition = new StagePositionAnnotation();
    annotationLayer.getAnnotations().add(requestedPosition);
    stage
        .requestedSampleState()
        .optionalValue()
        .observe(o -> o.ifPresentOrElse(this::setRequestedState, this::unsetRequestedState));

    actualPosition = new StagePositionAnnotation();
    annotationLayer.getAnnotations().add(actualPosition);
    stage
        .samplePosition()
        .optionalValue()
        .observe(o -> o.ifPresentOrElse(this::setPosition, this::unsetPosition));

    imageView.fitWidthProperty().bind(annotationLayer.widthProperty());
    imageView.fitHeightProperty().bind(annotationLayer.heightProperty());
    imageView.layoutXProperty().bind(annotationLayer.layoutXProperty());
    imageView.layoutYProperty().bind(annotationLayer.layoutYProperty());
    imageView.setPreserveRatio(false);
    imageView.setManaged(false);
  }

  public Unit<Length> getUnit() {
    return unit;
  }

  public void setRequestedState(RequestedSampleState<T> requestedState) {
    if (requestedState instanceof Analysis<?>) {
      T position = ((Analysis<T>) requestedState).position();
      requestedPosition.setMeasurementX(getCoordinatesAtStageLocation(position).getX());
      requestedPosition.setMeasurementY(getCoordinatesAtStageLocation(position).getY());
      requestedPosition.setVisible(true);
    } else {
      requestedPosition.setVisible(false);
    }
  }

  public void unsetRequestedState() {
    requestedPosition.setVisible(false);
  }

  public void setPosition(T position) {
    actualPosition.setMeasurementX(getCoordinatesAtStageLocation(position).getX());
    actualPosition.setMeasurementY(getCoordinatesAtStageLocation(position).getY());
    actualPosition.setVisible(true);
  }

  public void unsetPosition() {
    actualPosition.setVisible(false);
  }

  public ObjectBinding<XYCoordinate<Length>> stageLocationToCoordinates(
      ObservableValue<? extends T> location) {
    return createObjectBinding(() -> getCoordinatesAtStageLocation(location.getValue()), location);
  }

  public Stage<T, ?> getStageDevice() {
    return stage;
  }

  public Image getImage() {
    return imageView.getImage();
  }

  protected void setImage(Image image) {
    this.imageView.setImage(image);
  }

  public AnnotationLayer<Length, Length> getAnnotationLayer() {
    return annotationLayer;
  }

  public abstract T getStageLocationAtCoordinates(XYCoordinate<Length> coordinates);

  public abstract XYCoordinate<Length> getCoordinatesAtStageLocation(T location);
}
