package uk.co.saiman.experiment.processing;

import uk.co.saiman.experiment.persistence.PersistedState;

public interface ProcessorService {
  ProcessorState loadProcessorState(PersistedState persistedState);
}
