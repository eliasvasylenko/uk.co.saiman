/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.api.
 *
 * uk.co.saiman.experiment.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment;

import uk.co.saiman.data.CachingData;
import uk.co.saiman.data.Data;
import uk.co.saiman.data.Resource;
import uk.co.saiman.data.format.DataFormat;

/**
 * The context of an {@link ExperimentType#execute(ExecutionContext) experiment
 * execution}, providing information about the current state, and enabling
 * modification of that state.
 * 
 * @author Elias N Vasylenko
 * @param <T>
 *          the type of the executing node
 */
public interface ExecutionContext<T, R> {
  /**
   * @return the currently executing experiment node
   */
  ExperimentNode<T, R> node();

  /**
   * Execute the child nodes of the currently executing node. This method may
   * only be invoked once for a given execution. If it is not invoked, then the
   * children will be executed after the execution completes.
   * <p>
   * This method is useful in cases where an experiment node must ensure that
   * its execution state is maintained during the execution of its children.
   * 
   * @throws ExperimentException
   *           if invoked multiple times
   */
  void executeChildren();

  /**
   * Get a handle on a resource with the given name and extension. Multiple
   * resources may be obtained during a given execution, but the combination of
   * name and extension must be locally unique. Typically a resource is used to
   * construct one or more related {@link Data data} instances, one of which
   * will be set as the {@link #setResult(Data) result} of the execution.
   * 
   * @param name
   *          the name of the resource
   * @param extension
   *          the file extension of the resource, must be alphanumeric
   * @return an interface over the requested resource
   */
  Resource getResource(String name, String extension);

  default <U> Data<U> getData(String name, DataFormat<U> format) {
    return new CachingData<>(getResource(name, format.getExtension()), format);
  }

  /**
   * Set the result data for this execution. The value of the given data is
   * expected to match the return value of the
   * {@link ExperimentType#execute(ExecutionContext) execution} once it
   * completes. At this point, if necessary, the data will be {@link Data#save()
   * saved}.
   * <p>
   * This method may be invoked at most once during any given execution, and
   * this precludes invocation of {@link #setResultFormat(String, DataFormat)}
   * or {@link #setResultFormat(String, String)} during the same execution.
   * <p>
   * This method returns a wrapper around the given data item which invalidates
   * the {@link Result result} associated with the execution when it is
   * {@link Data#set(Object) changed} or {@link Data#makeDirty() dirtied}. This
   * allows an experiment in progress to feed the application with
   * {@link Result#observe(uk.co.saiman.observable.Observer) live updates} to
   * the result data.
   * 
   * @param data
   *          the data item representing the execution result
   * @return a wrapper around the given data item
   */
  Data<R> setResult(Data<? extends R> data);

  /**
   * Set the result format for this execution. If invoked, then once the
   * {@link ExperimentType#execute(ExecutionContext) execution} is complete the
   * returned value will be persisted according to the given file name and
   * format.
   * <p>
   * This method may be invoked at most once during any given execution, and
   * this precludes invocation of {@link #setResult(Data)} or
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
   * the {@link ExperimentType#execute(ExecutionContext) execution} is complete
   * the returned value will be persisted according to the given file name and a
   * format matching the given extension.
   * <p>
   * This method may be invoked at most once during any given execution, and
   * this precludes invocation of {@link #setResult(Data)} or
   * {@link #setResultFormat(String, DataFormat)} during the same execution.
   * 
   * @param name
   *          the name of the result data file
   * @param extension
   *          the extension of the format of the result data file
   */
  void setResultFormat(String name, String extension);
}
