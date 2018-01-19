package uk.co.saiman.experiment.processing;

import uk.co.saiman.experiment.persistence.PersistedState;

public interface ProcessorType<T extends ProcessorState> {
  default String getId() {
    return getClass().getName();
  }

  String getName();

  T configure(PersistedState state);
}
