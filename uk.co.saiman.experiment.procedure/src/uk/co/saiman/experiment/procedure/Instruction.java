/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.experiment.procedure.
 *
 * uk.co.saiman.experiment.procedure is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.procedure is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.procedure;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static uk.co.saiman.collection.StreamUtilities.throwingMerger;
import static uk.co.saiman.experiment.variables.VariableCardinality.REQUIRED;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.dependency.ConditionPath;
import uk.co.saiman.experiment.dependency.ProductPath;
import uk.co.saiman.experiment.dependency.ResultPath;
import uk.co.saiman.experiment.executor.Evaluation;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.PlanningContext;
import uk.co.saiman.experiment.variables.VariableDeclaration;
import uk.co.saiman.experiment.variables.Variables;
import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;
import uk.co.saiman.property.IdentityProperty;
import uk.co.saiman.property.Property;
import uk.co.saiman.state.StateMap;

/**
 * An experiment instruction is the machine-consumable unit of information to
 * describe how an experiment step should be executed.
 * <p>
 * An experiment instruction corresponds to a node in an experiment graph, and
 * should typically be processed as part of a {@link Procedure procedure}.
 * <p>
 * For information about the experiment graph, @see
 * uk.co.saiman.experiment.procedure
 * 
 * @author Elias N Vasylenko
 */
public class Instruction {
  private final StateMap variableMap;
  private final Executor executor;
  private final WorkspaceExperimentPath path;

  private final Map<Class<?>, Evaluation> conditionPreparations;
  private final Set<Class<?>> resultObservations;

  private final ConditionPath<Absolute, ?> conditionRequirement;
  private final ResultPath<Absolute, ?> resultRequirement;
  private final Set<ResultPath<Absolute, ?>> additionalResultRequirements;
  private final Set<Class<?>> resourceRequirements;

  private final Set<VariableDeclaration<?>> variableDeclarations;

  private final boolean executesAutomatically;

  private final Procedure minimalProcedure;

  Instruction(Procedure precedingProcedure, ExperimentPath<Absolute> path, StateMap variableMap, Executor executor) {
    this.path = WorkspaceExperimentPath.define(precedingProcedure.id(), path);
    this.variableMap = variableMap;
    this.executor = executor;

    Map<Class<?>, Evaluation> conditionPreparations = new HashMap<>();
    Set<Class<?>> resultObservations = new HashSet<>();

    Property<Class<?>> conditionRequirement = new IdentityProperty<>();
    Property<Class<?>> resultRequirement = new IdentityProperty<>();
    Set<ResultPath<Absolute, ?>> additionalResultRequirements = new HashSet<>();
    Set<Class<?>> resourceRequirements = new HashSet<>();

    Set<VariableDeclaration<?>> variableDeclarations = new HashSet<>();

    Property<Boolean> executesAutomatically = new IdentityProperty<>(false);

    var variables = new Variables(precedingProcedure.environment(), variableMap);
    var safeContext = new PlanningContext() {
      private boolean done = false;

      private void assertLive() {
        if (done) {
          throw new IllegalStateException();
        }
      }

      @Override
      public void preparesCondition(Class<?> type, Evaluation evaluation) {
        assertLive();
        conditionPreparations.merge(type, evaluation, (a, b) -> {
          throw new InstructionException(
              path(),
              "Cannot prepare the condition '" + type + "' multiple times with different evaluation models");
        });
      }

      @Override
      public void observesResult(Class<?> production) {
        assertLive();
        resultObservations.add(production);
      }

      @Override
      public void executesAutomatically() {
        assertLive();
        executesAutomatically.set(true);
      }

      @Override
      public <T> Optional<T> declareVariable(VariableDeclaration<T> declaration) {
        assertLive();
        if (declaration.cardinality() == REQUIRED && variables.get(declaration.variable()).isEmpty()) {
          throw new InstructionException(path(), "Required variable '" + declaration.variable().id() + "' is missing");
        }
        return variables.get(declaration.variable());
      }

      @Override
      public void declareResourceRequirement(Class<?> type) {
        assertLive();
        if (!precedingProcedure.environment().providesResource(type)) {
          throw new InstructionException(path(), "Required resource '" + type + "' is missing from the environment");
        }
        resourceRequirements.add(type);
      }

      @Override
      public void declareConditionRequirement(Class<?> production) {
        assertLive();
        if (resultRequirement.isSet() || !additionalResultRequirements.isEmpty()) {
          throw new InstructionException(path(), "Cannot depend on results and conditions at the same time");
        }
        if (conditionRequirement.isSet()) {
          throw new InstructionException(path(), "Cannot declare multiple primary condition requirements");
        }
        conditionRequirement.set(production);
      }

      @Override
      public void declareResultRequirement(Class<?> production) {
        assertLive();
        if (resultRequirement.isSet()) {
          throw new InstructionException(
              path(),
              "Cannot declare multiple primary result requirements, use additional result requirements");
        }
        if (conditionRequirement.isSet()) {
          throw new InstructionException(path(), "Cannot depend on results and conditions at the same time");
        }
        resultRequirement.set(production);
      }

      @Override
      public void declareAdditionalResultRequirement(ResultPath<?, ?> path) {
        assertLive();
        if (conditionRequirement.isSet()) {
          throw new InstructionException(path(), "Cannot depend on results and conditions at the same time");
        }
        additionalResultRequirements.add(path.toAbsolute());
      }
    };
    executor.plan(safeContext);
    safeContext.done = true;

    this.conditionPreparations = Map.copyOf(conditionPreparations);
    this.resultObservations = Set.copyOf(resultObservations);

    this.conditionRequirement = ConditionPath
        .toCondition(path().getExperimentPath().parent().get(), conditionRequirement.get());
    this.resultRequirement = ResultPath.toResult(path().getExperimentPath().parent().get(), resultRequirement.get());
    this.additionalResultRequirements = Set.copyOf(additionalResultRequirements);
    this.resourceRequirements = Set.copyOf(resourceRequirements);

    this.variableDeclarations = Set.copyOf(variableDeclarations);

    this.executesAutomatically = executesAutomatically.tryGet().orElse(false);

    var deps = dependencies(precedingProcedure);
    var minimalInstructions = precedingProcedure
        .instructions()
        .filter(i -> deps.contains(i.path()))
        .collect(toMap(Instruction::path, identity(), throwingMerger(), LinkedHashMap::new));
    this.minimalProcedure = new Procedure(
        precedingProcedure.id(),
        precedingProcedure.environment(),
        minimalInstructions);
  }

