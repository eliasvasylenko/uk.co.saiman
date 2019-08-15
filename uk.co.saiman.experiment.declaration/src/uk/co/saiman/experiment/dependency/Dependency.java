package uk.co.saiman.experiment.dependency;

public interface Dependency<T> {
  Class<T> type();
}
