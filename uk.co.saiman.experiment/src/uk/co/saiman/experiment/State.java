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
package uk.co.saiman.experiment;

import java.util.HashSet;
import java.util.Set;

public class State {
  private final ExperimentStep<?> experimentStep;
  private final Condition condition;
  private final Set<Hold> holds;

  public State(ExperimentStep<?> experimentStep, Condition condition) {
    this.experimentStep = experimentStep;
    this.condition = condition;
    this.holds = new HashSet<>();
  }

  public ExperimentStep<?> getExperimentStep() {
    return experimentStep;
  }

  public Condition getCondition() {
    return condition;
  }

  void enter() {
    // TODO Auto-generated method stub

  }

  void exit() {
    // TODO Auto-generated method stub

  }

  Hold takeHold() {
    var hold = new Hold(this);
    holds.add(hold);
    return hold;
  }

  void releaseHold(Hold hold) {
    holds.remove(hold);
  }
}
