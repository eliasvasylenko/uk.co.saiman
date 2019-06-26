package uk.co.saiman.instrument.sample;

/**
 * An analysis location was requested, and was reached. The
 * {@link SampleDevice#samplePosition() location} of the device should be valid.
 * <p>
 * Depending on the type of hardware this may only indicate that the analysis
 * location is reached within a certain tolerance. Therefore this state does not
 * necessarily indicate that the {@link SampleDevice#samplePosition() actual}
 * and {@link SampleDevice#requestedSampleState() requested} locations are
 * {@link #equals(Object) exactly equal}.
 */
public class Analysis<T> extends RequestedSampleState<T> {
  private final T position;

  public Analysis(T position) {
    this.position = position;
  }

  public T position() {
    return position;
  }
}