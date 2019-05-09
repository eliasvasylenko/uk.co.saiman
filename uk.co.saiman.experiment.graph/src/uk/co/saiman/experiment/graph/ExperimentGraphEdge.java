package uk.co.saiman.experiment.graph;

// TODO value and record type
public abstract class ExperimentGraphEdge {
  ExperimentGraphEdge() {}

  public static class Dependent extends ExperimentGraphEdge {
    private final ExperimentId id;

    public Dependent(ExperimentId id) {
      this.id = id;
    }

    public ExperimentId id() {
      return id;
    }
  }

  public static class Dependency extends ExperimentGraphEdge {}
}
