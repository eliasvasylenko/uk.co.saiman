package uk.co.saiman.experiment.impl;

import java.io.IOException;

import uk.co.saiman.data.resource.Location;

public interface ExperimentLocationManager {
  void removeLocation(ExperimentNodeImpl<?, ?> node) throws IOException;

  void updateLocation(ExperimentNodeImpl<?, ?> node, String id) throws IOException;

  Location getLocation(ExperimentNodeImpl<?, ?> node) throws IOException;
}
