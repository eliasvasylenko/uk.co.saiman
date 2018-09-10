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
import static uk.co.saiman.experiment.processing.Processor.PROCESSOR_ID_KEY;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.processing.MissingProcessorType;
import uk.co.saiman.experiment.processing.ProcessingProperties;
import uk.co.saiman.experiment.processing.Processor;
import uk.co.saiman.experiment.processing.ProcessorService;
import uk.co.saiman.experiment.state.StateMap;
import uk.co.saiman.properties.PropertyLoader;

@Component
public class ProcessorServiceImpl implements ProcessorService {
  private final Map<String, Processor> processors = new HashMap<>();

  @Reference
  PropertyLoader propertyLoader;

  @Reference(cardinality = MULTIPLE, policy = DYNAMIC)
  void addProcessingType(Processor type) {
    processors.putIfAbsent(type.getId(), type);
  }

  void removeProcessingType(Processor type) {
    processors.remove(type.getId());
  }

  @Override
  public Processor loadProcessor(StateMap persistedState) {
    return processors
        .computeIfAbsent(
            persistedState.get(PROCESSOR_ID_KEY).asProperty().getValue(),
            id -> new MissingProcessorType(
                id,
                propertyLoader.getProperties(ProcessingProperties.class)))
        .withState(persistedState);
  }
}
