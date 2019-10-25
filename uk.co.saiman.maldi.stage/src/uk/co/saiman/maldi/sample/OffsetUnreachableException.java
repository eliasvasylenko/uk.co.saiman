package uk.co.saiman.maldi.sample;

import javax.measure.quantity.Length;

import uk.co.saiman.measurement.coordinate.XYCoordinate;

public class OffsetUnreachableException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private final XYCoordinate<Length> offset;

  public OffsetUnreachableException(XYCoordinate<Length> offset) {
    super(offset.toString());
    this.offset = offset;
  }

  public XYCoordinate<Length> getOffset() {
    return offset;
  }
}
