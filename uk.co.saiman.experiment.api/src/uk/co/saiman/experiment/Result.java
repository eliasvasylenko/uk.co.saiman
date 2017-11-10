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

import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.observable.ObservableValue;
import uk.co.saiman.reflection.token.TypeArgument;
import uk.co.saiman.reflection.token.TypeToken;
import uk.co.saiman.reflection.token.TypedReference;

/**
 * TODO this should probably be weak-referenced to the actual data object, with
 * a way to load (and save?) according to whatever {@link DataFormat format} it
 * was originally saved as or is appropriate. Otherwise there will certainly be
 * memory usage issues. So how can this work alongside the observable value
 * aspect? Presumably if any observers are attached this will count as a
 * reference to the data object? Or will they just be sent a fail event after
 * it's cleaned up by GC?
 * 
 * TODO also consider that the data should still be loadable even if the
 * ExperimentNode type is {@link MissingExperimentType missing}.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the data type of the result
 */
public interface Result<T> extends ObservableValue<T> {
  ExperimentNode<?, ?> getExperimentNode();

  TypeToken<T> getType();

  default TypeToken<Result<T>> getThisTypeToken() {
    return new TypeToken<Result<T>>() {}.withTypeArguments(new TypeArgument<T>(getType()) {});
  }

  default TypedReference<Result<T>> asTypedObject() {
    return TypedReference.typedObject(getThisTypeToken(), this);
  }
}
