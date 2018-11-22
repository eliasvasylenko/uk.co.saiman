package uk.co.saiman.experiment.path;

import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.Experiment;

public interface ExperimentIndex {
  Stream<Experiment> getExperiments();

  Optional<Experiment> getExperiment(String id);

  boolean containsExperiment(Experiment experiment);
}
