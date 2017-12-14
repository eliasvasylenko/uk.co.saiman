package uk.co.saiman.experiment.spectrum;

import uk.co.saiman.data.spectrum.SpectrumProcessor;
import uk.co.saiman.experiment.persistence.PersistedState;

public interface SpectrumProcessorType {
  String getId();

  String getName();

  SpectrumProcessor getProcessor();

  void save(PersistedState state);

  SpectrumProcessorType load(PersistedState state);
}
