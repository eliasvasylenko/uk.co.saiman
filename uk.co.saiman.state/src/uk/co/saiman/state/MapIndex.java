package uk.co.saiman.state;

public class MapIndex<T> {
  private final String id;
  private final Accessor<T, ?> accessor;

  public MapIndex(String id, Accessor<T, ?> accessor) {
    this.id = id;
    this.accessor = accessor;
  }

  public String id() {
    return id;
  }

  public Accessor<T, ?> accessor() {
    return accessor;
  }
}
