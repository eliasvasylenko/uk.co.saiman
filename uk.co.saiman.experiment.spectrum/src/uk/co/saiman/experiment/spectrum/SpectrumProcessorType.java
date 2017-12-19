package uk.co.saiman.experiment.spectrum;

import uk.co.saiman.experiment.persistence.PersistedState;

public interface SpectrumProcessorType<T extends SpectrumProcessorState> {
  default String getId() {
    return getClass().getName();
  }

  String getName();

  T configure(PersistedState state);
}
