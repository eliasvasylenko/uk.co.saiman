package uk.co.saiman.webmodule;

import java.util.Objects;

public final class ModuleFormat {
  private final String id;

  public ModuleFormat(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    return getId();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (!(obj instanceof ModuleFormat))
      return false;

    ModuleFormat that = (ModuleFormat) obj;

    return Objects.equals(this.id, that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
