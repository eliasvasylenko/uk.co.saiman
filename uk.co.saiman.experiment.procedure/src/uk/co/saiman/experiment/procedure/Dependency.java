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
package uk.co.saiman.experiment.procedure;

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;

public class Dependency {
  public enum Kind {
    CONDITION, RESULT, ADDITIONAL_RESULT, ORDERING
  }

  private final Kind kind;
  private final Class<?> production;
  private final ExperimentPath<Absolute> from;
  private final ExperimentPath<Absolute> to;

  public Dependency(
      Kind kind,
      Class<?> production,
      ExperimentPath<Absolute> from,
      ExperimentPath<Absolute> to) {
    this.kind = kind;
    this.production = production;
    this.from = from;
    this.to = to;
  }

  public Kind kind() {
    return kind;
  }

  public Class<?> production() {
    return production;
  }

  public ExperimentPath<Absolute> from() {
    return from;
  }

  public ExperimentPath<Absolute> to() {
    return to;
  }
}
