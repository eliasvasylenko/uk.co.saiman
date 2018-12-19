package uk.co.saiman.experiment;

public class Hold implements AutoCloseable {
  private final State state;

  public Hold(State state) {
    this.state = state;
  }

  @Override
  public void close() {
    state.releaseHold(this);
  }
}
