package uk.co.saiman.experiment.environment;

public interface StaticEnvironment {
  <T> T getStaticValue(Provision<T> provision);
}
