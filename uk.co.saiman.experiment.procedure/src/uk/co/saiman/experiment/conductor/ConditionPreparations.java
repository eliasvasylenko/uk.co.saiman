package uk.co.saiman.experiment.conductor;

import static java.lang.String.format;
import static java.util.stream.Collectors.toCollection;
import static uk.co.saiman.experiment.procedure.Dependency.Kind.CONDITION;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.experiment.dependency.Condition;
import uk.co.saiman.experiment.executor.ExecutionCancelledException;
import uk.co.saiman.experiment.procedure.InstructionDependencies;

public class ConditionPreparations {
  private final Lock lock;
  private final Supplier<InstructionDependencies> dependencies;

  private final java.util.concurrent.locks.Condition consumer;

  private Class<?> preparedCondition;
  private Object preparedConditionValue;

  private ConditionImpl<?> preparedConditionImpl;

  private final Set<Class<?>> preparedConditions = new HashSet<>();

  public ConditionPreparations(Lock lock, Supplier<InstructionDependencies> dependencies) {
    this.lock = lock;
    this.dependencies = dependencies;
  }

  public <T> void prepare(Class<T> condition, T resource) {
    lock.lock();
    try {
      System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

      var conditionDependents = dependencies
          .get()
          .getDependenciesTo()
          .filter(d -> d.production() == condition && d.kind() == CONDITION)
          .collect(toCollection(HashSet::new));
      System.out.println("prepare condition for " + conditionDependents);
      if (conditionDependents.isEmpty()) {
        return;
      }
      try {
        preparedCondition = condition;
        preparedConditionValue = resource;
        do {
          System.out.println("prepare condition for " + conditionDependents);
          conductor.notifyAll();
          conductor.wait();
        } while (dependencies.getConditionDependents(condition).findAny().isPresent());
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } finally {
        preparedConditions.add(condition);
        preparedCondition = null;
        preparedConditionValue = null;
      }
    } finally {
      lock.unlock();
    }
  }

  protected <T> Condition<T> consume(Class<T> source, InstructionExecution consumer) {
    lock.lock();
    try {
      while (source != preparedCondition && isReady(source, consumer)) {
        if (preparedConditions.contains(source) || !isRunning()) {
          throw new ConductorException(
              format(
                  "Failed to acquire condition %s from instruction %s",
                  source,
                  instruction.path()));
        }
        try {
          System.out.println("await condition " + source);
          System.out.println("prepared " + preparedCondition);
          System.out.println("prepared in " + instruction.path());
          conductor.wait();
        } catch (InterruptedException e) {
          throw new ExecutionCancelledException(e);
        }
      }
      return (Condition<T>) preparedConditionValue;
    } finally {
      lock.unlock();
    }
  }

  public Stream<InstructionExecution> consumers() {
    // TODO Auto-generated method stub
    return null;
  }
}
