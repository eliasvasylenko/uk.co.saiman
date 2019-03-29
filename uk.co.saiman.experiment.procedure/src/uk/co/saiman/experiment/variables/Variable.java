package uk.co.saiman.experiment.variables;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import uk.co.saiman.state.Accessor;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.State;

/**
 * A variable is simply a representation of an API point. In particular, it
 * represents a type of {@link State state} which may be associated with an
 * instruction, and the Java type which the state may be materialized as.
 * <p>
 * Variable instances are intended to be static, and do not prescribe the method
 * of converting between state and object, though they may suggest a default
 * strategy.
 */
public class Variable<T> {
  private final String id;
  private final Accessor<T, ?> accessor;

  public Variable(String id) {
    this.id = requireNonNull(id);
    this.accessor = null;
  }

  public Variable(String id, Accessor<T, ?> accessor) {
    this.id = requireNonNull(id);
    this.accessor = requireNonNull(accessor);
  }

  public String id() {
    return id;
  }

  public Optional<MapIndex<T>> defaultMapIndex() {
    return Optional.ofNullable(accessor).map(a -> new MapIndex<>(id, a));
  }

  public VariableDeclaration declareRequired() {
    return declare(VariableCardinality.REQUIRED);
  }

  public VariableDeclaration declareOptional() {
    return declare(VariableCardinality.OPTIONAL);
  }

  public VariableDeclaration declare(VariableCardinality cardinality) {
    return new VariableDeclaration(this, cardinality);
  }
}
