package uk.co.saiman.experiment.impl;

import java.io.IOException;
import java.util.stream.Stream;

import uk.co.saiman.experiment.persistence.PersistedState;

/**
 * implementations do not need to maintain identity or keep unique instances for
 * a given experiment node, but they do need to define equality.
 * 
 * @author Elias N Vasylenko
 *
 */
public interface PersistedExperiment {
  String getId();

  void setId(String id) throws IOException;

  String getTypeId();

  PersistedState getPersistedState();

  Stream<? extends PersistedExperiment> getChildren();

  PersistedExperiment addChild(String id, String typeId, PersistedState configuration, int index)
      throws IOException;

  void removeChild(String id, String typeId) throws IOException;

  @Override
  boolean equals(Object obj);

  @Override
  int hashCode();
}