  private Set<WorkspaceExperimentPath> dependencies(Procedure precedingProcedure) {
    Set<WorkspaceExperimentPath> deps = new HashSet<>();
    deps.add(path());
    Stream
        .of(conditionRequirement().stream(), resultRequirement().stream(), additionalResultRequirements())
        .flatMap(identity())
        .map(ProductPath::getExperimentPath)
        .map(p -> precedingProcedure.instruction(p).get().dependencies(precedingProcedure))
        .forEach(deps::addAll);
    return deps;
  }

  public boolean preparesCondition(Class<?> type) {
    return conditionPreparations.containsKey(type);
  }

  public Stream<Class<?>> conditionPreparations() {
    return conditionPreparations.keySet().stream();
  }

  public Evaluation conditionPreparationEvaluation(Class<?> conditionPreparation) {
    var c = conditionPreparations.get(conditionPreparation);
    if (c == null) {
      throw new InstructionException(path(), "Condition '" + conditionPreparation + "' is missing");
    }
    return c;
  }

  public boolean observesResult(Class<?> type) {
    return resultObservations.contains(type);
  }

  public Stream<Class<?>> resultObservations() {
    return resultObservations.stream();
  }

  public Optional<ConditionPath<Absolute, ?>> conditionRequirement() {
    return Optional.ofNullable(conditionRequirement);
  }

  public Optional<ResultPath<Absolute, ?>> resultRequirement() {
    return Optional.ofNullable(resultRequirement);
  }

  public Stream<ResultPath<Absolute, ?>> additionalResultRequirements() {
    return additionalResultRequirements.stream();
  }

  public Stream<Class<?>> resourceRequirements() {
    return resourceRequirements.stream();
  }

  public Stream<VariableDeclaration<?>> variableDeclarations() {
    return variableDeclarations.stream();
  }

  public boolean executesAutomatically() {
    return executesAutomatically;
  }

  public WorkspaceExperimentPath path() {
    return path;
  }

  public ExperimentId experimentId() {
    return path.getExperimentId();
  }

  public StateMap variableMap() {
    return variableMap;
  }

  public Executor executor() {
    return executor;
  }

  public ExperimentId id() {
    return path().ids().reduce((a, b) -> b).get();
  }

  public Procedure extractMinimalProcedure() {
    return minimalProcedure;
  }
}
