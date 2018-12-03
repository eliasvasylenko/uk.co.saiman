package uk.co.saiman.experiment;

import java.util.Objects;

/**
 * A condition which is provided by an experiment procedure.
 * 
 * @author Elias N Vasylenko
 */
public class Condition {
  private final String id;

  public Condition(String id) {
    this.id = id;
  }

  public String id() {
    return id;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || obj.getClass() != getClass()) {
      return false;
    }
    Condition that = (Condition) obj;
    return Objects.equals(this.id, that.id);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" + id + ")";
  }
}
