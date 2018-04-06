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

import static javafx.beans.binding.Bindings.createDoubleBinding;
import static javafx.beans.binding.Bindings.select;
import static uk.co.saiman.fx.FxUtilities.createStrongBinding;
import static uk.co.saiman.measurement.fx.QuantityBindings.toUnit;

import java.util.Optional;
import java.util.function.BiFunction;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;

public abstract class Annotation<X extends Quantity<X>, Y extends Quantity<Y>> extends Group {
  private final Unit<X> unitX;
  private final Unit<Y> unitY;

  private final IntegerProperty priority;

  private final ObjectBinding<Optional<AnnotationLayer<X, Y>>> annotationLayer;

  public Annotation(Unit<X> unitX, Unit<Y> unitY) {
    this.unitX = unitX;
    this.unitY = unitY;

    priority = new SimpleIntegerProperty();

    annotationLayer = createStrongBinding(
        this::getAnnotationLayer,
        select(parentProperty(), "unitX"),
        select(parentProperty(), "unitY"),
        select(parentProperty(), "width"),
        select(parentProperty(), "measurementBounds"));
  }

  public Unit<X> getUnitX() {
    return unitX;
  }

  public Unit<Y> getUnitY() {
    return unitY;
  }

  public Unit<X> getLayoutUnitX() {
    return getAnnotationLayer().map(AnnotationLayer::getUnitX).orElse(getUnitX());
  }

  public Unit<Y> getLayoutUnitY() {
    return getAnnotationLayer().map(AnnotationLayer::getUnitY).orElse(getUnitY());
  }

  @SuppressWarnings("unchecked")
  private Optional<AnnotationLayer<X, Y>> getAnnotationLayer() {
    if (getParent() instanceof AnnotationLayer<?, ?>)
      return Optional.of((AnnotationLayer<X, Y>) getParent());
    else
      return Optional.empty();
  }

  private DoubleBinding measurementConversion(
      BiFunction<AnnotationLayer<X, Y>, Double, Double> function,
      ObservableValue<? extends Number> value) {
    return createDoubleBinding(() -> {
      return annotationLayer
          .get()
          .map(p -> function.apply(p, value.getValue().doubleValue()))
          .orElse(0d);
    }, annotationLayer);
  }

  protected DoubleBinding measurementToLayoutWidth(ObservableValue<? extends Number> value) {
    return measurementConversion(
        AnnotationLayer::measurementToLocalWidth,
        toUnit(getLayoutUnitX()).fromUnit(unitX).convertInterval(value));
  }

  protected DoubleBinding measurementToLayoutHeight(ObservableValue<? extends Number> value) {
    return measurementConversion(
        AnnotationLayer::measurementToLocalHeight,
        toUnit(getLayoutUnitY()).fromUnit(unitY).convertInterval(value));
  }

  protected DoubleBinding layoutWidthToMeasurement(ObservableValue<? extends Number> value) {
    return measurementConversion(
        AnnotationLayer::localWidthToMeasurement,
        toUnit(unitX).fromUnit(getLayoutUnitX()).convertInterval(value));
  }

  protected DoubleBinding layoutHeightToMeasurement(ObservableValue<? extends Number> value) {
    return measurementConversion(
        AnnotationLayer::localHeightToMeasurement,
        toUnit(unitY).fromUnit(getLayoutUnitY()).convertInterval(value));
  }

  protected DoubleBinding measurementToLayoutX(ObservableValue<? extends Number> value) {
    return measurementConversion(
        AnnotationLayer::measurementToLocalX,
        toUnit(getLayoutUnitX()).fromUnit(unitX).convert(value));
  }

  protected DoubleBinding measurementToLayoutY(ObservableValue<? extends Number> value) {
    return measurementConversion(
        AnnotationLayer::measurementToLocalY,
        toUnit(getLayoutUnitY()).fromUnit(unitY).convert(value));
  }

  protected DoubleBinding layoutXToMeasurement(ObservableValue<? extends Number> value) {
    return measurementConversion(
        AnnotationLayer::localXToMeasurement,
        toUnit(unitX).fromUnit(getLayoutUnitX()).convert(value));
  }

  protected DoubleBinding layoutYToMeasurement(ObservableValue<? extends Number> value) {
    return measurementConversion(
        AnnotationLayer::localYToMeasurement,
        toUnit(unitY).fromUnit(getLayoutUnitY()).convert(value));
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
