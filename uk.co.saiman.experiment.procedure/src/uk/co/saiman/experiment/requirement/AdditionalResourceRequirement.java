package uk.co.saiman.experiment.requirement;

import uk.co.saiman.experiment.dependency.Resource;
import uk.co.saiman.experiment.dependency.source.Provision;

public class AdditionalResourceRequirement<T> extends AdditionalRequirement<Resource<T>> {
  private final Provision<T> provision;

  AdditionalResourceRequirement(Provision<T> provision) {
    this.provision = provision;
  }

  public Provision<T> provision() {
    return provision;
  }
}
