package uk.co.saiman.experiment.scheduling.concurrent;

import uk.co.saiman.experiment.Condition;
import uk.co.saiman.experiment.ExperimentStep;
import uk.co.saiman.experiment.Resource;
import uk.co.saiman.experiment.Result;
import uk.co.saiman.experiment.scheduling.Schedule;

public class ConcurrentSchedule implements Schedule {
  @Override
  public <T extends AutoCloseable> T awaitResource(ExperimentStep<?> step, Resource<T> supplier) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void awaitResult(ExperimentStep<?> step, Result<?> result) {
    // TODO Auto-generated method stub

  }

  @Override
  public void awaitConditionDependency(ExperimentStep<?> step, Condition condition) {
    // TODO Auto-generated method stub

  }

  @Override
  public void awaitConditionDependents(ExperimentStep<?> step, Condition condition) {
    // TODO Auto-generated method stub

  }
}
