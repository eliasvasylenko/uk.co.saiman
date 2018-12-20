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
