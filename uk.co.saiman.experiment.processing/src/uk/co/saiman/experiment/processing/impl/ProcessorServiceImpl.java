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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.processing.MissingProcessorType;
import uk.co.saiman.experiment.processing.ProcessorConfiguration;
import uk.co.saiman.experiment.processing.ProcessorService;
import uk.co.saiman.experiment.state.Accessor;
import uk.co.saiman.experiment.state.StateMap;

@Component
public class ProcessorServiceImpl implements ProcessorService {
  private static final Accessor<String, ?> PROCESSOR_ID = stringAccessor(PROCESSOR_ID_KEY);

  private final Map<String, ProcessorConfiguration> processors = new HashMap<>();

  @Reference(cardinality = MULTIPLE, policy = DYNAMIC)
  void addProcessingType(ProcessorConfiguration type) {
    processors.putIfAbsent(type.getId(), type);
  }

  void removeProcessingType(ProcessorConfiguration type) {
    processors.remove(type.getId());
  }

  @Override
  public ProcessorConfiguration loadProcessor(StateMap persistedState) {
    return processors
        .computeIfAbsent(persistedState.get(PROCESSOR_ID), id -> new MissingProcessorType(id))
        .withState(persistedState);
  }

  @Override
  public StateMap saveProcessor(ProcessorConfiguration processor) {
    return processor.getState().with(PROCESSOR_ID, processor.getId());
  }
}
