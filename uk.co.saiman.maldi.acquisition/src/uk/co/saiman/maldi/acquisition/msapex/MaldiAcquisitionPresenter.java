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
 * This file is part of uk.co.saiman.maldi.acquisition.
 *
 * uk.co.saiman.maldi.acquisition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.acquisition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.acquisition.msapex;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.experiment.msapex.environment.ResourcePresenter;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;

@Component(enabled = true, immediate = true)
public class MaldiAcquisitionPresenter implements ResourcePresenter<AcquisitionDevice> {
  @Override
  public String getLocalizedLabel() {
    return "Maldi Acquisition Device";
  }

  @Override
  public String getIconURI() {
    return "fugue:size16/system-monitor.png";
  }

  @Override
  public Class<AcquisitionDevice> getResourceClass() {
    return AcquisitionDevice.class;
  }
}
