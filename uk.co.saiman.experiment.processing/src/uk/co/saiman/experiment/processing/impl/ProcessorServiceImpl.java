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
import static uk.co.saiman.experiment.processing.ProcessorState.PROCESSOR_TYPE_KEY;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.experiment.processing.MissingProcessorType;
import uk.co.saiman.experiment.processing.ProcessingProperties;
import uk.co.saiman.experiment.processing.ProcessorService;
import uk.co.saiman.experiment.processing.ProcessorState;
import uk.co.saiman.experiment.processing.ProcessorType;
import uk.co.saiman.text.properties.PropertyLoader;

@Component
public class ProcessorServiceImpl implements ProcessorService {
  private final Map<String, ProcessorType<?>> processingTypes = new HashMap<>();

  @Reference
  PropertyLoader propertyLoader;

  @Reference(cardinality = MULTIPLE, policy = DYNAMIC)
  void addProcessingType(ProcessorType<?> type) {
    processingTypes.putIfAbsent(type.getId(), type);
  }

  void removeProcessingType(ProcessorType<?> type) {
    processingTypes.remove(type.getId());
  }

  public ProcessorState loadProcessorState(PersistedState persistedState) {
    return processingTypes
        .computeIfAbsent(
            persistedState.forString(PROCESSOR_TYPE_KEY).get(),
            id -> new MissingProcessorType(
                id,
                propertyLoader.getProperties(ProcessingProperties.class)))
        .configure(persistedState);
  }
}
