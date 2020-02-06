package uk.co.saiman.experiment.conductor;

import static java.util.stream.Collectors.toCollection;

import java.util.HashSet;
import java.util.Set;

import uk.co.saiman.experiment.procedure.InstructionDependents;

public class ConditionPreparations {

  private Class<?> preparedCondition;
  private Object preparedConditionValue;

  private ConditionImpl<?> preparedConditionImpl;

  private final Set<Class<?>> preparedConditions = new HashSet<>();

  public ConditionPreparations() {
    // TODO Auto-generated constructor stub
  }
  
  public <U> void prepare(Class<U> condition, U resource) {

    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    synchronized (conductor) {
      var conditionDependents = dependencies
          .getConditionDependents(condition)
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
    }
  }
}
