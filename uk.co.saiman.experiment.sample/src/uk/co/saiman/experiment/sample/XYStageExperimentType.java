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

import uk.co.saiman.experiment.VoidExecutionContext;
import uk.co.saiman.instrument.stage.XYStageDevice;

/**
 * An {@link SampleExperimentType experiment type} for {@link XYStageDevice XY
 * stage devices}.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of sample configuration for the instrument
 */
public interface XYStageExperimentType<T extends XYStageConfiguration>
    extends SampleExperimentType<T> {
  @Override
  default String getName() {
    return "XY Sample Stage";
  }

  XYStageDevice device();

  @Override
  default void executeVoid(VoidExecutionContext<T> context) {
    device().getXAxis().requestedPosition().set(context.node().getState().getX());
    device().getYAxis().requestedPosition().set(context.node().getState().getY());

    /*
     * TODO listen for interruption of the stage position and cancel/fail/warn if it
     * is disturbed. Possibly acquire some sort of lock?
     */

    context.processChildren();
  }
}
