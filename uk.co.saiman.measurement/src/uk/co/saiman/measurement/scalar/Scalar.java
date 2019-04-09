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
package uk.co.saiman.measurement.scalar;

import javax.measure.Quantity;
import javax.measure.Unit;

import uk.co.saiman.measurement.UnitBuilder;

/*
 * TODO this needs to be revisited when Valhalla brings value types and
 * specialization. It's possible that it can be hugely overhauled to be both a
 * value and specializable over unit (or perhaps just unit type). So we can use
 * the scalar API and get the same performance and memory layout as if we were
 * using pure primitives. This in turn may make it useful to revisit continuous
 * function API etc. to more directly deal with quantities, since we can have
 * e.g. flattened arrays of them.
 */
public class Scalar<T extends Quantity<T>> implements Quantity<T>, Comparable<Quantity<T>> {
  private final Unit<T> unit;
  private final double amount;

  public Scalar(UnitBuilder<T> unit, Number amount) {
    this.unit = unit.getUnit();
    this.amount = amount.doubleValue();
  }

  public Scalar(Unit<T> unit, Number amount) {
    this.unit = unit;
    this.amount = amount.doubleValue();
  }

  @Override
  public Scalar<T> add(Quantity<T> augend) {
    return new Scalar<>(getUnit(), amount + augend.to(getUnit()).getValue().doubleValue());
  }

  @Override
  public Scalar<T> subtract(Quantity<T> subtrahend) {
    return new Scalar<>(getUnit(), amount - subtrahend.to(getUnit()).getValue().doubleValue());
  }

  @Override
  public Scalar<?> divide(Quantity<?> divisor) {
    return new Scalar<>(
        getUnit().divide(divisor.getUnit()),
        amount / divisor.getValue().doubleValue());
  }

  @Override
  public Scalar<T> divide(Number divisor) {
    return new Scalar<>(getUnit(), amount / divisor.doubleValue());
  }

  @Override
  public Scalar<?> multiply(Quantity<?> multiplier) {
    return new Scalar<>(
        getUnit().multiply(multiplier.getUnit()),
        amount / multiplier.getValue().doubleValue());
  }

  @Override
  public Scalar<T> multiply(Number multiplier) {
    return new Scalar<>(getUnit(), amount * multiplier.doubleValue());
  }

  @Override
  public Scalar<?> inverse() {
    return new Scalar<>(getUnit().inverse(), 1 / amount);
  }

  @Override
  public Scalar<T> to(Unit<T> unit) {
    return unit.equals(getUnit())
        ? this
        : new Scalar<>(unit, getUnit().getConverterTo(unit).convert(getValue()).doubleValue());
  }

  @SuppressWarnings("unchecked")
  @Override
  public <U extends Quantity<U>> Scalar<U> asType(Class<U> type) throws ClassCastException {
    this.getUnit().asType(type);
    return (Scalar<U>) this;
  }

  @Override
  public Number getValue() {
    return amount;
  }

  public int intValue() {
    return (int) amount;
  }

  public long longValue() {
    return (long) amount;
  }

  public float floatValue() {
    return (float) amount;
  }

  public double doubleValue() {
    return amount;
  }

  @Override
  public Unit<T> getUnit() {
    return unit;
  }

  @Override
  public int compareTo(Quantity<T> o) {
    return Double.compare(amount, o.to(getUnit()).getValue().doubleValue());
  }
}
