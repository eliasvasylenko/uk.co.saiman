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

import static uk.co.saiman.fx.bindings.FluentObjectBinding.over;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import uk.co.saiman.fx.bindings.FluentObjectBinding;

public class Annotation<X extends Quantity<X>, Y extends Quantity<Y>> extends Group {
  private final IntegerProperty priority;

  private final FluentObjectBinding<AnnotationLayer<X, Y>> annotationLayer;

  private final ObjectBinding<Unit<X>> layoutUnitX;
  private final ObjectBinding<Unit<Y>> layoutUnitY;

  @SuppressWarnings("unchecked")
  public Annotation() {
    priority = new SimpleIntegerProperty();

    annotationLayer = over(parentProperty())
        .filter(p -> p instanceof AnnotationLayer<?, ?>)
        .map(a -> (AnnotationLayer<X, Y>) a);

    layoutUnitX = annotationLayer.flatMap(AnnotationLayer::unitXProperty);
    layoutUnitY = annotationLayer.flatMap(AnnotationLayer::unitYProperty);
  }

  public ObservableObjectValue<AnnotationLayer<X, Y>> annotationLayerProperty() {
    return annotationLayer;
  }

  public AnnotationLayer<X, Y> getAnnotationLayer() {
    return annotationLayer.get();
  }

  public ObjectBinding<Unit<X>> layoutUnitX() {
    return layoutUnitX;
  }

  public ObjectBinding<Unit<Y>> layoutUnitY() {
    return layoutUnitY;
  }

  protected DoubleBinding measurementToLayoutX(ObservableValue<? extends Quantity<X>> value) {
    return annotationLayer
        .withDependency(value)
        .flatMap(p -> p.measurementToLocalX(value.getValue()))
        .orDefault(0d)
        .mapToDouble(Number::doubleValue);
  }

  protected DoubleBinding measurementToLayoutY(ObservableValue<? extends Quantity<Y>> value) {
    return annotationLayer
        .withDependency(value)
        .flatMap(p -> p.measurementToLocalY(value.getValue()))
        .orDefault(0d)
        .mapToDouble(Number::doubleValue);
  }

  protected DoubleBinding measurementToLayoutWidth(ObservableValue<? extends Quantity<X>> value) {
    return annotationLayer
        .withDependency(value)
        .flatMap(p -> p.measurementToLocalWidth(value.getValue()))
        .orDefault(0d)
        .mapToDouble(Number::doubleValue);
  }

  protected DoubleBinding measurementToLayoutHeight(ObservableValue<? extends Quantity<Y>> value) {
    return annotationLayer
        .withDependency(value)
        .flatMap(p -> p.measurementToLocalHeight(value.getValue()))
        .orDefault(0d)
        .mapToDouble(Number::doubleValue);
  }

  /**
   * Priority is used to resolve a number of different potential conflicts between
   * annotations.
   * <p>
   * It is used to determine the order in which they receive and are able to
   * consume input events. Higher priority annotations receive them first.
   * <p>
   * It is used to determine which annotation is rendered when parts of two
   * annotations overlap in a way which is not allowed. Higher priority
   * annotations are selected for rendering.
   * <p>
   * It is used to determine render order when parts of two annotations overlap in
   * a way which is allowed. Higher priority annotations are drawn on top.
   */
  public IntegerProperty priorityProperty() {
    return priority;
  }

  public int getPriority() {
    return priority.get();
  }

  public void setPriority(int value) {
    priority.set(value);
  }
}
