package uk.co.saiman.acquisition;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

public interface AcquisitionControl extends AutoCloseable {
  /**
   * Begin an acquisition experiment with the current configuration.
   * 
   * @throws IllegalStateException if acquisition is already in progress
   */
  void startAcquisition();

  /**
   * Set the total acquisition count for a single experiment.
   * 
   * @param count the number of continua to acquire for a single experiment
   */
  void setAcquisitionCount(int count);

  /**
   * Set the active sampling duration for a single data acquisition event. This
   * may adjust the acquisition depth to fit according to the current acquisition
   * resolution.
   * 
   * @param time the time an acquisition will last in milliseconds
   */
  void setAcquisitionTime(Quantity<Time> time);

  /**
   * Set the number of samples in an acquired sampled continuous function. This
   * may adjust the acquisition time to fit according to the current acquisition
   * resolution.
   * 
   * @param depth the sample depth for an acquired data array
   */
  void setSampleDepth(int depth);

  @Override
  void close();
}
