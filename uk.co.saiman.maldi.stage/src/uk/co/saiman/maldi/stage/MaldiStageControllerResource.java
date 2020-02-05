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
package uk.co.saiman.maldi.stage;

import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.environment.osgi.CloseableResourceProvider;
import uk.co.saiman.experiment.environment.osgi.ExclusiveResourceProvider;

@Component(service = ExclusiveResourceProvider.class)
public class MaldiStageControllerResource
    implements CloseableResourceProvider<MaldiStageController> {

  @Override
  public Class<MaldiStageController> getProvision() {
    return MaldiStageController.class;
  }

  @Override
  public MaldiStageController deriveValue(
      GlobalEnvironment globalEnvironment,
      long timeout,
      TimeUnit unit) throws Exception {
    return globalEnvironment.provideValue(MaldiStage.class).acquireControl(timeout, unit);
  }
}
