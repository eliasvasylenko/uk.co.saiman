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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import uk.co.saiman.data.Data;
import uk.co.saiman.experiment.environment.service.LocalEnvironmentService;
import uk.co.saiman.experiment.executor.service.ExecutorService;
import uk.co.saiman.experiment.output.Output;
import uk.co.saiman.experiment.output.event.OutputBeginEvent;
import uk.co.saiman.experiment.output.event.OutputSucceededEvent;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.procedure.Procedures;
import uk.co.saiman.experiment.procedure.json.JsonInstructionFormat;
import uk.co.saiman.experiment.procedure.json.JsonProcedureFormat;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;
import uk.co.saiman.log.Log;

public class Conductor {
  private final ReentrantLock lock;

  private final StorageConfiguration<?> storageConfiguration;
  private final JsonInstructionFormat instructionFormat;
  private final LocalEnvironmentService environmentService;
  private final Log log;
  private final Executor executor;

  private final Map<WorkspaceExperimentPath, ExecutionManager> progress;
  private Procedure procedure;
  private Data<Procedure> data;

  private ConductorOutput output = new ConductorOutput();

  public Conductor(
      StorageConfiguration<?> storageConfiguration,
      ExecutorService executorService,
      LocalEnvironmentService environmentService,
      Log log) {
    this.lock = new ReentrantLock();

    this.storageConfiguration = requireNonNull(storageConfiguration);
    this.instructionFormat = new JsonInstructionFormat(executorService);
    this.environmentService = environmentService;
    this.log = log;

    this.executor = Executors.defaultThreadFactory()::newThread;

    this.progress = new HashMap<>();
  }

  void execute(Runnable runnable) {
    executor.execute(runnable);
  }

  Log log() {
    return log;
  }

  JsonInstructionFormat instructionFormat() {
    return instructionFormat;
  }

  StorageConfiguration<?> storageConfiguration() {
    return storageConfiguration;
  }

  public Output getOutput() {
    return output;
  }

  private Output nextOutput() {
    var supersedingOutput = new ConductorOutput();
    this.output.nextEvent(new OutputSucceededEvent(this.output, supersedingOutput));
    this.output = supersedingOutput;
    this.output.nextEvent(new OutputBeginEvent(output));
    return this.output;
  }

  public Output conduct(Procedure procedure) {
    try {
      lock.lock();

      var environment = Procedures.openEnvironment(procedure, environmentService, 2, SECONDS);

      Procedures.validateDependencies(procedure);

      var storage = storageConfiguration.locateStorage(procedure.path());

      var procedureFormat = new JsonProcedureFormat(instructionFormat, procedure.environment());

      if (data != null) {
        data.unset();
        data.save();
      }
      data = Data.locate(storage.location(), procedure.id().name(), procedureFormat);
      data.set(procedure);
      data.save();

      this.progress
          .values()
          .stream()
          .filter(execution -> procedure.instruction(execution.getPath()).isEmpty())
          .forEach(ExecutionManager::markRemoved);

      procedure
          .instructionPaths()
          .forEach(
              path -> this.progress
                  .compute(
                      path,
                      (p, execution) -> Optional
                          .ofNullable(execution)
                          .orElseGet(() -> new ExecutionManager(this, p)))
                  .updateInstruction(procedure.instruction(path).get(), environment));

      procedure.instructionPaths().forEach(path -> this.progress.get(path).updateDependencies());

      this.progress.replaceAll((path, execution) -> execution.execute() ? execution : null);

      this.procedure = procedure;

      return nextOutput();

      /*
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * TODO we must close the environment when the experiment is complete!!!!
       * 
       * 
       * TODO we also need to emit a completed event at that point
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
       * 
       * 
       */

    } catch (Exception e) {
      throw new ConductorException(format("Unable to conduct procedure %s", procedure), e);

    } finally {
      lock.unlock();
    }
  }

  void completeExecution(Execution execution) {
    // TODO Auto-generated method stub

  }

  Optional<ExecutionManager> findInstruction(WorkspaceExperimentPath path) {
    return Optional.ofNullable(progress.get(path));
  }

  public Optional<Procedure> procedure() {
    return Optional.ofNullable(procedure);
  }

  public Output clear() {
    try {
      lock.lock();

      Procedures.validateDependencies(procedure);

      if (data != null) {
        data.unset();
        data.save();
      }

      this.progress.values().stream().forEach(execution -> {
        execution.markRemoved();
        execution.execute();
      });

      this.progress.clear();
      this.procedure = null;

      return nextOutput();

    } finally {
      lock.unlock();
    }
  }

  Lock lock() {
    return lock;
  }
}
