package uk.co.saiman.experiment.environment;

public interface Environment extends StaticEnvironment, AutoCloseable {
  <T> Resource<T> provideResource(Provision<T> provision);
}
