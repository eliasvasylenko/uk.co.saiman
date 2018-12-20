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
 * This file is part of uk.co.saiman.instrument.stage.
 *
 * uk.co.saiman.instrument.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.stage.composed;

import java.util.concurrent.TimeUnit;

import uk.co.saiman.instrument.DeviceControlImpl;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.StageControl;

public class ComposedStageControl<T> extends DeviceControlImpl<ComposedStage<T, ?>>
    implements StageControl<T> {
  public ComposedStageControl(ComposedStage<T, ?> device, long timeout, TimeUnit unit) {
    super(device, timeout, unit);
  }

  @Override
  public SampleState requestExchange() {
    return getDevice().requestExchange();
  }

  @Override
  public SampleState requestAnalysis() {
    return getDevice().requestAnalysis();
  }

  @Override
  public SampleState requestAnalysisLocation(T location) {
    return getDevice().requestAnalysisLocation(location);
  }
}
