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

import static java.lang.String.format;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.data.Data;
import uk.co.saiman.data.resource.Location;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.procedure.ConditionRequirement;
import uk.co.saiman.experiment.procedure.ConductionContext;
import uk.co.saiman.experiment.procedure.Conductor;
import uk.co.saiman.experiment.procedure.Instruction;
import uk.co.saiman.experiment.procedure.ResultRequirement;
import uk.co.saiman.experiment.product.Observation;
import uk.co.saiman.experiment.product.Preparation;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Result;
import uk.co.saiman.experiment.variables.Variable;

public class InstructionProgress {
  private final Scheduler scheduler;
  private final ExperimentPath<Absolute> path;
  private final Instruction instruction;

  public InstructionProgress(
      Scheduler scheduler,
      ExperimentPath<Absolute> path,
      Instruction instruction) {
    this.scheduler = scheduler;
    this.path = path;
    this.instruction = instruction;
  }

  void updateInstruction(Instruction instruction) {

  }

  void interrupt() {

  }

  public <T extends Product> void conduct(Conductor<T> conductor) {
    conductor.conduct(new ConductionContext<T>() {
      @Override
      public Instruction instruction() {
        return instruction;
      }

      @Override
      public T dependency() {
        return conductor.directRequirement() instanceof ConditionRequirement<?> ? null : null;
      }

      @Override
      public <U> U acquireCondition(ConditionRequirement<U> resource) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public <U> Result<? extends U> acquireResult(ResultRequirement<U> requirement) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public <U> Stream<Result<? extends U>> acquireResults(ResultRequirement<U> requirement) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public Location getLocation() {
        try {
          return scheduler.getStorageConfiguration().locateStorage(path).location();
        } catch (IOException e) {
          throw new SchedulingException(format("Failed to allocate storage for %s", path));
        }
      }

      @Override
      public <U> void prepareCondition(Preparation<U> condition, U resource) {
        // TODO Auto-generated method stub

      }

      @Override
      public <R> void setPartialResult(Observation<R> observation, Supplier<? extends R> value) {
        // TODO Auto-generated method stub

      }

      @Override
      public <R> void setResultData(Observation<R> observation, Data<R> data) {
        // TODO Auto-generated method stub

      }

      @Override
      public void completeObservation(Observation<?> observation) {
        // TODO Auto-generated method stub

      }

      @Override
      public <U> Optional<U> getOptionalVariable(Variable<U> variable) {
        // TODO Auto-generated method stub
        return null;
      }
    });
  }
}
