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

import java.io.IOException;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.data.Data;
import uk.co.saiman.data.resource.Location;
import uk.co.saiman.experiment.dependency.Condition;
import uk.co.saiman.experiment.dependency.Resource;
import uk.co.saiman.experiment.dependency.Result;
import uk.co.saiman.experiment.executor.ExecutionContext;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.variables.Variables;
import uk.co.saiman.log.Log;

public class ExecutorProgress {
  private final Conductor scheduler;
  private final Instruction instruction;

  public ExecutorProgress(Conductor scheduler, Instruction instruction) {
    this.scheduler = scheduler;
    this.instruction = instruction;
  }

  void updateInstruction(Instruction instruction) {

  }

  void interrupt() {

  }

  public void conduct(Executor executor) {
    executor.execute(new ExecutionContext() {
      @Override
      public Location getLocation() {
        try {
          return scheduler.storageConfiguration().locateStorage(instruction.path()).location();
        } catch (IOException e) {
          throw new ConductorException(
              format("Failed to allocate storage for %s", instruction.path()));
        }
      }

      @Override
      public Variables getVariables() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public <T> Condition<T> acquireCondition(Class<T> source) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public <T> Resource<T> acquireResource(Class<T> source) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public <T> Result<T> acquireResult(Class<T> source) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public <T> Stream<Result<T>> acquireResults(Class<T> source) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public <U> void prepareCondition(Class<U> condition, U resource) {
        // TODO Auto-generated method stub
        
      }

      @Override
      public <R> void observePartialResult(Class<R> observation, Supplier<? extends R> value) {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void completeObservation(Class<?> observation) {
        // TODO Auto-generated method stub
        
      }

      @Override
      public <R> void setResultData(Class<R> observation, Data<R> data) {
        // TODO Auto-generated method stub
        
      }

      @Override
      public Log log() {
        // TODO Auto-generated method stub
        return null;
      }
    });
  }
}
