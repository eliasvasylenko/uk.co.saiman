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
 * This file is part of uk.co.saiman.measurement.
 *
 * uk.co.saiman.measurement is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.measurement is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.measurement.coordinate;

import static uk.co.saiman.measurement.Quantities.quantityFormat;

import javax.measure.Quantity;
import javax.measure.Unit;

import uk.co.saiman.measurement.QuantityFormat;
import uk.co.saiman.measurement.scalar.Scalar;

/**
 * A Cartesian coordinate in two dimensions.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of unit of the axes
 */
public class XYCoordinate<T extends Quantity<T>> {
  private final Quantity<T> x;
  private final Quantity<T> y;

  public XYCoordinate(Quantity<T> x, Quantity<T> y) {
    this.x = x;
    this.y = y;
  }

  public XYCoordinate(Unit<T> unit, double x, double y) {
    this(new Scalar<>(unit, x), new Scalar<>(unit, y));
  }

  public Quantity<T> getX() {
    return x;
  }

  public Quantity<T> getY() {
    return y;
  }

  public Unit<T> getXUnit() {
    return x.getUnit();
  }

  public Unit<T> getYUnit() {
    return y.getUnit();
  }

  public double getXValue() {
    return x.getValue().doubleValue();
  }

  public double getYValue() {
    return y.getValue().doubleValue();
  }

  public XYCoordinate<T> to(Unit<T> unit) {
    return new XYCoordinate<>(x.to(unit), y.to(unit));
  }

  public <U extends Quantity<U>> XYCoordinate<U> asType(Class<U> type) throws ClassCastException {
    return new XYCoordinate<>(x.asType(type), y.asType(type));
  }

  @Override
  public String toString() {
    return toString(quantityFormat());
  }

  public String toString(QuantityFormat format) {
    return "(" + format.format(x) + ", " + format.format(y) + ")";
  }

  public static XYCoordinate<?> fromString(String string) {
    return fromString(string, quantityFormat());
  }

  public static XYCoordinate<?> fromString(String string, QuantityFormat format) {
    string = string.trim();

    if (string.startsWith("(")) {
      if (!string.endsWith(")")) {
        throw new IllegalArgumentException("Illegal component count: " + string);
      }

      string = string.substring(1, string.length() - 1);
    }

    String[] split = string.trim().split(",");
    if (split.length != 2)
      throw new IllegalArgumentException("Unrecognised format: " + string);

    Quantity<?> x = format.parse(split[0]);
    Quantity<?> y = format.parse(split[1]);

    if (!x.getUnit().isCompatible(y.getUnit()))
      throw new IllegalArgumentException("Incompatible components: " + x + ", " + y);

    return newUnsafe(x, y);
  }

  @SuppressWarnings("unchecked")
  private static <T extends Quantity<T>> XYCoordinate<T> newUnsafe(Quantity<?> x, Quantity<?> y) {
    return new XYCoordinate<>((Quantity<T>) x, (Quantity<T>) y);
  }
}
