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

import javafx.beans.binding.ObjectBinding;
import javafx.scene.shape.Ellipse;
import uk.co.saiman.measurement.scalar.Scalar;

public class EllipseAnnotation<X extends Quantity<X>, Y extends Quantity<Y>>
    extends XYAnnotation<X, Y> {
  private final Ellipse shape;

  public EllipseAnnotation(Quantity<X> radiusX, Quantity<Y> radiusY) {
    this.shape = new Ellipse();
    getChildren().add(shape);

    var zeroX = new Scalar<>(radiusX.getUnit(), 0);
    var zeroY = new Scalar<>(radiusY.getUnit(), 0);

    ObjectBinding<Quantity<X>> relativeWidth = over(layoutUnitX())
        .map(u -> radiusX.to(u).subtract(zeroX.to(u)));
    shape.radiusXProperty().bind(measurementToLayoutWidth(relativeWidth));

    ObjectBinding<Quantity<Y>> relativeHeight = over(layoutUnitY())
        .map(u -> radiusY.to(u).subtract(zeroY.to(u)));
    shape.radiusYProperty().bind(measurementToLayoutHeight(relativeHeight));
  }

  protected Ellipse getShape() {
    return shape;
  }
}
