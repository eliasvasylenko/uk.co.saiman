package uk.co.saiman.experiment.procedure;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.experiment.variables.Variables;

public class Instruction {
  private final String id;
  private final Variables variables;
  private final Conductor<?> conductor;

  private Instruction(String id, Variables variables, Conductor<?> conductor) {
    this.id = id;
    this.variables = variables;
    this.conductor = conductor;
  }

  /*
   * 
   * 
   * 
   * 
   * TODO most ordering problems are solved by disallowing a condition dependency
   * to have indirect result dependencies. Items with only result dependencies can
   * be processed at any time, they don't have awkward timing/ordering issues.
   * 
   * 
   * 
   * TODO maybe simplify this so we only have an experiment path not a product
   * path, then the conductor/requirement mediates what product we attach to
   * (optionally based on our state). That way:
   * 
   * - If we have conductors/requirements that can only wire to one specific
   * product id, we don't need to redundantly specify that id anywhere.
   * 
   * - If we can connect to different products we also have the option of
   * specifying one.
   * 
   * 
   * What about the age old problem of ordering? Of responding to moving/renaming
   * of dependencies that we're wired to?
   * 
   * 
   * Maybe a product can provide multiple product paths when multiple requirements
   * try to wire against it? e.g. one for each index needed for ordering? Or maybe
   * it just tracks which requirements attempt to wire against it and maintains
   * ordering internally? Where and when is this ordering stored?
   * 
   * - How do we surface the ordering through e.g. drag and drop?
   * 
   * - How do we retain the ordering through template expansion?
   * 
   * 
   * 
   * 
   * 
   * 
   * Have instructions declare their dependency:
   * 
   * - Pleasing parallel with indirect dependencies.
   * 
   * - All information is self contained, only references parent
   * 
   * - Unusual place to specify ordering:
   * 
   * -- Specify an index: what if we overlap with another instruction at same
   * index?
   * 
   * -- Specify linked list: what if we're batch-copied out of order and link to
   * instructions which aren't there?
   * 
   * Have instructions declare their dependents
   * 
   * - No worry of instructions declaring conflicting dependencies e.g. at the
   * same position
   * 
   * - Central model of ordered list dependencies
   * 
   * - What do we do when adding instructions? Who is responsible for inserting
   * into metadata?
   * 
   * - What do we do when removing instructions?
   * 
   * 
   * 
   * What cases do we actually care about ordering? Generating reports? This is a
   * bit of an edge case. Does it matter if it's a little clunky? Maybe ordering
   * *should* be maintained by the container.
   * 
   * 
   * 
   * 
   * Maybe a conductor adds a number of products to an instruction based on
   * configuration, e.g. pulls a SIZE variable to determine how many numbered
   * products to add, of takes a CHAPTER_NAMES variable to get a list of strings
   * to determine the names of the products to add.
   * 
   * 
   * 
   * 
   * 
   * 
   * TODO problem with having paths which specify their index is insertion and
   * removal, all instructions at paths with higher indices must be moved. How do
   * we keep track of movement? Do we need to? Experiment path is still the same
   * so we have a persistent id.
   * 
   * 
   * 
   * 
   * 
   */

  public static Instruction define(String id, Variables variables, Conductor<?> conductor) {
    return new Instruction(
        Procedure.validateName(id),
        requireNonNull(variables),
        requireNonNull(conductor));
  }

  public String id() {
    return id;
  }

  public Instruction withId(String id) {
    return new Instruction(Procedure.validateName(id), variables, conductor);
  }

  public Variables variables() {
    return variables;
  }

  public Instruction withVariables(Variables variables) {
    return new Instruction(id, variables, conductor);
  }

  public <T> Optional<T> variable(Variable<T> variable) {
    return variables.variable(variable);
  }

  public <U> Instruction withVariable(Variable<U> variable, U value) {
    return withVariables(variables.withVariable(variable, value));
  }

  public <U> Instruction withVariable(Variable<U> variable, Function<Optional<U>, U> value) {
    return withVariables(variables.withVariable(variable, value));
  }

  public Conductor<?> conductor() {
    return conductor;
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
        && Objects.equals(this.variables, that.variables)
        && Objects.equals(this.conductor, that.conductor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, variables, conductor);
  }
}
