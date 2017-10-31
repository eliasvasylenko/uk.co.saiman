package uk.co.saiman.experiment;

public enum ExecutionStrategy {
  /**
   * The node may be executed independently of execution of ancestor nodes, so
   * long as all result have been produced for ancestors. Workspace
   * implementations should re-execute such nodes automatically whenever their
   * configuration is changed.
   */
  RESULT_DEPENDENT,

  /**
   * The node is dependent on the context provided through the execution of the
   * parent node and so cannot be executed independently.
   */
  EXECUTION_DEPENDENT
}
