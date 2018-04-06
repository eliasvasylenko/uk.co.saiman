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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class XAnnotation<X extends Quantity<X>, Y extends Quantity<Y>> extends Annotation<X, Y> {
  private final DoubleProperty measurementX;

  public XAnnotation(Unit<X> unitX, Unit<Y> unitY) {
    super(unitX, unitY);

    this.measurementX = new SimpleDoubleProperty(0);

    layoutXProperty().bind(measurementToLayoutX(measurementX));
  }

  public DoubleProperty measurementXProperty() {
    return measurementX;
  }

  public double getMeasurementX() {
    return measurementX.getValue();
  }

  public void setMeasurementX(double value) {
    measurementX.setValue(value);
  }
}
