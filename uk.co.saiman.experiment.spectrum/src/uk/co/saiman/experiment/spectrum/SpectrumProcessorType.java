package uk.co.saiman.experiment.spectrum;

import uk.co.saiman.experiment.persistence.PersistedState;

public interface SpectrumProcessorType<T extends SpectrumProcessorConfiguration> {
  String getId();

  String getName();

  String getDescription();

  T createConfiguration(PersistedState state);
}
