package uk.co.saiman.experiment.schedule.conflicts;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.data.resource.Resource;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.procedure.Instruction;

public interface Change {
  ExperimentPath path();

  Optional<Instruction> currentInstruction();

  Optional<Instruction> scheduledInstruction();

  boolean isConflicting();

  Optional<Instruction> conflictingInstruction();

  Stream<Resource> conflictingResources() throws IOException;

  Stream<Change> conflictingDependencies();
}
