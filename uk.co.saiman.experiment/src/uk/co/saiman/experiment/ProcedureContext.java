/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import java.util.function.Supplier;

import uk.co.saiman.data.Data;
import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.resource.Location;

/**
 * The context of an {@link Procedure#proceed(ProcedureContext) experiment
 * execution}, providing information about the current state, and enabling
 * modification of that state.
 * 
 * @author Elias N Vasylenko
 * @param <T> the type of the executing node
 */
public interface ProcedureContext<T> {
  /**
   * @return the currently executing experiment node
   */
  ExperimentStep<T> node();

  Hold acquireHold(Condition condition);

  <U extends AutoCloseable> U acquireResource(Resource<U> resource);

  /**
   * Wait for the given requirement to be satisfied.
   * 
   * @param requirement
   * @throws ExperimentException if the requirement can not be fulfilled
   * @return the item which satisfies the requirement
   */
  <U> Result<? extends U> acquireResult(Dependency<U> requirement);

  /**
   * Get a location which can be used to persist resource artifacts of this
   * execution. Typically a resource in this location is used to construct one or
   * more related {@link Data data} instances, one of which will be set as the
   * {@link #setResultData(Observation, Data) result} of the execution.
   * <p>
   * The location will be empty before execution begins.
   * 
   * @return an interface over the execution location
   */
  Location getLocation();

  void enterCondition(Condition condition);

  void exitCondition(Condition condition);

  default void holdCondition(Condition condition) {
    enterCondition(condition);
    exitCondition(condition);
  }

  /**
   * Set a preliminary partial result value for this execution.
   * <p>
   * This method may be invoked multiple times during processing. The purpose is
   * to support live-updating of result data, and any values passed to this method
   * will be overridden by the return value of
   * {@link Procedure#proceed(ProcedureContext) execution} once processing
   * completes.
   * 
   * @param value the preliminary result
   */
  <R> void setPartialResult(Observation<R> observation, R value);

  /**
   * Set a preliminary partial result value for this execution.
   * <p>
   * This method may be invoked multiple times during processing. The purpose is
   * to support live-updating of result data, and any values passed to this method
   * will be overridden by the return value of
   * {@link Procedure#proceed(ProcedureContext) execution} once processing
   * completes.
   * 
   * @param value an invalidation representing the preliminary result
   */
  <R> void setPartialResult(Observation<R> observation, Supplier<? extends R> value);

  /**
   * Set the result data for this execution. If the {@link Data#get() value} of
   * the given data matches the return value of
   * {@link Procedure#proceed(ProcedureContext) execution} once it completes it
   * does not have to be rewritten. This means that expensive disk IO can be
   * performed during the experiment process rather than saved until the end.
   * <p>
   * This method may be invoked at most once during any given execution, and this
   * precludes invocation of
   * {@link #setResultFormat(Observation, String, DataFormat)} during the same
   * execution.
   */
  <R> void setResultData(Observation<R> observation, Data<R> data);

  /**
   * Set the result format for this execution. If invoked, then once the
   * {@link Procedure#proceed(ProcedureContext) execution} is complete the
   * returned value will be persisted according to the given file name and format.
   * <p>
   * This method may be invoked at most once during any given execution, and this
   * precludes invocation of {@link #setResultData(Observation, Data)} during the
   * same execution.
   * 
   * @param name   the name of the result data file
   * @param format the format of the result data file
   */
  default <R> void setResultFormat(Observation<R> observation, String name, DataFormat<R> format) {
    setResultData(observation, Data.locate(getLocation(), name, format));
  }

  default <R> void setResult(Observation<R> observation, R value) {
    setPartialResult(observation, value);
    completeObservation(observation);
  }

  void completeObservation(Observation<?> observation);
}
