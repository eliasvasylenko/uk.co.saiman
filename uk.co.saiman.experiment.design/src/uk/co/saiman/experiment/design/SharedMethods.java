package uk.co.saiman.experiment.design;

import static uk.co.saiman.experiment.design.ExecutionPlan.EXECUTE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Relative;

public class SharedMethods {
  private static final SharedMethods EMPTY = new SharedMethods(Map.of());

  private final Map<ExperimentId, ExperimentStepDesign> methods;

  private SharedMethods(Map<ExperimentId, ExperimentStepDesign> methods) {
    this.methods = methods;
  }

  public static SharedMethods define() {
    return EMPTY;
  }

  public SharedMethods withMethodDesign(ExperimentStepDesign definition) {
    var methods = new HashMap<>(this.methods);
    methods.put(definition.id(), definition.withPlan(EXECUTE));

    return new SharedMethods(methods);
  }

  public SharedMethods withoutMethod(ExperimentId id) {
    var methods = new HashMap<>(this.methods);
    methods.remove(id);

    return new SharedMethods(methods);
  }

  public Stream<ExperimentId> methods() {
    return methods.keySet().stream();
  }

  public Optional<ExperimentStepDesign> methodDesign(ExperimentId id) {
    return Optional.ofNullable(methods.get(id));
  }

  ExperimentStepDesign materializeMethod(ExperimentStepDesign step) {
    return materializeMethod(step, Set.of());
  }

  ExperimentStepDesign materializeMethod(ExperimentStepDesign step, Set<ExperimentId> visitedMethods) {
    if (!step.isMethodInstance()) {
      return step;
    }

    var id = step.sharedMethodId().get();

    if (visitedMethods.contains(id)) {
      throw new CircularReferenceException(id);
    }
    visitedMethods = new HashSet<>(visitedMethods);
    visitedMethods.add(id);

    var method = methodDesign(id).orElseThrow(() -> new UnresolvedReferenceException(id));

    return substituteMethodStep(step.withoutSharedMethod(), method, ExperimentPath.toSelf(), visitedMethods);
  }

  private ExperimentStepDesign substituteMethodStep(
      ExperimentStepDesign step,
      ExperimentStepDesign methodStep,
      ExperimentPath<Relative> stepPath,
      Set<ExperimentId> visitedMethods) {
    var executor = step.executor();
    if (executor == null) {
      executor = methodStep.executor();
    }

    List<ExperimentStepDesign> resultSteps = new ArrayList<>();

    var substeps = step.substeps().iterator();
    var methodSubsteps = methodStep.substeps().iterator();

    while (methodSubsteps.hasNext()) {
      var methodSubstep = methodSubsteps.next();
      try {
        methodSubstep = materializeMethod(methodSubstep, visitedMethods);
      } catch (Exception e) {
        throw new MethodInstanceException(step.sharedMethodId().get(), stepPath.resolve(step.id()), e);
      }

      if (!step.findSubstep(methodSubstep.id()).isPresent()) {
        resultSteps.add(methodSubstep);
        continue;
      }

      while (substeps.hasNext()) {
        var substep = substeps.next();

        if (substep.id() == methodSubstep.id()) {
          substep = substituteMethodStep(substep, methodSubstep, stepPath.resolve(substep.id()), visitedMethods);

        } else if (methodStep.findSubstep(substep.id()).isPresent()) {
          throw new DependentReorderingException(stepPath, methodSubstep.id(), substep.id());
        }

        resultSteps.add(substep);
      }
    }
    while (substeps.hasNext()) {
      var substep = substeps.next();
      resultSteps.add(substep);
    }

    var result = ExperimentStepDesign
        .define(step.id())
        .withSubsteps(resultSteps)
        .withVariableMap(methodStep.variableMap().withAll(step.variableMap()))
        .withPlan(step.plan());
    result = step.executor().map(result::withExecutor).orElse(result);
    result = step.sharedMethodId().map(result::withSharedMethod).orElse(result);
    return result;
  }
}
