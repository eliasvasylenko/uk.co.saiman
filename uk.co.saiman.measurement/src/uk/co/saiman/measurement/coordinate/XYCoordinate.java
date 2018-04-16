package uk.co.saiman.measurement.coordinate;

import static uk.co.saiman.measurement.Quantities.getQuantity;

import javax.measure.Quantity;
import javax.measure.Unit;

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
    this(getQuantity(unit, x), getQuantity(unit, y));
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
}
