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
 * This file is part of uk.co.saiman.experiment.production.
 *
 * uk.co.saiman.experiment.production is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.production is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.requirement;

import uk.co.saiman.experiment.production.Observation;
import uk.co.saiman.experiment.production.Result;

/**
 * An input to an experiment procedure should be wired up to an observation made
 * by a preceding procedure.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the type of the result we wish to find
 */
public class ResultRequirement<T> extends ProductRequirement<Result<T>> {
  public enum Cardinality {
    SINGULAR, PLURAL
  }

  private final Cardinality cardinality;

  ResultRequirement(Observation<T> observation) {
    this(observation, Cardinality.SINGULAR);
  }

  ResultRequirement(Observation<T> observation, Cardinality cardinality) {
    super(observation);
    this.cardinality = cardinality;
  }

  public Cardinality cardinality() {
    return cardinality;
  }
}
