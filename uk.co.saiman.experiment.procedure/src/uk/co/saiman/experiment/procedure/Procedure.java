package uk.co.saiman.experiment.procedure;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;

public class Procedure {
  private final String id;
  private final LinkedHashMap<ExperimentPath<Absolute>, Instruction<?>> instructions;

  public Procedure(String id, Collection<? extends Instruction<?>> instructions) {
    this.id = id;
    this.instructions = new LinkedHashMap<>();
    for (var instruction : instructions) {
      this.instructions.put(instruction.path(), instruction);
    }
  }

  public String id() {
    return id;
  }

  public Stream<Instruction<?>> instructions() {
    return instructions.values().stream();
  }

  public Stream<ExperimentPath<Absolute>> paths() {
    return instructions.keySet().stream();
  }

  public Optional<Instruction<?>> instruction(ExperimentPath<?> path) {
    return Optional.ofNullable(instructions.get(path));
  }
}
