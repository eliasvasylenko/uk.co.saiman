package uk.co.saiman.experiment.dependency;

import uk.co.saiman.experiment.dependency.source.Provision;

public interface Resource<T> extends Something, AutoCloseable {
  @Override
  Provision<T> source();

  T value();

  @Override
  void close();
}
