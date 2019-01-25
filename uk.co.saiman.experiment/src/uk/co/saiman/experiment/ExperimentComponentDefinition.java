package uk.co.saiman.experiment;

public class ExperimentComponentDefinition {
  private final Capability<?> capability;
  private final int index;
  private final ExperimentStepDefinition<?> step;

  ExperimentComponentDefinition(
      Capability<?> capability,
      int index,
      ExperimentStepDefinition<?> step) {
    this.capability = capability;
    this.index = index;
    this.step = step;
  }

  public Capability<?> capability() {
    return capability;
  }

  public int index() {
    return index;
  }

  public ExperimentStepDefinition<?> step() {
    return step;
  }
}
