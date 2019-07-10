package uk.co.saiman.experiment.environment;

import uk.co.saiman.experiment.dependency.Something;

public interface Resource<T> extends Something, AutoCloseable {
  T getValue() throws ResourceClosedException;
}
