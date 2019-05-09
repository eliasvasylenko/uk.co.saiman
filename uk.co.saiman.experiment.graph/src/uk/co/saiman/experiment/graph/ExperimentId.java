package uk.co.saiman.experiment.graph;

import static java.lang.String.format;

import java.util.Objects;

// TODO value and record type
public class ExperimentId implements Comparable<ExperimentId> {
  private final String name;

  private ExperimentId(String name) {
    if (!isNameValid(name)) {
      throw new ExperimentGraphException(format("Invalid name for experiment id %s", name));
    }
    this.name = Objects.requireNonNull(name);
  }

  public static ExperimentId fromName(String name) {
    return new ExperimentId(name);
  }

  public static boolean isNameValid(String name) {
    final String ALPHANUMERIC = "[a-zA-Z0-9]+";
    final String DIVIDER_CHARACTERS = "[ \\.\\-_]+";

    return name != null
        && name.matches(ALPHANUMERIC + "(" + DIVIDER_CHARACTERS + ALPHANUMERIC + ")*");
  }

  @Override
  public String toString() {
    return name;
  }

  public String name() {
    return name;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof ExperimentId)) {
      return false;
    }

    var that = (ExperimentId) obj;

    return Objects.equals(this.name, that.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public int compareTo(ExperimentId that) {
    return this.toString().compareTo(that.toString());
  }
}
