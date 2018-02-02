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
