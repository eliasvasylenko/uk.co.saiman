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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.experiment.environment.osgi.SharedResource;
import uk.co.saiman.experiment.processing.ProcessingService;
import uk.co.saiman.experiment.processing.ProcessingStrategy;

@Component
@SharedResource
public class ProcessingServiceImpl implements ProcessingService {
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
  public Stream<ProcessingStrategy<?>> strategies() {
    return processors.values().stream();
  }

  @Override
  public Optional<ProcessingStrategy<?>> findStrategy(String id) {
    return Optional.ofNullable(namedProcessors.get(id));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends DataProcessor> Optional<ProcessingStrategy<T>> findStrategy(Class<T> type) {
    return Optional.ofNullable(processors.get(type)).map(s -> (ProcessingStrategy<T>) s);
  }
}
