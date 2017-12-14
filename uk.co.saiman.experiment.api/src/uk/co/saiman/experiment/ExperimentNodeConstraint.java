package uk.co.saiman.experiment;

/**
 * A representation of whether a constraint on the structure of and experiment
 * node tree is fulfilled.
 * <p>
 * Each node is optionally constrained with every one of its ancestor and
 * descendant nodes via the
 * {@link ExperimentType#mayComeBefore(ExperimentNode, ExperimentType)} and
 * {@link ExperimentType#mayComeAfter(ExperimentNode)} methods.
 * 
 * Taking the set of results of all relevant invocations for a given node type
 * and node graph location, the following conditions are applied to determine
 * whether a node fulfills its constraints and my be added to the graph:
 * 
 * <ul>
 * <li>If the set contains at least one instance of {@link #VIOLATED} then the
 * node <em>does not</em> fulfill its constraints.</li>
 * <li>Else if the set contains at least one instance of
 * {@link #ASSUME_ALL_FULFILLED} then the node fulfills its constraints.</li>
 * <li>Else if the set contains at least one instance of {@link #UNFULFILLED}
 * then the node <em>does not</em> fulfill its constraints.</li>
 * <li>Else the node fulfills its constraints.</li>
 * </ul>
 * 
 * @author Elias N Vasylenko
 */
public enum ExperimentNodeConstraint {
  /**
   * A negative condition is present
   */
  VIOLATED,
  /**
   * A positive condition is missing
   */
  UNFULFILLED,
  /**
   * No positive conditions are missing
   */
  FULFILLED,
  /**
   * Missing positive conditions should be ignored
   */
  ASSUME_ALL_FULFILLED;
}
