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

import uk.co.saiman.data.Data;
import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.resource.Location;
import uk.co.saiman.observable.Invalidation;

/**
 * The context of an {@link ExperimentType#process(ExecutionContext) experiment
 * execution}, providing information about the current state, and enabling
 * modification of that state.
 * 
 * @author Elias N Vasylenko
 * @param <T>
 *          the type of the executing node
 */
public interface ProcessingContext<T, R> {
  /**
   * @return the currently executing experiment node
   */
  ExperimentNode<T, R> node();

  /**
   * Invocation of this method is optional, and it may only be invoked at most
   * once during an execution.
   * <p>
   * This method causes the execution of all child nodes of the currently
   * executing node, returning once they have stopped processing. This method may
   * only be invoked once for a given execution. If it is not invoked, then the
   * children will be processed after the execution completes.
   * <p>
   * If this method is invoked then
   * <p>
   * If this method is invoked then the currently executing node will only become
   * complete once the execution of all the child nodes are complete, and if and
   * of the child nodes fail then this node will also fail.
   * 
   * @return true if the children were processed successfully
   * @throws ExperimentException
   *           if invoked multiple times
   */
  void processChildren();

  /**
   * Get a location which can be used to persist resource artifacts of this
   * execution. Typically a resource in this location is used to construct one or
   * more related {@link Data data} instances, one of which will be set as the
   * {@link #setResultData(Data) result} of the execution.
   * <p>
   * The location will be empty before execution begins.
   * 
   * @return an interface over the execution location
   */
  Location getLocation();

  /**
   * Set a preliminary partial result value for this execution.
   * <p>
   * This method may be invoked multiple times during processing. The purpose is
   * to support live-updating of result data, and any values passed to this method
   * will be overridden by the return value of
   * {@link ExperimentType#process(ProcessingContext) execution} once processing
   * completes.
   * 
   * @param value
   *          the preliminary result
   */
  void setPartialResult(R value);

  /**
   * Set a preliminary partial result value for this execution.
   * <p>
   * This method may be invoked multiple times during processing. The purpose is
   * to support live-updating of result data, and any values passed to this method
   * will be overridden by the return value of
   * {@link ExperimentType#process(ProcessingContext) execution} once processing
   * completes.
   * 
   * @param value
   *          an invalidation representing the preliminary result
   */
  void setPartialResult(Invalidation<R> value);

  /**
   * Set the result data for this execution. If the value of the given data
   * matches the return value of {@link ExperimentType#process(ProcessingContext)
   * execution} once it completes it does not have to be rewritten. This means
   * that expensive disk IO can be performed during the experiment process rather
   * than saved until the end.
   * <p>
   * This method may be invoked at most once during any given execution, and this
   * precludes invocation of {@link #setResultFormat(String, DataFormat)} or
   * {@link #setResultFormat(String, String)} during the same execution.
   */
  void setResultData(Data<R> data);

  /**
   * Set the result format for this execution. If invoked, then once the
   * {@link ExperimentType#process(ProcessingContext) execution} is complete the
   * returned value will be persisted according to the given file name and format.
   * <p>
   * This method may be invoked at most once during any given execution, and this
   * precludes invocation of {@link #setResultData(Data)} or
   * {@link #setResultFormat(String, String)} during the same execution.
   * 
   * @param name
   *          the name of the result data file
   * @param format
   *          the format of the result data file
   */
  void setResultFormat(String name, DataFormat<R> format);

  /**
   * Set the result format extensions for this execution. If invoked, then once
   * the {@link ExperimentType#process(ProcessingContext) execution} is complete
   * the returned value will be persisted according to the given file name and a
   * format matching the given extension.
   * <p>
   * This method may be invoked at most once during any given execution, and this
   * precludes invocation of {@link #setResultData(Data)} or
   * {@link #setResultFormat(String, DataFormat)} during the same execution.
   * 
   * @param name
   *          the name of the result data file
   * @param extension
   *          the extension of the format of the result data file
   */
  void setResultFormat(String name, String extension);
}
