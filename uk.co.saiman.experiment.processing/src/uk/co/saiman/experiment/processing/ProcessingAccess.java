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

import static uk.co.saiman.experiment.processing.Processing.toProcessing;
import static uk.co.saiman.state.Accessor.mapAccessor;
import static uk.co.saiman.state.Accessor.stringAccessor;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.state.Accessor.ListAccessor;
import uk.co.saiman.state.Accessor.MapAccessor;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.StateMap;

public final class ProcessingAccess {
  private ProcessingAccess() {}

  private static final MapIndex<String> PROCESSOR_ID = new MapIndex<>(
      "uk.co.saiman.experiment.processor.id",
      stringAccessor());

  public static ListAccessor<Processing> processingAccessor(ProcessingService service) {
    return processorAccessor(service)
        .toStreamAccessor()
        .map(p -> p.collect(toProcessing()), Processing::steps);
  }

  public static MapAccessor<DataProcessor> processorAccessor(ProcessingService service) {
    return mapAccessor()
        .map(
            state -> processorFromState(service, state),
            processor -> processorToState(service, processor));
  }

  private static DataProcessor processorFromState(ProcessingService service, StateMap state) {
    String id = state.get(PROCESSOR_ID);
    state = state.remove(PROCESSOR_ID);

    var strategy = service.findStrategy(id).get();

    if (strategy != null) {
      return strategy.configureProcessor(state);
    } else {
      return new MissingProcessor(id);
    }
  }

  @SuppressWarnings("unchecked")
  private static StateMap processorToState(ProcessingService service, DataProcessor processor) {
    var strategy = service.findStrategy(processor.getClass()).get();

    return ((ProcessingStrategy<DataProcessor>) strategy)
        .deconfigureProcessor(processor)
        .with(PROCESSOR_ID, processor.getClass().getName());
  }
}
