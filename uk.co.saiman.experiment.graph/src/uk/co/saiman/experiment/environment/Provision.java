package uk.co.saiman.experiment.environment;

public class Provision<T> {
  private final String id;

  public Provision(String id) {
    this.id = id;
  }

  public String id() {
    return id;
  }
}
