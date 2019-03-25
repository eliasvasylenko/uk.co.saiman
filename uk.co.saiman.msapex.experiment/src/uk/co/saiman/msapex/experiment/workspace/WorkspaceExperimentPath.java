package uk.co.saiman.msapex.experiment.workspace;

import java.util.Objects;

import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;

public class WorkspaceExperimentPath implements Comparable<WorkspaceExperimentPath> {
  private final ExperimentIndex experimentIndex;
  private final ExperimentPath<Absolute> experimentPath;

  WorkspaceExperimentPath(
      ExperimentIndex experimentIndex,
      ExperimentPath<Absolute> experimentPath) {
    this.experimentIndex = experimentIndex;
    this.experimentPath = experimentPath;
  }

  public static WorkspaceExperimentPath define(
      ExperimentIndex experimentIndex,
      ExperimentPath<Absolute> experimentPath) {
    return new WorkspaceExperimentPath(experimentIndex, experimentPath);
  }

  public ExperimentIndex getExperimentIndex() {
    return experimentIndex;
  }

  public ExperimentPath<Absolute> getExperimentPath() {
    return experimentPath;
  }

  public static WorkspaceExperimentPath fromString(String string) {
    string = string.strip();

    int lastSlash = string.lastIndexOf('/');

    return define(
        ExperimentIndex.define(string.substring(lastSlash + 1)),
        ExperimentPath.absoluteFromString(string.substring(0, lastSlash)));
  }

  @Override
  public String toString() {
    return experimentIndex.toString() + experimentPath.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null || obj.getClass() != getClass())
      return false;

    var that = (WorkspaceExperimentPath) obj;

    return Objects.equals(this.experimentIndex, that.experimentIndex)
        && Objects.equals(this.experimentPath, that.experimentPath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(experimentIndex, experimentPath);
  }

  @Override
  public int compareTo(WorkspaceExperimentPath that) {
    int compareIndex = this.experimentIndex.compareTo(that.experimentIndex);
    return compareIndex != 0 ? compareIndex : this.experimentPath.compareTo(that.experimentPath);
  }
}
