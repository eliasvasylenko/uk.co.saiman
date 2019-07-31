package uk.co.saiman.experiment.requirement;

import java.util.Objects;

import uk.co.saiman.experiment.dependency.Resource;
import uk.co.saiman.experiment.dependency.source.Provision;

public class ResourceRequirement<T> extends SomeRequirement<Resource<T>> {
  private final Provision<T> provision;

  ResourceRequirement(Provision<T> provision) {
    this.provision = provision;
  }

  public Provision<T> provision() {
    return provision;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof ResourceRequirement<?>)) {
      return false;
    }

    var that = (ResourceRequirement<?>) obj;

    return Objects.equals(this.provision, that.provision);
  }

  @Override
  public int hashCode() {
    return provision.hashCode();
  }
}
