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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.schedule.event.SchedulingEvent;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

public class Schedule {
  private final Scheduler scheduler;
  private final Procedure procedure;
  private final Optional<Products> currentProducts;

  private final Map<ExperimentPath<Absolute>, ScheduledInstruction> scheduledInstructions;

  private final HotObservable<SchedulingEvent> events = new HotObservable<>();

  public Schedule(Scheduler scheduler, Procedure procedure) {
    this.scheduler = scheduler;
    this.procedure = procedure;
    this.currentProducts = scheduler.getProducts();

    this.scheduledInstructions = new HashMap<>();
  }

  public Scheduler getScheduler() {
    return scheduler;
  }

  public Optional<Products> currentProducts() {
    return currentProducts;
  }

  public Procedure getProcedure() {
    return procedure;
  }

  public Optional<ScheduledInstruction> scheduledInstruction(ExperimentPath<Absolute> path) {
    return Optional.ofNullable(scheduledInstructions.get(path));
  }

  public void unschedule() {
    scheduler.unschedule(this);
  }

  public Products proceed() {
    return scheduler.proceed(this);
  }

  public Observable<SchedulingEvent> events() {
    return events;
  }

  Optional<ScheduledInstruction> getParent(ScheduledInstruction scheduledInstruction) {
    return scheduledInstruction
        .productPath()
        .map(ProductPath::getExperimentPath)
        .map(scheduledInstructions::get);
  }

  Stream<ScheduledInstruction> getChildren(ScheduledInstruction scheduledInstruction) {
    return procedure
        .dependentInstructions(scheduledInstruction.experimentPath())
        .map(scheduledInstructions::get);
  }
}
