package uk.co.saiman.experiment.variables;

import java.util.Optional;
import java.util.function.Function;

import uk.co.saiman.state.StateMap;

public interface Variables {
  StateMap state();

  <T> Optional<T> variable(Variable<T> variable);

  <U> Variables withVariable(Variable<U> variable, U value);

  default <U> Variables withVariable(Variable<U> variable, Function<Optional<U>, U> value) {
    return withVariable(variable, value.apply(variable(variable)));
  }
}
