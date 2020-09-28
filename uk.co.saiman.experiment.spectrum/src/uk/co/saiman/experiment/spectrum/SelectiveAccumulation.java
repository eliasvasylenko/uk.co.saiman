package uk.co.saiman.experiment.spectrum;

/**
 * A strategy for the evaluation of spectrum quality for the purposes of
 * selective accumulation. For instance spectra may be selected on the basis of
 * high signal-to-noise ratio.
 * 
 * Each successive spectrum is assigned a "score". A threshold is then defined,
 * scores above which and considered of sufficient quality to accumulate. The
 * score is dimensionless and its scale is essentially arbitrary, with the only
 * requirement that it be between {@code 0} and {@link #scoreMaximum()}.
 * 
 * @author Elias N Vasylenko
 */
public interface SelectiveAccumulation {
  /**
   * @return The threshold above which a score is considered sufficient for
   *         accumulation.
   */
  double scoreThreshold();

  /**
   * @return The maximum possible score.
   */
  double scoreMaximum();

  SelectiveAccumulationFunction begin();
}
