package uk.co.saiman.experiment.requirement;

import uk.co.saiman.experiment.dependency.Resource;

public final class Provision<T> extends Requirement<T, Resource<T>> {
  public Provision(Class<T> type) {
    super(type);
  }
}
