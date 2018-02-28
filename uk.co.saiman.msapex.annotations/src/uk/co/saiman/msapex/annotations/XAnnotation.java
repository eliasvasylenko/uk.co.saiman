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

import static uk.co.saiman.measurement.fx.QuantityBindings.toUnit;

import javax.measure.Quantity;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class XAnnotation<X extends Quantity<X>, Y extends Quantity<Y>> extends Annotation<X, Y> {
  private final Property<Quantity<X>> measurementX;

  public XAnnotation() {
    measurementX = new SimpleObjectProperty<>();

    layoutXProperty().bind(measurementToLayoutX(toUnit(unitXProperty()).convert(measurementX)));
  }

  public Property<Quantity<X>> measurementXProperty() {
    return measurementX;
  }

  public Quantity<X> getMeasurementX() {
    return measurementX.getValue();
  }

  public void setMeasurementX(Quantity<X> value) {
    measurementX.setValue(value);
  }
}
