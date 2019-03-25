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
 * This file is part of uk.co.saiman.msapex.experiment.
 *
 * uk.co.saiman.msapex.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment.provider.workspace;

import static uk.co.saiman.experiment.storage.filesystem.FileSystemStore.FILE_SYSTEM_STORE_ID;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.msapex.experiment.provider.AddExperimentWizard;
import uk.co.saiman.msapex.experiment.provider.ExperimentProvider;

@Component
public class WorkspaceExperimentProviderDescriptor implements AddExperimentWizard {

  @Override
  public String getLabel() {
    return "New Workspace Experiment"; // TODO l10n
  }

  @Override
  public String getId() {
    return FILE_SYSTEM_STORE_ID;
  }

  @Override
  public Class<? extends ExperimentProvider> getFirstPage() {
    return WorkspaceExperimentProvider.class;
  }

  @Override
  public String getIconURI() {
    // TODO Auto-generated method stub
    return null;
  }
}
