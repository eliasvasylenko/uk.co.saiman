package uk.co.saiman.experiment.service;

import uk.co.saiman.experiment.environment.Provision;
import uk.co.saiman.experiment.environment.Resource;

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

  @Override
  public Resource<T> provideResource() {
    // TODO Auto-generated method stub
    return null;
  }
}
