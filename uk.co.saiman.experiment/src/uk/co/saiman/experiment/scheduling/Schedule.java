package uk.co.saiman.experiment.scheduling;

import uk.co.saiman.experiment.Condition;
import uk.co.saiman.experiment.ExperimentStep;
import uk.co.saiman.experiment.Hold;
import uk.co.saiman.experiment.ProcedureContext;
import uk.co.saiman.experiment.Resource;
import uk.co.saiman.experiment.Result;

public interface Schedule {
  /**
   * Await some requirement. Generally a scheduler shouldn't need to know what
   * kind of requirement is blocking the procedure, but it may inspect the
   * associated experiment step in order to determine, for example, that it is
   * waiting on a particular input. This may inform the scheduler on which steps
   * to prioritize.
   */
  <T extends AutoCloseable> T awaitResource(ExperimentStep<?> step, Resource<T> supplier);

  /**
   * Await the completion of the given result.
   * 
   * @param result
   */
  void awaitResult(ExperimentStep<?> step, Result<?> result);

  /**
   * Await {@link ProcedureContext#enterCondition(Condition) preparation} of the
   * given condition by the parent.
   * 
   * @param condition
   */
  void awaitConditionDependency(ExperimentStep<?> step, Condition condition);

  /**
   * Await any children which are dependent on the provided condition to release
   * their {@link Hold hold}.
   * 
   * @param condition
   */
  void awaitConditionDependents(ExperimentStep<?> step, Condition condition);
}
