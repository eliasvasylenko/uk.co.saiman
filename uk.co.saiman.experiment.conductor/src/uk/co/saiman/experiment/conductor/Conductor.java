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

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import uk.co.saiman.experiment.environment.service.LocalEnvironmentService;
import uk.co.saiman.experiment.executor.service.ExecutorService;
import uk.co.saiman.experiment.output.Output;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.procedure.json.JsonInstructionFormat;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.log.Log;

public class Conductor {
  private final StorageConfiguration<?> storageConfiguration;
  private final JsonInstructionFormat instructionFormat;
  private final LocalEnvironmentService environmentService;
  private final Log log;

  private final Executor executor;
  private final ReentrantLock lock;

  private ConductorOutput output;

  public Conductor(
      StorageConfiguration<?> storageConfiguration,
      ExecutorService executorService,
      LocalEnvironmentService environmentService,
      Log log) {
    this.storageConfiguration = requireNonNull(storageConfiguration);
    this.instructionFormat = new JsonInstructionFormat(executorService);
    this.environmentService = requireNonNull(environmentService);
    this.log = log;

    this.executor = Executors.defaultThreadFactory()::newThread;
    this.lock = new ReentrantLock();

    this.output = new ConductorOutput(this);
  }

  StorageConfiguration<?> storageConfiguration() {
    return storageConfiguration;
  }

  JsonInstructionFormat instructionFormat() {
    return instructionFormat;
  }

  LocalEnvironmentService environmentService() {
    return environmentService;
  }

  Log log() {
    return log;
  }

  Executor getExecutor() {
    return executor;
  }

  Lock lock() {
    return lock;
  }

  public ConductorOutput getOutput() {
    return output;
  }

  public Output conduct(Procedure procedure) {
    return output = output.succeedWithProcedure(procedure);
  }

  public Output clear() {
    return output = output.succeedWithClear();
  }
}
