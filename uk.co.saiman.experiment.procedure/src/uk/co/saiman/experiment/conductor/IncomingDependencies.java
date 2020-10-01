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
 * This file is part of uk.co.saiman.experiment.conductor.
 *
 * uk.co.saiman.experiment.conductor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.conductor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.conductor;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.dependency.Condition;
import uk.co.saiman.experiment.dependency.Result;
import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.procedure.Instruction;
import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;

public class IncomingDependencies {
  enum IncomingDependencyState {
    WAITING, ACQUIRED, DONE
  }

  private final Conductor conductor;
  private final WorkspaceExperimentPath path;

  private IncomingCondition<?> incomingCondition;
  private IncomingResult<?> incomingResult;
  private List<IncomingResult<?>> additionalIncomingResults;

  public IncomingDependencies(Conductor conductor, WorkspaceExperimentPath path) {
    this.conductor = conductor;
    this.path = path;
  }

  void update(ConductorOutput output, Instruction instruction, Environment environment) {
    requireNonNull(instruction);
    requireNonNull(environment);

    incomingCondition = instruction
        .conditionRequirement()
        .flatMap(
            production -> output
                .findInstruction(
                    WorkspaceExperimentPath.define(instruction.experimentId(), production.getExperimentPath()))
                .map(dependency -> dependency.addConditionConsumer(production.getProduction(), path)))
        .orElse(null);
    incomingResult = instruction
        .resultRequirement()
        .flatMap(
            production -> output
                .findInstruction(
                    WorkspaceExperimentPath.define(instruction.experimentId(), production.getExperimentPath()))
                .map(dependency -> dependency.addResultConsumer(production.getProduction(), path)))
        .orElse(null);
    additionalIncomingResults = instruction
        .additionalResultRequirements()
        .flatMap(
            production -> output
                .findInstruction(
                    WorkspaceExperimentPath.define(instruction.experimentId(), production.getExperimentPath()))
                .map(dependency -> dependency.addResultConsumer(production.getProduction(), path))
                .stream())
        .collect(toList());
  }

  protected Optional<ConductorInstruction> getParent(ConductorOutput output) {
    return path
        .getExperimentPath()
        .parent()
        .map(p -> WorkspaceExperimentPath.define(path.getExperimentId(), p))
        .flatMap(output::findInstruction);
  }

  @SuppressWarnings("unchecked")
  public <T> Condition<T> acquireCondition(Class<T> source) {
    if (incomingCondition == null || incomingCondition.type() != source) {
      throw new ConductorException("No condition dependency declared on " + source);
    }
    return ((IncomingCondition<T>) incomingCondition).acquire();
  }

  @SuppressWarnings("unchecked")
  public <T> Result<T> acquireResult(Class<T> source) {
    if (incomingResult == null || incomingResult.type() != source) {
      throw new ConductorException("No result dependency declared on " + source);
    }
    return ((IncomingResult<T>) incomingResult).acquire();
  }

  @SuppressWarnings("unchecked")
  public <T> Stream<Result<T>> acquireAdditionalResults(Class<T> source) {
    return Optional
        .ofNullable(additionalIncomingResults)
        .stream()
        .flatMap(List::stream)
        .filter(r -> r.type() == source)
        .map(r -> (IncomingResult<T>) r)
        .map(IncomingResult::acquire)
        .collect(toList())
        .stream();
  }

  public void terminate() {
    if (incomingCondition != null) {
      incomingCondition.done();
    }
    if (incomingResult != null) {
      incomingResult.done();
    }
    if (additionalIncomingResults != null) {
      for (var resultDependency : additionalIncomingResults) {
        resultDependency.done();
      }
    }
  }

  public void invalidate() {
    if (incomingCondition != null) {
      incomingCondition.invalidateIncoming();
    }
    if (incomingResult != null) {
      incomingResult.invalidateIncoming();
    }
    if (additionalIncomingResults != null) {
      for (var resultDependency : additionalIncomingResults) {
        resultDependency.invalidateIncoming();
      }
    }
  }
}
