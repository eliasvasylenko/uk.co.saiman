package uk.co.saiman.experiment.schedule;

import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.experiment.procedure.Conductor;
import uk.co.saiman.experiment.procedure.Instruction;
import uk.co.saiman.experiment.procedure.Variable;
import uk.co.saiman.state.StateMap;

public class ScheduledInstruction {
  private final Schedule schedule;
  private final Instruction instruction;
  private final Optional<ProductPath<Absolute>> path;

  ScheduledInstruction(
      Schedule schedule,
      Instruction instruction,
      Optional<ProductPath<Absolute>> path) {
    this.schedule = schedule;
    this.instruction = instruction;
    this.path = path;
  }

  public Instruction instruction() {
    return instruction;
  }

  public Optional<ProductPath<Absolute>> productPath() {
    return path;
  }

  public ExperimentPath<Absolute> experimentPath() {
    return path
        .map(ProductPath::getExperimentPath)
        .orElse(ExperimentPath.defineAbsolute())
        .resolve(instruction.id());
  }

  public String id() {
    return instruction.id();
  }

  public Conductor<?> conductor() {
    return instruction.conductor();
  }

  public StateMap state() {
    return instruction.state();
  }

  public <T> Optional<T> variable(Variable<T> variable) {
    return state().getOptional(variable.index());
  }

  public Optional<ScheduledInstruction> parent() {
    return schedule.getParent(this);
  }

  public Stream<ScheduledInstruction> children() {
    return schedule.getChildren(this);
  }
}
