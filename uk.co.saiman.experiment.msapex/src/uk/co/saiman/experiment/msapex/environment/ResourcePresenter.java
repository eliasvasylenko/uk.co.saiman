package uk.co.saiman.experiment.msapex.environment;

import uk.co.saiman.experiment.dependency.source.Provision;

public interface ResourcePresenter<T> {
  String getLocalizedLabel();

  String getIconURI();

  Class<? super T> getResourceClass();

  Provision<T> getProvision();
}
