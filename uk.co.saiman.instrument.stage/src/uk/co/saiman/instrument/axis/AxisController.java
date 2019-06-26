package uk.co.saiman.instrument.axis;

import javax.measure.Quantity;

import uk.co.saiman.instrument.Controller;

public interface AxisController<T extends Quantity<T>> extends Controller {
  /**
   * Initiate a request and return immediately. Throws an exception if the axis is
   * currently attempting to fulfill a previous request (i.e.
   * {@link AxisState#LOCATION_REQUESTED}).
   * 
   * @param location
   */
  void requestLocation(Quantity<T> location);
}
