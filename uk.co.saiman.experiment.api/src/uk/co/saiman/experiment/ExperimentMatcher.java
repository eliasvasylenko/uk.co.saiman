package uk.co.saiman.experiment;

/**
 * Currently the {@link ExperimentPathTest path} only matches each child by ID.
 * This could be expanded to XPath like behavior with different matching
 * strategies.
 */
public class ExperimentMatcher {
  private final String id;

  ExperimentMatcher(String id) {
    this.id = id;
  }

  public static ExperimentMatcher matching(ExperimentNode<?, ?> node) {
    return new ExperimentMatcher(node.getId());
  }

  public boolean match(ExperimentNode<?, ?> node) {
    return id.equals(node.getId());
  }

  @Override
  public String toString() {
    return id;
  }

  public static ExperimentMatcher fromString(String string) {
    return new ExperimentMatcher(string);
  }
}
