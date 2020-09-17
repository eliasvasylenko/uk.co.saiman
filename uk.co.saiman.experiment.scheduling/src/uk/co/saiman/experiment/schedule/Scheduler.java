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
 * This file is part of uk.co.saiman.experiment.scheduling.
 *
 * uk.co.saiman.experiment.scheduling is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.scheduling is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.schedule;

import java.util.Optional;
import java.util.function.Supplier;

import uk.co.saiman.experiment.conductor.Conductor;
import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.executor.service.ExecutorService;
import uk.co.saiman.experiment.output.Output;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.log.Log;

/**
 * A scheduler provides management and feedback facilities around a target
 * conductor. It allows users to schedule a procedure to be conducted, then
 * inspect what changes this would incur against the previously conducted
 * experiment, if any, before proceeding.
 * <p>
 * TODO "automatic" executors should be processed as soon as they are scheduled,
 * regardless of what data would be overwritten as a result. This requires the
 * "diff" calculation to be a little more thorough, such that we can extract a
 * subset of changes for partial application.
 * 
 * @author Elias N Vasylenko
 *
 */
public class Scheduler {
  private Schedule schedule;
  private final StorageConfiguration<?> storageConfiguration;
  private final ExecutorService executorService;
  private final Conductor conductor;

  public Scheduler(
      StorageConfiguration<?> storageConfiguration,
      ExecutorService executorService,
      Supplier<Environment> environment,
      Log log) {
    this.schedule = null;
    this.storageConfiguration = storageConfiguration;
    this.executorService = executorService;
    this.conductor = new Conductor(storageConfiguration, executorService, environment, log);
  }

  public StorageConfiguration<?> getStorageConfiguration() {
    return storageConfiguration;
  }

  public ExecutorService getExecutorService() {
    return executorService;
  }

  public Optional<Schedule> getSchedule() {
    return Optional.ofNullable(schedule);
  }

  Conductor getConductor() {
    return conductor;
  }

  public Output getOutput() {
    return conductor.getOutput();
  }

  public synchronized Schedule scheduleProcedure(Procedure procedure) {
    schedule = new Schedule(this, procedure);
    return schedule;
  }

  public synchronized Schedule scheduleClear() {
    schedule = new Schedule(this, null);
    return schedule;
  }

  synchronized void unschedule(Schedule schedule) {
    assertFresh(schedule);

    this.schedule = null;
  }

  synchronized Output conduct(Schedule schedule) {
    assertFresh(schedule);

    return schedule.getScheduledProcedure().map(conductor::conduct).orElseGet(conductor::clear);
  }

  private void assertFresh(Schedule schedule) {
    if (this.schedule != schedule) {
      throw new SchedulingException("Schedule is stale");
    }
  }
}
