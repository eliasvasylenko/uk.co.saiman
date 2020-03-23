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

import uk.co.saiman.experiment.conductor.event.ConductorEvent;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.output.Output;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

public class Schedule {
  private final Scheduler scheduler;
  private final Procedure scheduledProcedure;
  private final Optional<Procedure> previouslyConductedProcedure;

  private final HotObservable<ConductorEvent> events = new HotObservable<>();

  public Schedule(Scheduler scheduler, Procedure procedure) {
    this.scheduler = scheduler;
    this.scheduledProcedure = procedure;
    this.previouslyConductedProcedure = scheduler.getConductor().procedure();
  }

  public Scheduler getScheduler() {
    return scheduler;
  }

  public Optional<Procedure> getPreviouslyConductedProcedure() {
    return previouslyConductedProcedure;
  }

  public Procedure getScheduledProcedure() {
    return scheduledProcedure;
  }

  public void unschedule() {
    scheduler.unschedule(this);
  }

  public Output conduct() {
    return scheduler.conduct(this);
  }

  public Observable<ConductorEvent> events() {
    return events;
  }

  Optional<Instruction> getParent(Instruction instruction) {
    return instruction.path().parent().flatMap(scheduledProcedure::instruction);
  }
}
