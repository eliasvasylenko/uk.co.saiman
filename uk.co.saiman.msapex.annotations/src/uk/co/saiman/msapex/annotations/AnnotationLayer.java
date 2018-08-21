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

import static javafx.collections.FXCollections.observableSet;

import java.util.HashSet;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

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

    annotations.addListener(new WeakInvalidationListener(i -> {
      getChildren().retainAll(annotations);
      for (Annotation<X, Y> annotation : annotations) {
        if (!getChildren().contains(annotation)) {
          getChildren().add(annotation);
        }
      }
    }));
  }

  @Override
  public void resizeRelocate(double x, double y, double width, double height) {
    super.resizeRelocate(x, y, width, height);
    setClip(new Rectangle(0, 0, width, height));
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

  public double measurementToLocalWidth(double measurement) {
    return measurement * getWidth() / measurementBounds.getValue().getWidth();
  }

  public double measurementToLocalHeight(double measurement) {
    return measurement * getHeight() / measurementBounds.getValue().getHeight();
  }

  public double localWidthToMeasurement(double local) {
    return local * measurementBounds.getValue().getWidth() / getWidth();
  }

  public double localHeightToMeasurement(double local) {
    return local * measurementBounds.getValue().getHeight() / getHeight();
  }

  public double measurementToLocalX(double measurement) {
    return measurementToLocalWidth(measurement - measurementBounds.getValue().getMinX());
  }

  public double measurementToLocalY(double measurement) {
    return measurementToLocalHeight(measurement - measurementBounds.getValue().getMinY());
  }

  public double localXToMeasurement(double local) {
    return localWidthToMeasurement(local) + measurementBounds.getValue().getMinX();
  }

  public double localYToMeasurement(double local) {
    return localHeightToMeasurement(local) + measurementBounds.getValue().getMinY();
  }

  @Override
  protected void layoutChildren() {
    for (Node node : getChildren()) {
      node.autosize();
    }
  }
}
