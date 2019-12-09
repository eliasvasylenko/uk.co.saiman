/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.experiment.procedure.
 *
 * uk.co.saiman.experiment.procedure is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.procedure is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
