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
 * This file is part of uk.co.saiman.msapex.chart.
 *
 * uk.co.saiman.msapex.chart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.chart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.annotations;

import static javafx.beans.binding.Bindings.createDoubleBinding;
import static javafx.beans.binding.Bindings.createObjectBinding;
import static javafx.beans.binding.Bindings.select;
import static uk.co.saiman.msapex.annotations.AnnotationLayer.MEASUREMENT_BOUNDS;

import java.util.Optional;
import java.util.function.BiFunction;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;

public abstract class Annotation<X extends Quantity<X>, Y extends Quantity<Y>> extends Group {
  private final IntegerProperty priority;

  public Annotation() {
    priority = new SimpleIntegerProperty();
  }

  @SuppressWarnings("unchecked")
  protected Optional<AnnotationLayer<X, Y>> getAnnotationPane() {
    if (getParent() instanceof AnnotationLayer<?, ?>)
      return Optional.of((AnnotationLayer<X, Y>) getParent());
    else
      return Optional.empty();
  }

  protected ObservableValue<Unit<X>> unitXProperty() {
    return createObjectBinding(
        () -> getAnnotationPane().map(AnnotationLayer::getUnitX).orElse(null),
        parentProperty());
  }

  protected ObservableValue<Unit<Y>> unitYProperty() {
    return createObjectBinding(
        () -> getAnnotationPane().map(AnnotationLayer::getUnitY).orElse(null),
        parentProperty());
  }

  private DoubleBinding measurementConversion(
      BiFunction<AnnotationLayer<X, Y>, Double, Double> function,
      ObservableValue<? extends Number> value) {
    return createDoubleBinding(
        () -> getAnnotationPane()
            .map(p -> function.apply(p, value.getValue().doubleValue()))
            .orElse(0d),
        select(parentProperty(), MEASUREMENT_BOUNDS),
        value);
  }

  protected DoubleBinding measurementToLayoutWidth(ObservableValue<? extends Number> value) {
    return measurementConversion(AnnotationLayer::measurementToLocalWidth, value);
  }

  protected DoubleBinding measurementToLayoutHeight(ObservableValue<? extends Number> value) {
    return measurementConversion(AnnotationLayer::measurementToLocalHeight, value);
  }

  public DoubleBinding layoutWidthToMeasurement(ObservableValue<? extends Number> value) {
    return measurementConversion(AnnotationLayer::localWidthToMeasurement, value);
  }

  public DoubleBinding layoutHeightToMeasurement(ObservableValue<? extends Number> value) {
    return measurementConversion(AnnotationLayer::localHeightToMeasurement, value);
  }

  protected DoubleBinding measurementToLayoutX(ObservableValue<? extends Number> value) {
    return measurementConversion(AnnotationLayer::measurementToLocalX, value);
  }

  protected DoubleBinding measurementToLayoutY(ObservableValue<? extends Number> value) {
    return measurementConversion(AnnotationLayer::measurementToLocalY, value);
  }

  public DoubleBinding layoutXToMeasurement(ObservableValue<? extends Number> value) {
    return measurementConversion(AnnotationLayer::localXToMeasurement, value);
  }

  public DoubleBinding layoutYToMeasurement(ObservableValue<? extends Number> value) {
    return measurementConversion(AnnotationLayer::localYToMeasurement, value);
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
   * 
   * @return
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
