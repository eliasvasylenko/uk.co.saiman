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

import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;

public class ConductorInstruction {
  /*
   * Conductor
   */
  private final Conductor conductor;
  private final WorkspaceExperimentPath path;

  private ConductorOutput output;

  /*
   * Configuration
   */
  private Instruction instruction;
  private Environment environment;

  /*
   * Dependencies
   */
  private final OutgoingConditions outgoingConditions;
  private final OutgoingResults outgoingResults;
  private final IncomingDependencies incomingDependencies;

  private Execution execution;

  public ConductorInstruction(Conductor conductor, WorkspaceExperimentPath path) {
    this.conductor = conductor;
    this.path = path;

    /*
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * TODO these maybe need to be attached to a specific execution, not an
     * execution manager?
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     */
    this.outgoingConditions = new OutgoingConditions(conductor.lock(), path);
    this.outgoingResults = new OutgoingResults(conductor.lock(), path);
    this.incomingDependencies = new IncomingDependencies(conductor, path);
  }

  public WorkspaceExperimentPath getPath() {
    return path;
  }

  public Instruction getInstruction() {
    return instruction;
  }

  public Conductor getConductor() {
    return conductor;
  }

  void updateInstruction(Procedure procedure, Environment environment) {
    var instruction = procedure.instruction(path).get();
    requireNonNull(environment);

    if (!isCompatibleConfiguration(instruction, this.instruction) && execution != null) {
      execution.invalidate();
    }

    this.instruction = instruction;
    this.environment = environment;
  }

  void updateDependencies(ConductorOutput output) {
    this.output = output;
    outgoingConditions.update(instruction, environment);
    incomingDependencies.update(output, instruction, environment);
  }

  protected <T> IncomingCondition<T> addConditionConsumer(
      Class<T> condition,
      WorkspaceExperimentPath path) {
    return outgoingConditions
        .getOutgoingCondition(condition)
        .orElseThrow(
            () -> new ConductorException(
                "Cannot add dependency on missing condition " + condition + " to " + this.path))
        .addConsumer(path);
  }

  protected <T> IncomingResult<T> addResultConsumer(Class<T> result, WorkspaceExperimentPath path) {
    return outgoingResults
        .getOutgoingResult(result)
        .orElseThrow(
            () -> new ConductorException(
                "Cannot add dependency on missing result " + result + " to " + this.path))
        .addConsumer(path);
  }

  private boolean isCompatibleConfiguration(
      Instruction instruction,
      Instruction previousInstruction) {
    return instruction != null && previousInstruction != null
        && previousInstruction.id().equals(instruction.id())
        && previousInstruction.executor().equals(instruction.executor())
        && previousInstruction.variableMap().equals(instruction.variableMap());
  }

  void execute() {
    if (execution != null && execution.isValid()) {
      return;
    }

    execution = new Execution(
        output,
        path,
        instruction,
        environment,
        outgoingConditions,
        outgoingResults,
        incomingDependencies);
    execution.start();
  }

  void remove() {
    instruction = null;
    environment = null;
    if (execution != null) {
      execution.invalidate();
      execution = null;
    }
  }

  void join() {
    execution.join();
  }
}
