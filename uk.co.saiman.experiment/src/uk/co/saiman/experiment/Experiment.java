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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment;

import static java.util.Objects.requireNonNull;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Stream;

import uk.co.saiman.experiment.event.ExperimentEvent;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.procedure.Instruction;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.procedure.Template;
import uk.co.saiman.experiment.product.Nothing;
import uk.co.saiman.experiment.schedule.Products;
import uk.co.saiman.experiment.schedule.Schedule;
import uk.co.saiman.experiment.schedule.Scheduler;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

public class Experiment {
  private Procedure procedure;
  private NavigableSet<ExperimentPath<Absolute>> enabled = new TreeSet<>();

  private final Scheduler scheduler;

  private Map<ExperimentPath<Absolute>, Reference<Step>> steps = new HashMap<>();

  private final HotObservable<ExperimentEvent> events = new HotObservable<>();

  public Experiment(Procedure procedure, StorageConfiguration<?> storageConfiguration) {
    this.procedure = requireNonNull(procedure);
    this.scheduler = new Scheduler(storageConfiguration);
  }

  public Procedure getProcedure() {
    return procedure;
  }

  public Schedule getSchedule() {
    return scheduler.getSchedule().get();
  }

  public Products getProducts() {
    return scheduler.getProducts().get();
  }

  public String getId() {
    return getSchedule().getProcedure().id();
  }

  public void setId(String id) {
    updateProcedure(p -> p.withId(id));
  }

  private synchronized void updateProcedure(Function<Procedure, Procedure> modifier) {
    updateProcedure(modifier.apply(this.procedure));
  }

  private synchronized boolean updateProcedure(Procedure newProcedure) {
    boolean changed = !procedure.equals(newProcedure);
    if (changed) {
      procedure = newProcedure;
      scheduler.schedule(procedure);
    }
    return changed;
  }

  public StorageConfiguration<?> getStorageConfiguration() {
    return scheduler.getStorageConfiguration();
  }

  public Observable<ExperimentEvent> events() {
    return events;
  }

  public synchronized Step attach(Template<Nothing> step) {

    // TODO

    return null;
  }

  public synchronized void close() {

  }

  Instruction getInstruction(ExperimentPath<?> path) {
    // TODO Auto-generated method stub
    return null;
  }

  void updateInstruction(ExperimentPath<?> path, Instruction instruction) {
    // TODO Auto-generated method stub

  }

  public Stream<Step> getSteps() {
    // TODO Auto-generated method stub
    return null;
  }
}
