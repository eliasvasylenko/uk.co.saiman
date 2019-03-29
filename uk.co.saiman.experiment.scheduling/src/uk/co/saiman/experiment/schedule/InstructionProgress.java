package uk.co.saiman.experiment.schedule;

import static java.lang.String.format;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.data.Data;
import uk.co.saiman.data.resource.Location;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.procedure.ConditionRequirement;
import uk.co.saiman.experiment.procedure.ConductionContext;
import uk.co.saiman.experiment.procedure.Conductor;
import uk.co.saiman.experiment.procedure.Instruction;
import uk.co.saiman.experiment.procedure.ResultRequirement;
import uk.co.saiman.experiment.product.Observation;
import uk.co.saiman.experiment.product.Preparation;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Result;
import uk.co.saiman.experiment.variables.Variable;

public class InstructionProgress {
  private final Scheduler scheduler;
  private final ExperimentPath<Absolute> path;
  private final Instruction instruction;

  public InstructionProgress(
      Scheduler scheduler,
      ExperimentPath<Absolute> path,
      Instruction instruction) {
    this.scheduler = scheduler;
    this.path = path;
    this.instruction = instruction;
  }

  void updateInstruction(Instruction instruction) {

  }

  void interrupt() {

  }

  public <T extends Product> void conduct(Conductor<T> conductor) {
    conductor.conduct(new ConductionContext<T>() {
      @Override
      public Instruction instruction() {
        return instruction;
      }

      @Override
      public T dependency() {
        return conductor.directRequirement() instanceof ConditionRequirement<?> ? null : null;
      }

      @Override
      public <U> U acquireCondition(ConditionRequirement<U> resource) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public <U> Result<? extends U> acquireResult(ResultRequirement<U> requirement) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public <U> Stream<Result<? extends U>> acquireResults(ResultRequirement<U> requirement) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public Location getLocation() {
        try {
          return scheduler.getStorageConfiguration().locateStorage(path).location();
        } catch (IOException e) {
          throw new SchedulingException(format("Failed to allocate storage for %s", path));
        }
      }

      @Override
      public <U> void prepareCondition(Preparation<U> condition, U resource) {
        // TODO Auto-generated method stub

      }

      @Override
      public <R> void setPartialResult(Observation<R> observation, Supplier<? extends R> value) {
        // TODO Auto-generated method stub

      }

      @Override
      public <R> void setResultData(Observation<R> observation, Data<R> data) {
        // TODO Auto-generated method stub

      }

      @Override
      public void completeObservation(Observation<?> observation) {
        // TODO Auto-generated method stub

      }

      @Override
      public <U> Optional<U> getOptionalVariable(Variable<U> variable) {
        // TODO Auto-generated method stub
        return null;
      }
    });
  }
}
