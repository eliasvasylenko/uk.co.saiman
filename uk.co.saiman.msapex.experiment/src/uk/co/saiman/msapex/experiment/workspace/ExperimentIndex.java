package uk.co.saiman.msapex.experiment.workspace;

import java.util.Objects;

public class ExperimentIndex implements Comparable<ExperimentIndex> {
  private final String experimentId;

  ExperimentIndex(String experimentId) {
    this.experimentId = experimentId;
  }

  public static ExperimentIndex define(String experimentId) {
    return new ExperimentIndex(experimentId);
  }

  public String getexperimentId() {
    return experimentId;
  }

  public static ExperimentIndex fromString(String string) {
    return define(string.strip());
  }

  @Override
  public String toString() {
    return experimentId;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null || obj.getClass() != getClass())
      return false;

    var that = (ExperimentIndex) obj;

    return Objects.equals(this.experimentId, that.experimentId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(experimentId);
  }

  @Override
  public int compareTo(ExperimentIndex that) {
    return this.experimentId.compareTo(that.experimentId);
  }
}
