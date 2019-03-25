package uk.co.saiman.state;

public class ListIndex<T> {
  private final int position;
  private final Accessor<T, ?> accessor;

  public ListIndex(int position, Accessor<T, ?> accessor) {
    this.position = position;
    this.accessor = accessor;
  }

  public int position() {
    return position;
  }

  public Accessor<T, ?> accessor() {
    return accessor;
  }
}
