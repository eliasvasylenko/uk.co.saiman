/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.api.
 *
 * uk.co.saiman.experiment.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment;

import java.nio.file.Path;

import uk.co.saiman.observable.ObservableValue;
import uk.co.saiman.reflection.token.TypeArgument;
import uk.co.saiman.reflection.token.TypeToken;
import uk.co.saiman.reflection.token.TypedReference;

public interface Result<T> extends ObservableValue<T> {
  ExperimentNode<?, ?> getExperimentNode();

  /**
   * @return the path of the result data relative to the
   *         {@link Workspace#getRootPath() workspace root}.
   */
  Path getDataPath();

  /**
   * @return the absolute path of the result data from the
   *         {@link Workspace#getRootPath() workspace root}.
   */
  Path getAbsoluteDataPath();

  ResultType<T> getType();

  default TypeToken<Result<T>> getThisTypeToken() {
    return new TypeToken<Result<T>>() {}
        .withTypeArguments(new TypeArgument<T>(getType().getDataType()) {});
  }

  default TypedReference<Result<T>> asTypedObject() {
    return TypedReference.typedObject(getThisTypeToken(), this);
  }
}
