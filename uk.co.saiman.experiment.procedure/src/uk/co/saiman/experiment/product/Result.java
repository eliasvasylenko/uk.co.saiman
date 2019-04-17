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
package uk.co.saiman.experiment.product;

import java.util.Optional;

import uk.co.saiman.experiment.path.Dependency;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.observable.Observable;

/**
 * A result which may be produced by an observation during the conducting of an
 * experiment procedure.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the data type of the result
 */
public interface Result<T> extends Product {
  Observable<Result<T>> updates();

  Observation<T> observation();

  Dependency<Result<T>, Absolute> dependency();

  @Override
  default ProductPath<Absolute> path() {
    return dependency().getProductPath();
  }

  boolean isEmpty();

  boolean isComplete();

  boolean isPartial();

  Optional<T> value();
}
