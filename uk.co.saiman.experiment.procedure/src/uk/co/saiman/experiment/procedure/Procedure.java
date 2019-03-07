package uk.co.saiman.experiment.procedure;

import static java.lang.String.format;

import java.util.Map;
import java.util.NavigableMap;

import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.experiment.product.Nothing;

public class Procedure extends Instructions<Procedure> {
  private final String id;

  private Procedure(String id) {
    this.id = validateName(id);
  }

  private Procedure(
      String id,
      NavigableMap<ExperimentPath, ExperimentLocation> instructions,
      Map<ProductPath, ProductLocation> dependencies) {
    super(instructions, dependencies);
    this.id = validateName(id);
  }

  public static Procedure define(String id) {
    return new Procedure(id);
  }

  public String id() {
    return id;
  }

  public Procedure withId(String id) {
    return new Procedure(id, getInstructions(), getDependencies());
  }

  static String validateName(String name) {
    if (!isNameValid(name)) {
      throw new ProcedureException(format("Invalid name for experiment %s", name));
    }
    return name;
  }

  public static boolean isNameValid(String name) {
    final String ALPHANUMERIC = "[a-zA-Z0-9]+";
    final String DIVIDER_CHARACTERS = "[ \\.\\-_]+";

    return name != null
        && name.matches(ALPHANUMERIC + "(" + DIVIDER_CHARACTERS + ALPHANUMERIC + ")*");
  }

  @Override
  protected Procedure withInstructions(
      NavigableMap<ExperimentPath, ExperimentLocation> instructions,
      Map<ProductPath, ProductLocation> dependencies) {
    return new Procedure(id, instructions, dependencies);
  }

  public Procedure withInstruction(Instruction instruction) {
    return withInstruction(-1, instruction);
  }

  @Override
  Procedure withInstruction(long index, Instruction instruction) {
    return super.withInstruction(index, instruction);
  }

  public Procedure withTemplate(Template<?, Nothing> template) {
    return withTemplate(-1, template);
  }

  public Procedure withTemplate(long index, Template<?, Nothing> template) {
    return super.withTemplate(index, template);
  }
}
