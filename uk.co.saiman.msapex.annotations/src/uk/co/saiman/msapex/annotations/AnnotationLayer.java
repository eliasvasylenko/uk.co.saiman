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
 * This file is part of uk.co.saiman.msapex.annotations.
 *
 * uk.co.saiman.msapex.annotations is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.annotations is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.annotations;

import static java.util.function.Function.identity;
import static javafx.collections.FXCollections.observableSet;
import static uk.co.saiman.fx.bindings.FluentObjectBinding.over;

import java.util.HashSet;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import uk.co.saiman.measurement.scalar.Scalar;

public class AnnotationLayer<X extends Quantity<X>, Y extends Quantity<Y>> extends Region {
  private final ObjectProperty<Unit<X>> unitX;
  private final ObjectProperty<Unit<Y>> unitY;

  private final ObservableSet<Annotation<X, Y>> annotations;
  private final ObjectProperty<Bounds> measurementBounds;

  public AnnotationLayer() {
    this((Unit<X>) null, (Unit<Y>) null);
  }

  public AnnotationLayer(ObservableValue<Unit<X>> unitX, ObservableValue<Unit<Y>> unitY) {
    this(unitX.getValue(), unitY.getValue());
    this.unitX.bind(unitX);
    this.unitY.bind(unitY);
  }

  public AnnotationLayer(Unit<X> unitX, Unit<Y> unitY) {
    this.unitX = new SimpleObjectProperty<>(unitX);
    this.unitY = new SimpleObjectProperty<>(unitY);
    this.annotations = observableSet(new HashSet<>());

    measurementBounds = new SimpleObjectProperty<>(new BoundingBox(0, 0, 1, 1));

    annotations.addListener((SetChangeListener<Annotation<X, Y>>) i -> {
      if (i.wasRemoved()) {
        getChildren().remove(i.getElementRemoved());
      }
      if (i.wasAdded()) {
        getChildren().add(i.getElementAdded());
      }
    });

    Rectangle clip = new Rectangle();
    setClip(clip);
    layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
      clip.setWidth(newValue.getWidth());
      clip.setHeight(newValue.getHeight());
    });
  }

  @Override
  public void resize(double width, double height) {
    super.resize(width, height);
  }

  public ObservableSet<Annotation<X, Y>> getAnnotations() {
    return annotations;
  }

  public ObjectProperty<Bounds> measurementBoundsProperty() {
    return measurementBounds;
  }

  public Bounds getMeasurementBounds() {
    return measurementBounds.getValue();
  }

  public void setMeasurementBounds(Bounds value) {
    if (!measurementBounds.getValue().equals(value))
      measurementBounds.setValue(value);
  }

  public ObjectProperty<Unit<X>> unitXProperty() {
    return unitX;
  }

  public Unit<X> getUnitX() {
    return unitX.get();
  }

  public void setUnitX(Unit<X> value) {
    unitX.set(value);
  }

  public ObjectProperty<Unit<Y>> unitYProperty() {
    return unitY;
  }

  public Unit<Y> getUnitY() {
    return unitY.get();
  }

  public void setUnitY(Unit<Y> value) {
    unitY.set(value);
  }

  public DoubleBinding measurementToLocalWidth(Quantity<X> measurement) {
    return over(measurementBounds)
        .withDependency(unitX, widthProperty())
        .filter(b -> measurement != null)
        .map(b -> measurement.to(unitX.get()).getValue().doubleValue() * getWidth() / b.getWidth())
        .orDefault(0d)
        .mapToDouble(identity());
  }

  public DoubleBinding measurementToLocalHeight(Quantity<Y> measurement) {
    return over(measurementBounds)
        .withDependency(unitY, heightProperty())
        .filter(b -> measurement != null)
        .map(
            b -> measurement.to(unitY.get()).getValue().doubleValue() * getHeight() / b.getHeight())
        .orDefault(0d)
        .mapToDouble(identity());
  }

  public ObjectBinding<Quantity<X>> localWidthToMeasurement(double local) {
    return over(measurementBounds)
        .withDependency(unitX, widthProperty())
        .map(b -> local * b.getWidth() / getWidth())
        .map(v -> new Scalar<>(unitX.get(), v));
  }

  public ObjectBinding<Quantity<Y>> localHeightToMeasurement(double local) {
    return over(measurementBounds)
        .withDependency(unitY, heightProperty())
        .map(b -> local * b.getHeight() / getHeight())
        .map(v -> new Scalar<>(unitY.get(), v));
  }

  public DoubleBinding measurementToLocalX(Quantity<X> measurement) {
    return over(measurementBounds)
        .withDependency(unitX, widthProperty())
        .filter(b -> measurement != null)
        .map(
            b -> (measurement.to(unitX.get()).getValue().doubleValue() - b.getMinX())
                * getWidth()
                / b.getWidth())
        .orDefault(0d)
        .mapToDouble(identity());
  }

  public DoubleBinding measurementToLocalY(Quantity<Y> measurement) {
    return over(measurementBounds)
        .withDependency(unitY, heightProperty())
        .filter(b -> measurement != null)
        .map(
            b -> (measurement.to(unitY.get()).getValue().doubleValue() - b.getMinY())
                * getHeight()
                / b.getHeight())
        .orDefault(0d)
        .mapToDouble(identity());
  }

  public ObjectBinding<Quantity<X>> localXToMeasurement(double local) {
    return over(measurementBounds)
        .withDependency(unitX, widthProperty())
        .map(b -> local * b.getWidth() / getWidth() + b.getMinX())
        .map(v -> new Scalar<>(unitX.get(), v));
  }

  public ObjectBinding<Quantity<Y>> localYToMeasurement(double local) {
    return over(measurementBounds)
        .withDependency(unitY, heightProperty())
        .map(b -> local * b.getHeight() / getHeight() + b.getMinY())
        .map(v -> new Scalar<>(unitY.get(), v));
  }

  @Override
  protected void layoutChildren() {
    for (Node node : getChildren()) {
      node.autosize();
    }
  }
}
