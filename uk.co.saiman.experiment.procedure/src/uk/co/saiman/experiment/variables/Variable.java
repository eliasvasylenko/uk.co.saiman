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
/*
 * Items for consideration:
 * 
 * 1) Aliasing between variables.
 * 
 * 1.5) Migration concerns. How can we evolve a conductor implementation to
 * remain backwards compatible while also adding new functionality?
 * 
 * 2) Different requirements/formats for storage, each variable accessor stores
 * to its own ID so we always know we're loading according to the correct
 * format.
 * 
 * 3) Adding variables from outside sources.
 * 
 * 4) Required and Optional variables. This is stored on a per-procedure basis.
 * 
 * 5) Read only / Writable variables! This is a side-issue of 2)
 * 
 * 
 * 
 * TODO variable service is owned by the conductor
 * 
 * pro: nice neat API, users don't need to interact with service
 * 
 * con: if conductor implementor doesn't do it properly we're screwed! maybe
 * conductor can be a class and we can pass the service in...
 * 
 * conceptually the responsibility is being delegated to the wrong actors, it
 * should probably be centralized somewhere else.
 * 
 * TODO variable service is external
 * 
 * pro: no reliance on conductor, maybe we can restrict instances of indexing
 * class so they're only created by via a management service
 * 
 * con: more hoops to jump through, less direct api. We can improve this is some
 * contexts, e.g. automatically doing it in ConductorContext or when exposing
 * via e4 injection.
 * 
 * 
 * 
 * TODO there may be a parallel concept of products that are put onto
 * experiments from outside sources. Not sure if this is necessary or even
 * plausible!! How would it work? Do other components get to participate in the
 * lifecycle of a conducting experiment?
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
