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
 * This file is part of uk.co.saiman.experiment.declaration.
 *
 * uk.co.saiman.experiment.declaration is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.declaration is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.dependency;

import java.util.Optional;

import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.observable.Observable;

/**
 * A result which may be produced by an observation during the conducting of an
 * experiment procedure.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the data type of the result
 */
public interface Result<T> extends Product<T> {
  static enum ResultStatus {
    /**
     * The result is not yet observed
     */
    READY,
    /**
     * The result is being observed and a partial value is available
     */
    OBSERVING,
    /**
     * The result was observed and a complete value is available
     */
    OBSERVED,
    /**
     * The result was not observed, something went wrong
     */
    FAILED
  }

  ResultStatus status();

  @Override
  ResultPath<Absolute, T> path();

  Optional<T> value();

  Observable<Result<T>> updates();
}
