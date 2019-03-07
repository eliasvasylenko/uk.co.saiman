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

import static java.util.Objects.requireNonNull;

import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.procedure.Procedure;

public class ExperimentEvent {
  private final Step<?, ?> step;
  private final ExperimentPath previousPath;
  private final ExperimentPath newPath;
  private final Procedure previousProcedure;
  private final Procedure newProcedure;

  public ExperimentEvent(Step<?, ?> step, ExperimentPath path, Procedure procedure) {
    this.step = requireNonNull(step);
    this.previousPath = step.getPath();
    this.newPath = requireNonNull(path);
    this.previousProcedure = step.getExperiment().getProcedure();
    this.newProcedure = requireNonNull(procedure);
  }

  public Step<?, ?> step() {
    return step;
  }

  public ExperimentPath previousPath() {
    return previousPath;
  }

  public ExperimentPath newPath() {
    return newPath;
  }

  public Procedure previousProcedure() {
    return previousProcedure;
  }

  public Procedure newProcedure() {
    return newProcedure;
  }

  @Override
  public String toString() {
    return ExperimentEvent.class.getSimpleName() + "(" + step().getId() + ")";
  }
}
