package uk.co.saiman.experiment;

public interface ResultConfigurationContext<T> {
  Resource getResource(ResultType<?> resultType);
}
