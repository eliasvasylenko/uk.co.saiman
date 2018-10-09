package uk.co.saiman.experiment;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.function.Supplier;

/**
 * Utility to lock on all event sources participating in an event dispatch.
 * Avoids deadlock by selecting an arbitrary winner by locking on the class.
 * <p>
 * When we send an event it must be dispatched from the node in question as well
 * as to all ancestor nodes, so we need to lock on them all first to make sure
 * they don't shift underneath us and we dispatch events from the wrong nodes.
 * <p>
 * This almost is a neat little system, as we avoid deadlock by virtue of having
 * a natural ordering defined by the parent-child relationship. Unfortunately we
 * sometimes need to dispatch two events atomically from different stacks, in
 * particular for
 * 
 * @author Elias N Vasylenko
 */
class ExperimentLocker {
  class WorkspaceEventLock {}

  private final List<ExperimentNode<?, ?>> experimentNodes;

  public ExperimentLocker(ExperimentNode<?, ?>... experimentNodes) {
    this.experimentNodes = asList(experimentNodes);
  }

  /*
   * 
   * 
   * 
   * 
   * 
   * TODO lock on each chain of event sources in order.
   * 
   * If we get blocked by something which is already owned by another
   * WorkspaceEventLock AND we have more than one event source AND the other has
   * more than one event source then lock on this class (not instance) and
   * relinquish all locks until the other is finished.
   * 
   * 
   * 
   * 
   * 
   */

  public void run(Runnable action) {
    run(() -> {
      action.run();
      return null;
    });
  }

  public <T> T run(Supplier<T> action) {
    // TODO safely lock on all experiment nodes and their parents

    synchronized (ExperimentNode.class) {
      return action.get();
    }
  }
}