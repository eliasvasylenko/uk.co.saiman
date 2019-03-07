package uk.co.saiman.experiment.procedure;

import java.util.Map;
import java.util.NavigableMap;

import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.state.StateMap;

public class Template<S, T extends Product> extends Instructions<Template<S, T>> {
  private final Instruction instruction;

  private Template(Instruction instruction) {
    this.instruction = instruction;
  }

  private Template(
      Instruction instruction,
      NavigableMap<ExperimentPath, ExperimentLocation> instructions,
      Map<ProductPath, ProductLocation> dependencies) {
    super(instructions, dependencies);
    this.instruction = instruction;
  }

  public static <S, T extends Product> Template<S, T> define(
      String id,
      StateMap state,
      Conductor<S, T> conductor) {
    return new Template<>(Instruction.define(Procedure.validateName(id), state, conductor));
  }

  public Instruction instruction() {
    return instruction;
  }

  public String id() {
    return instruction.id();
  }

  public StateMap state() {
    return instruction.state();
  }

  @SuppressWarnings("unchecked")
  public Conductor<S, T> conductor() {
    return (Conductor<S, T>) instruction.conductor();
  }

  public Template<S, T> withId(String id) {
    return new Template<>(instruction.withId(id), getInstructions(), getDependencies());
  }

  public Template<S, T> withState(StateMap state) {
    return new Template<>(instruction.withState(state), getInstructions(), getDependencies());
  }

  @Override
  protected Template<S, T> withInstructions(
      NavigableMap<ExperimentPath, ExperimentLocation> instructions,
      Map<ProductPath, ProductLocation> dependencies) {
    return new Template<>(instruction, instructions, dependencies);
  }
}
