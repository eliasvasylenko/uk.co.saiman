package uk.co.saiman.experiment.impl;

import java.io.IOException;
import java.util.stream.Stream;

import uk.co.saiman.experiment.persistence.PersistedState;

public interface PersistedExperiment {
  String getId();

  void setId(String id) throws IOException;

  String getTypeId();

  PersistedState getPersistedState();

  Stream<PersistedExperiment> getChildren() throws IOException;

  PersistedExperiment addChild(int index, String typeId, PersistedState initialState)
      throws IOException;

  void removeChild(PersistedExperiment child) throws IOException;
}
