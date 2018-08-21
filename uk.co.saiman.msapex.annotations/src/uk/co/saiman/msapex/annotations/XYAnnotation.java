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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class XYAnnotation<X extends Quantity<X>, Y extends Quantity<Y>> extends Annotation<X, Y> {
  private final ObjectProperty<Quantity<X>> measurementX;
  private final ObjectProperty<Quantity<Y>> measurementY;

  public XYAnnotation() {
    this.measurementX = new SimpleObjectProperty<>(null);
    this.measurementY = new SimpleObjectProperty<>(null);

    layoutXProperty().bind(measurementToLayoutX(measurementX));
    layoutYProperty().bind(measurementToLayoutY(measurementY));
  }

  public ObjectProperty<Quantity<X>> measurementXProperty() {
    return measurementX;
  }

  public Quantity<X> getMeasurementX() {
    return measurementX.get();
  }

  public void setMeasurementX(Quantity<X> value) {
    measurementX.set(value);
  }

  public ObjectProperty<Quantity<Y>> measurementYProperty() {
    return measurementY;
  }

  public Quantity<Y> getMeasurementY() {
    return measurementY.get();
  }

  public void setMeasurementY(Quantity<Y> value) {
    measurementY.set(value);
  }
}
