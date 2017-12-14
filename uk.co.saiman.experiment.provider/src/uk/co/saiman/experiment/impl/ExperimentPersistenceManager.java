package uk.co.saiman.experiment.impl;

import java.io.IOException;
import java.util.stream.Stream;

import uk.co.saiman.experiment.ExperimentType;

public interface ExperimentPersistenceManager {
  Stream<ExperimentType<?, ?>> getExperimentTypes();

  Stream<PersistedExperiment> getExperiments() throws IOException;

  PersistedExperiment addExperiment(String id, String typeId) throws IOException;

  void removeExperiment(PersistedExperiment experiment) throws IOException;
}
