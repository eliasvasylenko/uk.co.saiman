package uk.co.saiman.experiment.schedule;

import static java.lang.String.format;

import java.util.List;
import java.util.Map;

import uk.co.saiman.experiment.path.Dependency;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.experiment.procedure.Conductor;
import uk.co.saiman.experiment.procedure.DependencyHandle;
import uk.co.saiman.experiment.procedure.Instruction;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.procedure.ConfigurationContext;
import uk.co.saiman.experiment.procedure.Requirement;
import uk.co.saiman.experiment.procedure.ResultRequirement;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Result;
import uk.co.saiman.state.StateMap;

public class ScheduledInstruction<S, T extends Product> {
  private ScheduledInstruction(
      Procedure procedure,
      ExperimentPath path,
      Instruction instruction,
      Conductor<S, T> conductor) {
    var context = new ConfigurationContext() {
      @Override
      public void update(StateMap state) {
        assertImmutable();
      }

      @Override
      public <U> DependencyHandle<Result<U>> setRequiredResult(
          Requirement<Result<U>> requirement,
          Dependency<? extends Result<? extends U>> dependency) {
        /*
         * 
         * 
         * 
         * TODO this needs to be allowed while Conductor#configureVariables is running,
         * but then disallowed after
         * 
         * 
         * TODO think more carefully about how the state map and the indirect
         * requirements are linked. We load from the former to instantiate the latter,
         * than modifications to the latter need to be saved to the former.
         * 
         * TODO maybe think of it as a one-way relationship? That way we take care of
         * the immutability issue too. We build our requirement mappings from the state
         * map. If we want to modify our requirements, we modify the state map, then our
         * mappings are automatically re-loaded from the new state map.
         * 
         * TODO e.g. have a method:
         * 
         * IndirectRequirements configureRequirements(Context)
         * 
         * where IndirectRequirements is immutable, and we can register update listeners
         * in the context.
         * 
         * 
         * 
         */
        return assertImmutable();
      }

      @Override
      public void setId(String id) {
        assertImmutable();
      }

      @Override
      public boolean removeRequiredResult(
          ResultRequirement<?> requirement,
          ProductPath dependency) {
        // TODO Auto-generated method stub
        return false;
      }

      @Override
      public StateMap getState() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public String getId() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public boolean clearRequiredResults(ResultRequirement<?> requirement) {
        return assertImmutable();
      }

      @Override
      public <T> DependencyHandle<Result<T>> addRequiredResult(
          Requirement<Result<T>> requirement,
          Dependency<? extends Result<? extends T>> dependency) {
        return assertImmutable();
      }
    };

    // TODO Auto-generated constructor stub
  }

  public static ScheduledInstruction<?, ?> schedule(Procedure procedure, ExperimentPath path) {
    var instruction = procedure
        .instruction(path)
        .orElseThrow(
            () -> new SchedulingException(
                format(
                    "Failed find an instruction to schedule at path %s in procedure %s",
                    path,
                    procedure)));
    return new ScheduledInstruction<>(procedure, path, instruction, instruction.conductor());
  }

  Object variables() {
    // TODO Auto-generated method stub
    return null;
  }

  Map<Requirement<?>, List<Dependency<?>>> dependencies() {
    // TODO Auto-generated method stub
    return null;
  }

  private <U> U assertImmutable() {
    throw new Exception();
  }
}