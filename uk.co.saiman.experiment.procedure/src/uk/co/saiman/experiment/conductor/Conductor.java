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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import uk.co.saiman.data.Data;
import uk.co.saiman.experiment.conductor.event.ConductorEvent;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.dependency.ProductPath;
import uk.co.saiman.experiment.dependency.Result;
import uk.co.saiman.experiment.environment.service.LocalEnvironmentService;
import uk.co.saiman.experiment.executor.service.ExecutorService;
import uk.co.saiman.experiment.output.Output;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.procedure.Procedures;
import uk.co.saiman.experiment.procedure.json.JsonInstructionFormat;
import uk.co.saiman.experiment.procedure.json.JsonProcedureFormat;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;
import uk.co.saiman.log.Log;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

public class Conductor implements Output {
  private final ReentrantLock lock;

  private final StorageConfiguration<?> storageConfiguration;
  private final JsonInstructionFormat instructionFormat;
  private final LocalEnvironmentService environmentService;
  private final Log log;
  private final Executor executor;

  private final Map<WorkspaceExperimentPath, InstructionExecution> progress;
  private Procedure procedure;
  private Data<Procedure> data;

  private final HotObservable<ConductorEvent> events = new HotObservable<>();

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

  public void conduct(Procedure procedure) {
    lock.lock();
    try {
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
          .forEach(InstructionExecution::markRemoved);

      procedure
          .instructionPaths()
          .forEach(
              path -> this.progress
                  .compute(
                      path,
                      (p, execution) -> Optional
                          .ofNullable(execution)
                          .orElseGet(() -> new InstructionExecution(this, p)))
                  .updateInstruction(procedure.instruction(path).get(), environment));

      procedure.instructionPaths().forEach(path -> this.progress.get(path).updateDependencies());

      this.progress.replaceAll((path, execution) -> execution.execute() ? execution : null);

      this.procedure = procedure;
    } catch (IOException e) {
      throw new ConductorException(format("Unable to conduct procedure %s", procedure), e);
    } finally {
      lock.unlock();
    }
  }

  Optional<InstructionExecution> findInstruction(WorkspaceExperimentPath path) {
    return Optional.ofNullable(progress.get(path));
  }

  public Optional<Procedure> procedure() {
    return Optional.ofNullable(procedure);
  }

  public void interrupt() {
    lock.lock();
    try {
      var progressIterator = progress.entrySet().iterator();
      while (progressIterator.hasNext()) {
        var progress = progressIterator.next();

        progress.getValue().interrupt();
      }
    } finally {
      lock.unlock();
    }
  }

  public void clear() {
    lock.lock();
    try {
      interrupt();

      try {
        storageConfiguration.locateStorage(procedure.path()).deallocate();
      } catch (IOException e) {
        throw new ConductorException(
            format("Unable to clear conducted procedure %s", procedure),
            e);
      }

      procedure = null;
    } finally {
      lock.unlock();
    }
  }

  Lock lock() {
    return lock;
  }

  @Override
  public Stream<Result<?>> results() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <U extends ExperimentPath<U>> Stream<ProductPath<U, ? extends Result<?>>> resultPaths(
      ExperimentPath<U> path) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T extends Result<?>> T resolveResult(ProductPath<?, T> path) {
    // TODO Auto-generated method stub
    return null;
  }

  public Observable<ConductorEvent> events() {
    return events;
  }
}
