package uk.co.saiman.experiment.schedule.conflicts;

import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.data.resource.Resource;
import uk.co.saiman.experiment.path.Dependency;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.procedure.Conductor;
import uk.co.saiman.experiment.procedure.IndirectRequirement;
import uk.co.saiman.experiment.procedure.Instruction;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.procedure.RequirementResolutionContext;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.schedule.Products;
import uk.co.saiman.experiment.schedule.Schedule;
import uk.co.saiman.experiment.schedule.ScheduledInstruction;

public class Conflicts {
  private final Schedule schedule;
  private final Map<ExperimentPath<Absolute>, Change> differences;

  public Conflicts(Schedule schedule) {
    this.schedule = schedule;

    this.differences = new HashMap<>();
    schedule.getProcedure().instructions().forEach(this::checkDifference);
    schedule
        .currentProducts()
        .map(Products::getProcedure)
        .stream()
        .flatMap(Procedure::instructions)
        .filter(path -> schedule.getProcedure().instruction(path).isEmpty())
        .forEach(this::checkDifference);
  }

  public boolean isConflictFree() {
    return changes().allMatch(not(Change::isConflicting));
  }

  public Stream<Change> changes() {
    return differences.values().stream();
  }

  public Optional<Change> change(ExperimentPath<Absolute> path) {
    return Optional.ofNullable(differences.get(path));
  }

  private void checkDifference(ExperimentPath<Absolute> experimentPath) {
    differences.put(experimentPath, new ChangeImpl(experimentPath));
  }

  public class ChangeImpl implements Change {
    private final ExperimentPath<Absolute> path;

    private final Optional<Instruction> previousInstruction;
    private final Optional<ScheduledInstruction> scheduledInstruction;

    public ChangeImpl(ExperimentPath<Absolute> path) {
      this.path = path;

      this.previousInstruction = schedule
          .currentProducts()
          .map(Products::getProcedure)
          .flatMap(p -> p.instruction(path));
      this.scheduledInstruction = schedule.scheduledInstruction(path);
    }

    @Override
    public ExperimentPath<Absolute> path() {
      return path;
    }

    @Override
    public Optional<Instruction> currentInstruction() {
      return previousInstruction;
    }

    @Override
    public Optional<Instruction> scheduledInstruction() {
      return scheduledInstruction.map(ScheduledInstruction::instruction);
    }

    @Override
    public boolean isConflicting() {
      try {
        return conflictingInstruction().isPresent()
            || conflictingResources().findAny().isPresent()
            || conflictingDependencies().findAny().isPresent();
      } catch (IOException e) {
        return true;
      }
    }

    @Override
    public Stream<Resource> conflictingResources() throws IOException {
      return currentInstruction().isEmpty()
          ? schedule
              .getScheduler()
              .getStorageConfiguration()
              .locateStorage(path)
              .location()
              .resources()
          : Stream.empty();
    }

    @Override
    public Optional<Instruction> conflictingInstruction() {
      return previousInstruction
          .filter(
              state -> scheduledInstruction
                  .map(ScheduledInstruction::instruction)
                  .filter(state::equals)
                  .isEmpty());
    }

    @Override
    public Stream<Change> conflictingDependencies() {
      return scheduledInstruction
          .map(ScheduledInstruction::conductor)
          .stream()
          .flatMap(Conductor::indirectRequirements)
          .flatMap(this::resolveDependencies)
          .map(this::conflictingDependency)
          .flatMap(Optional::stream)
          .collect(toMap(Change::path, identity(), (a, b) -> a))
          .values()
          .stream();
    }

    private <T extends Product> Stream<Dependency<? extends T, Absolute>> resolveDependencies(
        IndirectRequirement<T> requirement) {
      return requirement
          .dependencies(new RequirementResolutionContext() {})
          .flatMap(dependency -> dependency.resolveAgainst(path).stream());
    }

    private Optional<Change> conflictingDependency(Dependency<?, Absolute> dependency) {
      return change(dependency.getExperimentPath())
          .filter(
              c -> c.isConflicting()
                  || dependency
                      .getProduction()
                      .isPresent(c.scheduledInstruction().get().conductor()));
    }
  }
}
