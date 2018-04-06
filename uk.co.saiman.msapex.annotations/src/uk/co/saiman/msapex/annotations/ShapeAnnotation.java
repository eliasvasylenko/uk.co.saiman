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

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;

public class ShapeAnnotation<X extends Quantity<X>, Y extends Quantity<Y>>
    extends XYAnnotation<X, Y> {
  public ShapeAnnotation(Unit<X> unitX, Unit<Y> unitY, Shape shape) {
    super(unitX, unitY);

    Scale scale = new Scale();
    scale.setPivotX(0);
    scale.setPivotY(0);
    scale.xProperty().bind(measurementToLayoutWidth(new SimpleDoubleProperty(1)));
    scale.yProperty().bind(measurementToLayoutHeight(new SimpleDoubleProperty(1)));

    Group transformationGroup = new Group(shape);
    transformationGroup.getTransforms().add(scale);

    getChildren().add(transformationGroup);
  }
}
