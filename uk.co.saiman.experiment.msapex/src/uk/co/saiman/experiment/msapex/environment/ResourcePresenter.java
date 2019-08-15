package uk.co.saiman.experiment.msapex.environment;

public interface ResourcePresenter<T> {
  String getLocalizedLabel();

  String getIconURI();

  Class<T> getResourceClass();
}
