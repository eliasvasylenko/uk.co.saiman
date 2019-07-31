package uk.co.saiman.experiment.dependency.source;

import uk.co.saiman.experiment.dependency.Resource;

public class Provision<T> extends Source<Resource<? extends T>> {
  private final String id;

  public Provision(String id) {
    this.id = id;
  }

  @Override
  public String id() {
    return id;
  }
}
