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
 * This file is part of uk.co.saiman.experiment.scheduling.
 *
 * uk.co.saiman.experiment.scheduling is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.scheduling is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.schedule;

import uk.co.saiman.experiment.path.Dependency;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.procedure.Instruction;
import uk.co.saiman.experiment.product.Condition;
import uk.co.saiman.experiment.product.Preparation;

public class ConditionImpl<T> implements Condition<T> {
  private final Preparation<T> preparation;
  private final Instruction procedure;
  private final ExperimentPath<Absolute> path;

  public ConditionImpl(
      Preparation<T> preparation,
      Instruction procedure,
      ExperimentPath<Absolute> path) {
    this.preparation = preparation;
    this.procedure = procedure;
    this.path = path;
  }

  @Override
  public Preparation<T> preparation() {
    return preparation;
  }

  @Override
  public Dependency<Condition<T>, Absolute> dependency() {
    return path.resolve(preparation());
  }
}
