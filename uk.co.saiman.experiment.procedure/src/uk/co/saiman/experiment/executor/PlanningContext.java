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
package uk.co.saiman.experiment.executor;

import static uk.co.saiman.experiment.executor.Evaluation.SEPARATE;

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
    preparesCondition(type, SEPARATE);
  }

  void preparesCondition(Class<?> type, Evaluation evaluation);
}
