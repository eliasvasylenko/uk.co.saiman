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

import uk.co.saiman.experiment.dependency.ResultPath;
import uk.co.saiman.experiment.executor.Evaluation;
import uk.co.saiman.experiment.variables.VariableDeclaration;

public interface InstructionPlanningContext {
  default <T> void declareVariable(VariableDeclaration<T> declaration) {}

  default void declareResultRequirement(Class<?> production) {}

  default void declareConditionRequirement(Class<?> production) {}

  default void declareAdditionalResultRequirement(ResultPath<?, ?> path) {}

  default void declareResourceRequirement(Class<?> type) {}

  default void executesAutomatically() {}

  default void observesResult(Class<?> type) {}

  default void preparesCondition(Class<?> type, Evaluation evaluation) {}
}
