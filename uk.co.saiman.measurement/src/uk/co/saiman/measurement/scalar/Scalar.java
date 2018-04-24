package uk.co.saiman.measurement.scalar;

import javax.measure.Quantity;
import javax.measure.Unit;

import uk.co.saiman.measurement.UnitBuilder;

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
