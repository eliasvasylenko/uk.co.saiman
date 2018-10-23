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
 * This file is part of uk.co.saiman.experiment.processing.
 *
 * uk.co.saiman.experiment.processing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.processing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.processing;

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.experiment.state.Accessor.mapAccessor;

import java.util.stream.Stream;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.experiment.state.Accessor;
import uk.co.saiman.experiment.state.StateMap;

public interface ProcessingService {
  Stream<Class<? extends DataProcessor>> types();

  <T extends DataProcessor> T createProcessor(Class<T> type);

  DataProcessor loadProcessor(StateMap persistedState);

  StateMap saveProcessor(DataProcessor processor);

  default Accessor<DataProcessor, ?> getStepAccessor(String id) {
    return mapAccessor(id, this::loadProcessor, this::saveProcessor);
  }

  default Accessor<Processing, ?> getAccessor(String id) {
    return getStepAccessor(id)
        .toListAccessor()
        .map(Processing::new, p -> p.steps().collect(toList()));
  }
}
