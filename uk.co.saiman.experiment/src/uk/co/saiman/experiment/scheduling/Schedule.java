/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
