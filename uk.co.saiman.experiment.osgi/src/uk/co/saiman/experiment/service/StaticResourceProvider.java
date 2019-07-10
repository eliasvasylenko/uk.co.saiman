package uk.co.saiman.experiment.service;

import uk.co.saiman.experiment.environment.Provision;

public class StaticResourceProvider<T> implements ResourceProvider<T> {
  private Provision<T> provision;
  private T value;

  public StaticResourceProvider(Provision<T> provision, T value) {
    this.provision = provision;
    this.value = value;
  }

  @Override
  public Provision<T> provision() {
    return provision;
  }

  public T value() {
    return value;
  }
}
