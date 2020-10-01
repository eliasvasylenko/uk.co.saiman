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
 * This file is part of uk.co.saiman.experiment.conductor.
 *
 * uk.co.saiman.experiment.conductor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.conductor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.conductor;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import uk.co.saiman.data.Data;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.dependency.ProductPath;
import uk.co.saiman.experiment.dependency.Result;
import uk.co.saiman.experiment.output.Output;
import uk.co.saiman.experiment.output.event.OutputEvent;
import uk.co.saiman.experiment.output.event.OutputSucceededEvent;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.procedure.Procedures;
import uk.co.saiman.experiment.procedure.json.JsonProcedureFormat;
import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.state.json.JsonStateMapFormat;

public class ConductorOutput implements Output {
  private final Conductor conductor;
  private final Procedure procedure;
  private final Data<Procedure> data;

  private final HotObservable<OutputEvent> events = new HotObservable<>();

  private Map<WorkspaceExperimentPath, ConductorInstruction> progress;

  private final ConductorOutput prior;
  private ConductorOutput successor;

  ConductorOutput(Conductor conductor) {
    this.conductor = conductor;
    this.prior = null;
    this.procedure = null;
    this.data = null;
    this.progress = Map.of();
  }

  private ConductorOutput(ConductorOutput prior) {
    this.conductor = prior.getConductor();
    this.prior = prior;
    this.procedure = null;
    this.data = null;
  }

  private ConductorOutput(ConductorOutput prior, Procedure procedure) throws IOException {
    this.conductor = prior.getConductor();
    this.prior = prior;
    this.procedure = procedure;

    var storage = conductor.storageConfiguration().locateStorage(procedure.path());
    var procedureFormat = new JsonProcedureFormat(
        conductor.executorService(),
        procedure.environment(),
        new JsonStateMapFormat());

    this.data = Data.locate(storage.location(), procedure.id().name(), procedureFormat);

    Procedures.validateDependencies(procedure);

  }

  private void start() throws Exception {
    if (procedure == null) {
      prior.terminateProgress();
      this.progress = Map.of();

    } else {
      this.progress = prior.inheritOrTerminateProgress(procedure);

      try {
        data.set(procedure);
        data.save();

        procedure.instructionPaths().forEach(i -> progress.get(i).updateInstruction(procedure));
        procedure.instructionPaths().forEach(i -> progress.get(i).updateDependencies(this));
        procedure.instructionPaths().forEach(i -> progress.get(i).execute());

        // is there any point in this? it will always just immediately follow an
        // outputsucceeded event on the previous output...
        // nextOutput.nextEvent(new OutputBeginEvent(output));

      } finally {
        // TODO attach environment close and end event to the join of any executor
        // threads.
        conductor.getExecutor().execute(() -> {
          progress.values().forEach(e -> {
            try {
              e.join();
            } catch (Exception x) {}
          });
        });
      }
    }
  }

  @Override
  public Stream<Result<?>> results() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <U extends ExperimentPath<U>> Stream<ProductPath<U, ? extends Result<?>>> resultPaths(ExperimentPath<U> path) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T extends Result<?>> T resolveResult(ProductPath<?, T> path) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Observable<OutputEvent> events() {
    return events;
  }

  @Override
  public Optional<Output> successiveOutput() {
    return Optional.ofNullable(successor);
  }

  Optional<ConductorInstruction> findInstruction(WorkspaceExperimentPath path) {
    return Optional.ofNullable(progress.get(path));
  }

  public Optional<Procedure> procedure() {
    return Optional.ofNullable(procedure);
  }

  public ConductorOutput succeedWithProcedure(Procedure procedure) {
    try {
      return succeed(new ConductorOutput(this, procedure));

    } catch (Exception e) {
      var ce = new ConductorException(format("Unable to conduct procedure %s", procedure), e);
      conductor.log().log(Level.ERROR, ce);
      throw ce;
    }
  }

  public ConductorOutput succeedWithClear() {
    try {
      return succeed(new ConductorOutput(this));

    } catch (Exception e) {
      var ce = new ConductorException(format("Unable to terminate conductor"), e);
      conductor.log().log(Level.ERROR, ce);
      throw ce;
    }
  }

  private ConductorOutput succeed(ConductorOutput successor) throws Exception {
    try {
      conductor.lock().lock();

      this.successor = successor;
      try {
        clearData();

        successor.start();
      } finally {
        events.next(new OutputSucceededEvent(this));
      }

      return successor;
    } finally {
      conductor.lock().unlock();
    }
  }

  private void clearData() {
    if (data != null) {
      data.unset();
      data.save();
    }
  }

  private Map<WorkspaceExperimentPath, ConductorInstruction> inheritOrTerminateProgress(Procedure procedure) {
    var thisProgress = new HashMap<>(this.progress);
    var progress = procedure
        .instructionPaths()
        .collect(
            toMap(
                Function.identity(),
                p -> Optional
                    .ofNullable(thisProgress.remove(p))
                    .orElseGet(() -> new ConductorInstruction(conductor, p))));
    thisProgress.values().forEach(ConductorInstruction::remove);
    return progress;
  }

  private void terminateProgress() {
    this.progress.values().forEach(ConductorInstruction::remove);
  }

  Conductor getConductor() {
    return conductor;
  }
}
