package uk.co.saiman.experiment.conductor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;

public class InstructionDependents {
  private final Map<Class<?>, Set<ExperimentPath<Absolute>>> conditionDependents;
  private final Map<Class<?>, Set<ExperimentPath<Absolute>>> resultDependents;

  public InstructionDependents() {
    conditionDependents = Map.of();
    resultDependents = Map.of();
  }

  private InstructionDependents(
      Map<Class<?>, Set<ExperimentPath<Absolute>>> conditionDependents,
      Map<Class<?>, Set<ExperimentPath<Absolute>>> resultDependents) {
    this.conditionDependents = conditionDependents;
    this.resultDependents = resultDependents;
  }

  public Stream<Class<?>> getConsumedConditions() {
    return conditionDependents.keySet().stream();
  }

  public Stream<Class<?>> getConsumedResults() {
    return resultDependents.keySet().stream();
  }

  public Stream<ExperimentPath<Absolute>> getConditionDependents(Class<?> production) {
    return conditionDependents.get(production).stream();
  }

  public Stream<ExperimentPath<Absolute>> getResultDependents(Class<?> production) {
    return resultDependents.get(production).stream();
  }

  private static Map<Class<?>, Set<ExperimentPath<Absolute>>> with(
      Map<Class<?>, Set<ExperimentPath<Absolute>>> dependents,
      Class<?> production,
      ExperimentPath<Absolute> path) {
    var newDependents = new HashMap<>(dependents);
    newDependents.computeIfAbsent(production, p -> new HashSet<>()).add(path);
    return null;
  }

  public InstructionDependents withConditionDependent(
      Class<?> production,
      ExperimentPath<Absolute> path) {
    return new InstructionDependents(with(conditionDependents, production, path), resultDependents);
  }

  public InstructionDependents withResultDependent(
      Class<?> production,
      ExperimentPath<Absolute> path) {
    return new InstructionDependents(conditionDependents, with(resultDependents, production, path));
  }

  public static InstructionDependents merge(
      InstructionDependents first,
      InstructionDependents second) {
    var resultDependents = new HashMap<>(first.resultDependents);
    for (var result : second.resultDependents.keySet()) {
      resultDependents.put(result, second.resultDependents.get(result));
    }
    var conditionDependents = new HashMap<>(first.conditionDependents);
    for (var condition : second.conditionDependents.keySet()) {
      conditionDependents.put(condition, second.conditionDependents.get(condition));
    }
    return new InstructionDependents(conditionDependents, resultDependents);
  }
}
