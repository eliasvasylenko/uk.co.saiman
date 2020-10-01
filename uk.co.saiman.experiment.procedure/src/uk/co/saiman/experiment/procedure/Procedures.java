/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
package uk.co.saiman.experiment.procedure;

import java.util.Optional;

import uk.co.saiman.experiment.dependency.ResultPath;
import uk.co.saiman.experiment.executor.Evaluation;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.PlanningContext;
import uk.co.saiman.experiment.variables.VariableDeclaration;

public final class Procedures {
  private Procedures() {}

  public static void plan(Executor executor, PlanningContext context) {
    var safeContext = new PlanningContext() {
      private boolean done = false;

      private void assertLive() {
        if (done) {
          throw new IllegalStateException();
        }
      }

      @Override
      public void preparesCondition(Class<?> type, Evaluation evaluation) {
        assertLive();
        context.preparesCondition(type, evaluation);
      }

      @Override
      public void observesResult(Class<?> production) {
        assertLive();
        context.observesResult(production);
      }

      @Override
      public void executesAutomatically() {
        assertLive();
        context.executesAutomatically();
      }

      @Override
      public <T> Optional<T> declareVariable(VariableDeclaration<T> declaration) {
        assertLive();
        return context.declareVariable(declaration);
      }

      @Override
      public void declareResultRequirement(Class<?> production) {
        assertLive();
        context.declareResultRequirement(production);
      }

      @Override
      public void declareResourceRequirement(Class<?> type) {
        assertLive();
        context.declareResourceRequirement(type);
      }

      @Override
      public void declareConditionRequirement(Class<?> production) {
        assertLive();
        context.declareConditionRequirement(production);
      }

      @Override
      public void declareAdditionalResultRequirement(ResultPath<?, ?> path) {
        assertLive();
        declareAdditionalResultRequirement(path);
      }
    };
    executor.plan(safeContext);
    safeContext.done = true;
  }

  public static Procedure validateDependencies(Procedure procedure) {
    return procedure;
  }
}
