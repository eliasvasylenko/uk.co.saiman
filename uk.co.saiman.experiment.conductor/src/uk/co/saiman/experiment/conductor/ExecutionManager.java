/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
package uk.co.saiman.experiment.conductor;

import static java.util.Objects.requireNonNull;
import static uk.co.saiman.experiment.conductor.ExecutionManager.UpdateStatus.INVALID;
import static uk.co.saiman.experiment.conductor.ExecutionManager.UpdateStatus.REMOVED;
import static uk.co.saiman.experiment.conductor.ExecutionManager.UpdateStatus.VALID;

import uk.co.saiman.experiment.environment.LocalEnvironment;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;
import uk.co.saiman.log.Log.Level;

public class ExecutionManager {
  /**
   * Instructions can be updated mid-execution. This is made thread safe by
   * locking on any interaction with their context during the update. This
   * represents the status of an update in progress.
   * 
   * @author Elias N Vasylenko
   */
  enum UpdateStatus {
    /**
     * The instruction has been updated and the execution state appears to remain
     * valid. External factors may still mark this as invalid. If the status is
     * valid when execution resumes, it can resume safely.
     */
    VALID,
    /**
     * The instruction has been updated and the execution has been flagged as
     * invalidated. If the status is invalid when execution resumes, it must
     * terminate and restart.
     */
    INVALID,
    /**
     * The instruction has been removed from the procedure and should be terminated.
     */
    REMOVED
  }

  /*
   * Conductor
   */
  private final Conductor conductor;
  private final WorkspaceExperimentPath path;

  /*
   * Configuration
   */
  private Instruction instruction;
  private LocalEnvironment environment;

  /*
   * Dependencies
   */
  private UpdateStatus updateStatus;
  private final OutgoingConditions outgoingConditions;
  private final OutgoingResults outgoingResults;
  private final IncomingDependencies incomingDependencies;

  private Thread executionThread;
  private Execution execution;

  public ExecutionManager(Conductor conductor, WorkspaceExperimentPath path) {
    this.conductor = conductor;
    this.path = path;

    this.outgoingConditions = new OutgoingConditions(conductor.lock(), path);
    this.outgoingResults = new OutgoingResults(conductor.lock(), path);
    this.incomingDependencies = new IncomingDependencies(conductor, path);

    this.updateStatus = VALID;
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

  void updateInstruction(Instruction instruction, LocalEnvironment environment) {
    requireNonNull(instruction);
    requireNonNull(environment);

    if (!this.path.getExperimentPath().equals(instruction.path())) {
      throw new IllegalStateException("This shouldn't happen!");
    }

    if (!isCompatibleConfiguration(instruction, this.instruction)) {
      markInvalidated();
    }

    this.instruction = instruction;
    this.environment = environment;
  }

  void updateDependencies() {
    if (updateStatus != REMOVED) {
      outgoingConditions.update(instruction, environment);
      incomingDependencies.update(instruction, environment);
    }
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

  boolean execute() {
    if (updateStatus == VALID) {
      return true;
    }

    try {
      if (execution != null) {
        execution.stop();
      }
    } catch (Exception e) {
      conductor.log().log(Level.ERROR, "Failed to stop experiment.", e);

      throw e;
    } finally {
      execution = null;
    }

    if (updateStatus == REMOVED) {
      return false;
    }

    updateStatus = VALID;

    try {
      execution = new Execution(
          conductor,
          path,
          instruction,
          environment,
          outgoingConditions,
          outgoingResults,
          incomingDependencies);

      execution.start();
    } catch (Exception e) {
      conductor.log().log(Level.ERROR, "Failed to start experiment.", e);
      throw e;
    }

    return true;
  }

  boolean isRunning() {
    return executionThread != null;
  }

  void markRemoved() {
    markInvalidated();
    updateStatus = REMOVED;
    instruction = null;
    environment = null;
  }

  void markInvalidated() {
    if (updateStatus != INVALID && updateStatus != REMOVED) {
      updateStatus = INVALID;
      incomingDependencies.invalidate();
      outgoingConditions.invalidate();
      outgoingResults.invalidate();
    }
  }
}
