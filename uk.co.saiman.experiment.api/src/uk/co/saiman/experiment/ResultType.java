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

import uk.co.saiman.reflection.token.TypeToken;

/**
 * This class does not actually contain any result data, and instances are
 * associated with {@link ExperimentType experiment types} not
 * {@link ExperimentNode experiment nodes}. Therefore rather than being a
 * description of the result data itself, it is a description of how result data
 * is obtained from the experiment state/configuration.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of the result data
 */
public interface ResultType<T> {
  /**
   * A persistent ID for the result type.
   * 
   * @return the result type id
   */
  String getId();

  /**
   * A human readable name for the experiment result.
   * 
   * @return the result type name
   */
  String getName();

  /**
   * @return the exact static type of the experiment result data
   */
  TypeToken<T> getDataType();

  /*
   * 
   * 
   * 
   * TODO think about how we can separate result data from the actual
   * ExperimentType implementations. i.e. the data should still be loadable and
   * processable on systems without the correct particular experiment types
   * installed. This means we cannot rely on the ExperimentType which owns a
   * result to deal with creating and persisting the data as is the current
   * strategy.
   * 
   * 
   * 
   */
  T createData(ResultConfigurationContext<T> context);
}
