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

import static uk.co.saiman.fx.bindings.FluentObjectBinding.over;

import javax.measure.Quantity;

import javafx.beans.binding.ObjectBinding;
import javafx.scene.shape.Rectangle;
import uk.co.saiman.measurement.scalar.Scalar;

public class RectangleAnnotation<X extends Quantity<X>, Y extends Quantity<Y>>
    extends XYAnnotation<X, Y> {
  private final Rectangle shape;

  public RectangleAnnotation(Quantity<X> width, Quantity<Y> height) {
    this.shape = new Rectangle();
    this.shape.setStrokeWidth(1);
    getChildren().add(shape);

    var zeroX = new Scalar<>(width.getUnit(), 0);
    var zeroY = new Scalar<>(height.getUnit(), 0);

    ObjectBinding<Quantity<X>> relativeWidth = over(layoutUnitX())
        .map(u -> width.to(u).subtract(zeroX.to(u)));
    shape.widthProperty().bind(measurementToLayoutWidth(relativeWidth));

    ObjectBinding<Quantity<Y>> relativeHeight = over(layoutUnitY())
        .map(u -> height.to(u).subtract(zeroY.to(u)));
    shape.heightProperty().bind(measurementToLayoutHeight(relativeHeight));
  }
}
