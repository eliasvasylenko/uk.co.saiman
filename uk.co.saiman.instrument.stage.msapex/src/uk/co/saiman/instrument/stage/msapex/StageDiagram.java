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
import static uk.co.saiman.fx.FxUtilities.wrap;
import static uk.co.saiman.measurement.fx.CoordinateBindings.createCoordinateBinding;

import java.util.stream.Stream;

import javax.measure.Unit;
import javax.measure.quantity.Length;

import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import uk.co.saiman.instrument.sample.Analysis;
import uk.co.saiman.instrument.sample.RequestedSampleState;
import uk.co.saiman.instrument.stage.Stage;
import uk.co.saiman.measurement.coordinate.XYCoordinate;
import uk.co.saiman.measurement.fx.XYCoordinateBinding;
import uk.co.saiman.msapex.annotations.AnnotationLayer;
import uk.co.saiman.msapex.annotations.ShapeAnnotation;
import uk.co.saiman.msapex.annotations.XYAnnotation;
import uk.co.saiman.instrument.stage.msapex.StageDiagramSampleConfiguration;

/**
 * A visualization of the sample area of a stage mapped into cartesian
 * coordinates.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the coordinate type of the stage
 */
public abstract class StageDiagram<T> extends StackPane {
  private final ImageView imageView = new ImageView();

  private AnnotationLayer<Length, Length> annotationLayer;
  private XYCoordinateBinding<Length> requestedCoordinates;
  private XYCoordinateBinding<Length> actualCoordinates;

  protected void initialize(Unit<Length> unit) {
    this.annotationLayer = new AnnotationLayer<>(unit, unit);
    Stage<T, ?> stageDevice = getStageDevice();

    requestedCoordinates = stageLocationToCoordinates(
        wrap(stageDevice.requestedSampleState().map(this::requestedStateToPosition)));
    actualCoordinates = stageLocationToCoordinates(wrap(stageDevice.samplePosition()));

    XYAnnotation<Length, Length> requestedPosition = new ShapeAnnotation<>(new Circle(4));
    annotationLayer.getAnnotations().add(requestedPosition);
    requestedPosition.measurementXProperty().bind(requestedCoordinates.getX());
    requestedPosition.measurementYProperty().bind(requestedCoordinates.getY());

    XYAnnotation<Length, Length> actualPosition = new ShapeAnnotation<>(new Circle(4));
    annotationLayer.getAnnotations().add(actualPosition);
    actualPosition.measurementXProperty().bind(actualCoordinates.getX());
    actualPosition.measurementYProperty().bind(actualCoordinates.getY());

    getChildren().add(imageView);
    getChildren().add(annotationLayer);
  }

  public T requestedStateToPosition(RequestedSampleState<T> requestedState) {
    if (requestedState instanceof Analysis<?>) {
      return ((Analysis<T>) requestedState).position();
    } else {
      return null;
    }
  }

  public XYCoordinateBinding<Length> getRequestedCoordinates() {
    return requestedCoordinates;
  }

  public XYCoordinateBinding<Length> getActualCoordinates() {
    return actualCoordinates;
  }

  public XYCoordinateBinding<Length> stageLocationToCoordinates(
      ObservableValue<? extends T> location) {
    return createCoordinateBinding(
        createObjectBinding(() -> getCoordinatesAtStageLocation(location.getValue()), location));
  }

  public abstract Stage<T, ?> getStageDevice();

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

  public abstract Stream<? extends StageDiagramSampleConfiguration> getSampleConfigurations();
}
