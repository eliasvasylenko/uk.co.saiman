package uk.co.saiman.experiment.procedure;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.function.Function;

import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Relative;
import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.state.StateMap;

public class Template<T extends Product> extends Instructions<Template<T>, Relative> {
  private final String id;
  private final StateMap state;
  private final Conductor<T> conductor;

  private Template(String id, StateMap state, Conductor<T> conductor) {
    this.id = id;
    this.state = state;
    this.conductor = conductor;
  }

  private Template(
      String id,
      StateMap state,
      Conductor<T> conductor,
      NavigableMap<ExperimentPath<Relative>, ExperimentLocation> instructions,
      Map<ProductPath<Relative>, ProductLocation> dependencies) {
    super(instructions, dependencies);
    this.id = id;
    this.state = state;
    this.conductor = conductor;
  }

  public static <S, T extends Product> Template<T> define(
      String id,
      StateMap state,
      Conductor<T> conductor) {
    return new Template<>(Procedure.validateName(id), state, conductor);
  }

  public String id() {
    return id;
  }

  public Template<T> withId(String id) {
    return new Template<>(id, state, conductor, getInstructions(), getDependencies());
  }

  public <U> Optional<U> variable(Variable<U> variable) {
    return state().getOptional(variable.index());
  }

  public <U> Template<T> withVariable(Variable<U> variable, U value) {
    return withState(state().with(variable.index(), value));
  }

  public <U> Template<T> withVariable(Variable<U> variable, Function<Optional<U>, U> value) {
    return withVariable(variable, value.apply(variable(variable)));
  }

  public Conductor<T> conductor() {
    return conductor;
  }

  public StateMap state() {
    return state;
  }

  public Template<T> withState(StateMap state) {
    return new Template<>(id, state, conductor, getInstructions(), getDependencies());
  }

  public Instruction instruction() {
    return Instruction.define(id, state, conductor);
  }

  @Override
  protected Template<T> withInstructions(
      NavigableMap<ExperimentPath<Relative>, ExperimentLocation> instructions,
      Map<ProductPath<Relative>, ProductLocation> dependencies) {
    return new Template<>(id, state, conductor, instructions, dependencies);
  }

  @Override
  ExperimentPath<Relative> getExperimentPath() {
    return ExperimentPath.defineRelative();
  }
}
