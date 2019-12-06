package uk.co.saiman.experiment.executor;

import java.util.Optional;

import uk.co.saiman.experiment.dependency.ResultPath;
import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.experiment.variables.VariableCardinality;
import uk.co.saiman.experiment.variables.VariableDeclaration;

public interface PlanningContext {
  <T> Optional<T> declareVariable(VariableDeclaration<T> declaration);

  default <T> Optional<T> declareVariable(Variable<T> variable, VariableCardinality cardinality) {
    return declareVariable(new VariableDeclaration<>(variable, cardinality));
  }

  void declareResultRequirement(Class<?> production);

  void declareConditionRequirement(Class<?> production);

  void declareAdditionalResultRequirement(ResultPath<?, ?> path);

  void declareResourceRequirement(Class<?> type);

  void executesAutomatically();

  void observesResult(Class<?> production);

  default void preparesCondition(Class<?> type) {
    preparesCondition(type, Evaluation.INDEPENDENT);
  }

  void preparesCondition(Class<?> type, Evaluation evaluation);

  interface NoOpPlanningContext extends PlanningContext {
    @Override
    default void declareResultRequirement(Class<?> production) {}

    @Override
    default void declareConditionRequirement(Class<?> production) {}

    @Override
    default void declareAdditionalResultRequirement(ResultPath<?, ?> path) {}

    @Override
    default void declareResourceRequirement(Class<?> type) {}

    @Override
    default void executesAutomatically() {}

    @Override
    default void observesResult(Class<?> production) {}

    @Override
    default void preparesCondition(Class<?> type, Evaluation evaluation) {}
  }

  public default void useOnce(Executor executor) {
    var parent = this;
    var context = new PlanningContext() {
      private boolean done = false;

      private void assertLive() {
        if (done) {
          throw new IllegalStateException();
        }
      }

      @Override
      public void preparesCondition(Class<?> type, Evaluation evaluation) {
        assertLive();
        parent.preparesCondition(type, evaluation);
      }

      @Override
      public void observesResult(Class<?> production) {
        assertLive();
        parent.observesResult(production);
      }

      @Override
      public void executesAutomatically() {
        assertLive();
        parent.executesAutomatically();
      }

      @Override
      public <T> Optional<T> declareVariable(VariableDeclaration<T> declaration) {
        assertLive();
        return parent.declareVariable(declaration);
      }

      @Override
      public void declareResultRequirement(Class<?> production) {
        assertLive();
        parent.declareResultRequirement(production);
      }

      @Override
      public void declareResourceRequirement(Class<?> type) {
        assertLive();
        parent.declareResourceRequirement(type);
      }

      @Override
      public void declareConditionRequirement(Class<?> production) {
        assertLive();
        parent.declareConditionRequirement(production);
      }

      @Override
      public void declareAdditionalResultRequirement(ResultPath<?, ?> path) {
        assertLive();
        declareAdditionalResultRequirement(path);
      }
    };
    executor.plan(context);
    context.done = true;
  }
}
