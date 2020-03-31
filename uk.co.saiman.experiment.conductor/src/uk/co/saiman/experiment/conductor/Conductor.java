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
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import uk.co.saiman.experiment.environment.service.LocalEnvironmentService;
import uk.co.saiman.experiment.executor.service.ExecutorService;
import uk.co.saiman.experiment.output.Output;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.procedure.json.JsonInstructionFormat;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.log.Log;

/**
 * Conductor for expresiment procedures.
 * <p>
 * A procedure consists of a set of instructions, and to conduct a procedure
 * means to execute all of those instructions. Instructions may produce output,
 * and may depend on one another. The conductor must arrange the execution of
 * instructions so that their interdependencies can be satisfied, and gather
 * their collective outputs.
 * <p>
 * A conductor may be reused any number of times, but only one procedure may be
 * conducted at a time. Conducting a new procedure will overwrite the outputs of
 * any prior procedures, unless some of the instructions are the same and it is
 * possible to reuse previous outputs.
 * 
 * @author Elias N Vasylenko
 */
public class Conductor {
  private final StorageConfiguration<?> storageConfiguration;
  private final JsonInstructionFormat instructionFormat;
  private final LocalEnvironmentService environmentService;
  private final Log log;

  private final java.util.concurrent.ExecutorService executor;
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

    this.executor = new ThreadPoolExecutor(
        8,
        Integer.MAX_VALUE,
        2,
        SECONDS,
        new SynchronousQueue<>());
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

  java.util.concurrent.ExecutorService getExecutor() {
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
