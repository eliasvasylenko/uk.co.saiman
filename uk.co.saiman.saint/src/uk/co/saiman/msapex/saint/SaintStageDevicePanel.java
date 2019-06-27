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
 * This file is part of uk.co.saiman.saint.
 *
 * uk.co.saiman.saint is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.saint is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.saint;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.instrument.sample.SampleDevice;
import uk.co.saiman.msapex.instrument.sample.SampleDevicePanel;
import uk.co.saiman.saint.stage.impl.SaintSamplePlateExecutor;

@Component
public class SaintStageDevicePanel implements SampleDevicePanel {
  private final SaintSamplePlateExecutor stageExecutor;

  @Activate
  public SaintStageDevicePanel(
      @Reference(name = "stageExecutor") SaintSamplePlateExecutor stageExecutor) {
    this.stageExecutor = stageExecutor;
  }

  @Override
  public SampleDevice<?, ?> device() {
    return stageExecutor.sampleDevice();
  }

  @Override
  public Class<?> paneModelClass() {
    return SaintStageDeviceModel.class;
  }
}
