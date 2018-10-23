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
package uk.co.saiman.experiment.processing.impl;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;
import static uk.co.saiman.experiment.state.Accessor.stringAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.experiment.processing.MissingProcessor;
import uk.co.saiman.experiment.processing.ProcessingService;
import uk.co.saiman.experiment.processing.ProcessingStrategy;
import uk.co.saiman.experiment.state.Accessor.PropertyAccessor;
import uk.co.saiman.experiment.state.StateMap;

@Component
public class ProcessingServiceImpl implements ProcessingService {
  private static final String PROCESSOR_ID_KEY = "uk.co.saiman.experiment.processor.id";
  private static final PropertyAccessor<String> PROCESSOR_ID = stringAccessor(PROCESSOR_ID_KEY);

  private final Map<Class<? extends DataProcessor>, ProcessingStrategy<?>> processors = new HashMap<>();
  private final Map<String, ProcessingStrategy<?>> namedProcessors = new HashMap<>();

  @Reference(cardinality = MULTIPLE, policy = DYNAMIC)
  void addProcessingType(ProcessingStrategy<?> type) {
    processors.putIfAbsent(type.getType(), type);
    namedProcessors.putIfAbsent(type.getType().getName(), type);
  }

  void removeProcessingType(ProcessingStrategy<?> type) {
    processors.remove(type.getType(), type);
    namedProcessors.remove(type.getType().getName(), type);
  }

  @Override
  public DataProcessor loadProcessor(StateMap persistedState) {
    String id = persistedState.get(PROCESSOR_ID);
    ProcessingStrategy<?> process = namedProcessors.get(id);

    if (process != null) {
      return process.configureProcessor(persistedState);
    } else {
      return new MissingProcessor(id);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public StateMap saveProcessor(DataProcessor processor) {
    ProcessingStrategy<?> process = processors.get(processor.getClass());
    return ((ProcessingStrategy<DataProcessor>) process)
        .deconfigureProcessor(processor)
        .with(PROCESSOR_ID, processor.getClass().getName());
  }

  @Override
  public Stream<Class<? extends DataProcessor>> types() {
    return processors.keySet().stream();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends DataProcessor> T createProcessor(Class<T> type) {
    return (T) processors.get(type).createProcessor();
  }
}
