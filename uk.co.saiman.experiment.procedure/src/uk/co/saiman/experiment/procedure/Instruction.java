package uk.co.saiman.experiment.procedure;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import uk.co.saiman.state.StateMap;

public class Instruction {
  private final String id;
  private final StateMap state;
  private final Conductor<?, ?> conductor;

  private Instruction(String id, StateMap state, Conductor<?, ?> conductor) {
    this.id = id;
    this.state = state;
    this.conductor = conductor;
  }

  public static Instruction define(String id, StateMap state, Conductor<?, ?> conductor) {
    return new Instruction(
        Procedure.validateName(id),
        requireNonNull(state),
        requireNonNull(conductor));
  }

  public String id() {
    return id;
  }

  public Instruction withId(String id) {
    return new Instruction(Procedure.validateName(id), state, conductor);
  }

  public Conductor<?, ?> conductor() {
    return conductor;
  }

  public StateMap state() {
    return state;
  }

  public Instruction withState(StateMap state) {
    return new Instruction(id, requireNonNull(state), conductor);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (obj.getClass() != getClass())
      return false;

    Instruction that = (Instruction) obj;

    return Objects.equals(this.id, that.id)
        && Objects.equals(this.state, that.state)
        && Objects.equals(this.conductor, that.conductor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, state, conductor);
  }
}
