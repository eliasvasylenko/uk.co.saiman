package uk.co.saiman.mathematics;

public class FloatingPointUtilities {
  private FloatingPointUtilities() {}

  public static double unitInTheLastPlaceAbove(double value) {
    if (Double.isNaN(value) || Double.isInfinite(value)) {
      return value;
    }
    double absoluteValue = Math.abs(value);

    long nextValueLong = Double.doubleToLongBits(absoluteValue) + 1;
    double nextValue = Double.longBitsToDouble(nextValueLong);

    // if ended on bad number go down instead
    if (Double.isNaN(nextValue) || Double.isInfinite(nextValue)) {
      nextValueLong = nextValueLong - 2;
      nextValue = absoluteValue;
      absoluteValue = Double.longBitsToDouble(nextValueLong);
    }

    return Math.abs(nextValue - absoluteValue);
  }

  public final double unitInTheLastPlaceBelow(double value) {
    if (Double.isNaN(value) || Double.isInfinite(value)) {
      return value;
    }
    double absoluteValue = Math.abs(value);

    long nextValueLong = Double.doubleToLongBits(absoluteValue) - 1;
    double nextValue = Double.longBitsToDouble(nextValueLong);

    // if ended on bad number go up instead
    if (Double.isNaN(nextValue) || Double.isInfinite(nextValue)) {
      nextValueLong = nextValueLong + 2;
      nextValue = absoluteValue;
      absoluteValue = Double.longBitsToDouble(nextValueLong);
    }

    return Math.abs(nextValue - absoluteValue);
  }
}
