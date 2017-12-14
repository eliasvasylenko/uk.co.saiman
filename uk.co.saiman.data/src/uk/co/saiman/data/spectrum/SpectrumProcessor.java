package uk.co.saiman.data.spectrum;

/**
 * Spectrum processors must be immutable.
 * 
 * @author Elias N Vasylenko
 */
public interface SpectrumProcessor {
  /**
   * The given array must be unmodified by the invocation, and must not be
   * identity equal to the return value.
   * 
   * @param data
   * @return
   */
  double[] process(double[] data);

  default SpectrumProcessor andThen(SpectrumProcessor next) {
    return d -> next.process(process(d));
  }

  static SpectrumProcessor identity() {
    return d -> d;
  }
}
