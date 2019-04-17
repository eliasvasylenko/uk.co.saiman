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

import static javafx.beans.binding.Bindings.createDoubleBinding;
import static javafx.beans.binding.Bindings.select;
import static javafx.beans.binding.Bindings.selectDouble;
import static uk.co.saiman.measurement.fx.QuantityBindings.createQuantityBinding;

import java.util.Optional;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Group;

public abstract class Annotation<X extends Quantity<X>, Y extends Quantity<Y>> extends Group {
  private final IntegerProperty priority;

  private final ObjectBinding<Unit<X>> layoutUnitX;
  private final ObjectBinding<Unit<Y>> layoutUnitY;
  private final DoubleBinding layoutWidth;
  private final DoubleBinding layoutHeight;
  private final ObjectBinding<Bounds> measurementBounds;

  public Annotation() {
    priority = new SimpleIntegerProperty();

    layoutUnitX = select(parentProperty(), "unitX");
    layoutUnitY = select(parentProperty(), "unitY");
    layoutWidth = selectDouble(parentProperty(), "width");
    layoutHeight = selectDouble(parentProperty(), "height");
    measurementBounds = select(parentProperty(), "measurementBounds");
  }

  public ObjectBinding<Unit<X>> layoutUnitX() {
    return layoutUnitX;
  }

  public ObjectBinding<Unit<Y>> layoutUnitY() {
    return layoutUnitY;
  }

  @SuppressWarnings("unchecked")
  private Optional<AnnotationLayer<X, Y>> getAnnotationLayer() {
    if (getParent() instanceof AnnotationLayer<?, ?>)
      return Optional.of((AnnotationLayer<X, Y>) getParent());
    else
      return Optional.empty();
  }

  protected DoubleBinding measurementToLayoutX(ObservableValue<? extends Quantity<X>> value) {
    DoubleBinding amount = createQuantityBinding(value).convertTo(layoutUnitX).getDoubleAmount();
    return createDoubleBinding(
        () -> getAnnotationLayer().map(p -> p.measurementToLocalX(amount.get())).orElse(0d),
        measurementBounds,
        layoutWidth,
        amount);
  }

  protected DoubleBinding measurementToLayoutY(ObservableValue<? extends Quantity<Y>> value) {
    DoubleBinding amount = createQuantityBinding(value).convertTo(layoutUnitY).getDoubleAmount();
    return createDoubleBinding(
        () -> getAnnotationLayer().map(p -> p.measurementToLocalY(amount.get())).orElse(0d),
        measurementBounds,
        layoutHeight,
        amount);
  }

  protected DoubleBinding measurementToLayoutWidth(ObservableValue<? extends Quantity<X>> value) {
    DoubleBinding amount = createQuantityBinding(value)
        .convertIntervalTo(layoutUnitX)
        .getDoubleAmount();
    return createDoubleBinding(
        () -> getAnnotationLayer().map(p -> p.measurementToLocalWidth(amount.get())).orElse(0d),
        measurementBounds,
        layoutWidth,
        amount);
  }

  protected DoubleBinding measurementToLayoutHeight(ObservableValue<? extends Quantity<Y>> value) {
    DoubleBinding amount = createQuantityBinding(value)
        .convertIntervalTo(layoutUnitY)
        .getDoubleAmount();
    return createDoubleBinding(
        () -> getAnnotationLayer().map(p -> p.measurementToLocalHeight(amount.get())).orElse(0d),
        measurementBounds,
        layoutHeight,
        amount);
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
