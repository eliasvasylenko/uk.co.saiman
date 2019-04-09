package uk.co.saiman.experiment.variables;

import java.util.Optional;
import java.util.function.Function;

import uk.co.saiman.state.StateMap;

/**
 * An immutable container for experiment procedure variables.
 * 
 * @author Elias N Vasylenko
 */
public class Variables {
  private final StateMap state;

  public Variables() {
    this.state = StateMap.empty();
  }

  public Variables(StateMap state) {
    this.state = state;
  }

  public StateMap state() {
    return state;
  }

  public <T> Optional<T> get(Variable<T> variable) {
    return state.getOptional(variable.mapIndex());
  }

  public <U> Variables with(Variable<U> variable, U value) {
    return new Variables(state.with(variable.mapIndex(), value));
  }

  public <U> Variables with(Variable<U> variable, Function<Optional<U>, U> value) {
    return new Variables(state.with(variable.mapIndex(), value.apply(get(variable))));
  }
}
