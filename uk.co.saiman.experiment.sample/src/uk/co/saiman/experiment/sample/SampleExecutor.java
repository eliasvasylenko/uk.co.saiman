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
 * This file is part of uk.co.saiman.experiment.sample.
 *
 * uk.co.saiman.experiment.sample is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.sample is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.sample;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import uk.co.saiman.experiment.executor.ExecutionContext;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.ExecutorException;
import uk.co.saiman.experiment.executor.PlanningContext;
import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.experiment.variables.VariableCardinality;
import uk.co.saiman.instrument.sample.SampleDevice;

/**
 * Configure the sample position to perform an experiment at. Typically most
 * other experiment nodes will be descendant to a sample experiment node, such
 * that they operate on the configured sample.
 * 
 * @author Elias N Vasylenko
 */
public interface SampleExecutor<T> extends Executor {
  Variable<T> sampleLocation();

  Class<?> samplePreparation();

  Class<? extends SampleDevice<T>> sampleDevice();

  @Override
  default void plan(PlanningContext context) {
    context.declareVariable(sampleLocation(), VariableCardinality.REQUIRED);
    context.preparesCondition(samplePreparation());
  }

  @Override
  default void execute(ExecutionContext context) {
    var location = context.getVariable(sampleLocation());
    var device = context.acquireResource(sampleDevice()).value();

    try (var controller = device.acquireControl(10, TimeUnit.SECONDS)) {
      controller.requestAnalysis(location);
      context.prepareCondition(samplePreparation(), null);

    } catch (InterruptedException | TimeoutException e) {
      throw new ExecutorException("Failed to acquire control of sample device.", e);
    }
  }
}
