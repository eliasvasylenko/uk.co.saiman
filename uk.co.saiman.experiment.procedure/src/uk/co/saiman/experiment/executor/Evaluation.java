package uk.co.saiman.experiment.executor;

public enum Evaluation {
  /**
   * Evaluation of each dependent must occur during the same preparation, and must
   * also occur in order.
   */
  ORDERED,
  /**
   * Evaluation of each dependent must occur during the same preparation, but may
   * occur in any order.
   */
  UNORDERED,
  /**
   * Evaluation of each dependent must occur during the same preparation, but may
   * occur in parallel or in any order.
   */
  PARALLEL,
  /**
   * Evaluation of each dependent may occur during separate preparations, and may
   * also occur in any order.
   * <p>
   * This is the least restrictive evaluation strategy, and is also the default
   * when none is explicitly declared.
   */
  INDEPENDENT
}
