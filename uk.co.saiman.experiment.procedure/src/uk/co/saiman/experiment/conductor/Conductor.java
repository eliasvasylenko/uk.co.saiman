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
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.dependency.ProductPath;
import uk.co.saiman.experiment.dependency.Result;
import uk.co.saiman.experiment.environment.service.LocalEnvironmentService;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.output.Output;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.procedure.Procedures;
import uk.co.saiman.experiment.procedure.event.ConductorEvent;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.log.Log;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

public class Conductor implements Output {
  private final ReentrantLock lock;

  private final StorageConfiguration<?> storageConfiguration;
  private final LocalEnvironmentService environmentService;
  private final Log log;
  private final Executor executor;

  private Map<ExperimentPath<Absolute>, InstructionExecution> progress;
  private Procedure procedure;

  private final HotObservable<ConductorEvent> events = new HotObservable<>();

  public Conductor(
      StorageConfiguration<?> storageConfiguration,
      LocalEnvironmentService environmentService,
      Log log) {
    this.lock = new ReentrantLock();

    this.storageConfiguration = requireNonNull(storageConfiguration);
    this.environmentService = environmentService;
    this.log = log;

    this.executor = Executors.defaultThreadFactory()::newThread;

    this.progress = Map.of();
  }

  void execute(Runnable runnable) {
    executor.execute(runnable);
  }

  Log log() {
    return log;
  }

  public StorageConfiguration<?> storageConfiguration() {
    return storageConfiguration;
  }

  public void conduct(Procedure procedure) {
    lock.lock();
    try {
      System.out.println("Conducting");
      System.out.println(" - procedure " + procedure.id());
      System.out
          .println(
              " - instructions " + procedure.instructions().map(Instruction::id).collect(toList()));

      var procedureDependents = Procedures.getDependents(procedure);

      var environment = Procedures.openEnvironment(procedure, environmentService, 2, SECONDS);

      var progress = procedure
          .instructions()
          .collect(
              toMap(
                  Instruction::path,
                  instruction -> this.progress
                      .getOrDefault(instruction.path(), new InstructionExecution(this))
                      .update(
                          instruction,
                          procedureDependents.getInstructionDependents(instruction.path()),
                          environment)));
      var previousProgress = this.progress;

      this.procedure = procedure;
      this.progress = progress;

      previousProgress.keySet().removeAll(progress.keySet());
      previousProgress.values().forEach(InstructionExecution::interrupt);

      // now they're all present, kick them off
      this.progress.values().forEach(InstructionExecution::execute);

      System.out.println("Conducting Started");
    } finally {
      lock.unlock();
    }
  }

  Optional<InstructionExecution> findInstruction(ExperimentPath<Absolute> path) {
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
        storageConfiguration.locateStorage(ExperimentPath.toRoot()).deallocate();
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
