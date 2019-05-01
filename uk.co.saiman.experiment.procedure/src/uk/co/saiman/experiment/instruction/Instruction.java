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
package uk.co.saiman.experiment.instruction;

import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.production.Dependency;
import uk.co.saiman.experiment.variables.Variables;

public class Instruction<T extends Dependency> {
  private final String id;
  private final Variables variables;
  private final Executor<T> executor;
  private final ExperimentPath<Absolute> path;

  public Instruction(
      String id,
      Variables variables,
      Executor<T> executor,
      ExperimentPath<Absolute> path) {
    this.id = id;
    this.variables = variables;
    this.executor = executor;
    this.path = path;
  }

  public String id() {
    return id;
  }

  public Variables variables() {
    return variables;
  }

  public Executor<T> executor() {
    return executor;
  }

  public ExperimentPath<Absolute> path() {
    return path;
  }
}
