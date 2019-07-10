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
 * This file is part of uk.co.saiman.experiment.msapex.
 *
 * uk.co.saiman.experiment.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.msapex.provider.workspace;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.msapex.i18n.ExperimentProperties;
import uk.co.saiman.experiment.msapex.provider.ExperimentProvider;
import uk.co.saiman.experiment.msapex.provider.ExperimentProviderDescriptor;
import uk.co.saiman.properties.PropertyLoader;

@Component
public class WorkspaceExperimentProviderDescriptor implements ExperimentProviderDescriptor {
  public static final String ID = "uk.co.saiman.experiment.provider.workspace";

  @Reference
  PropertyLoader propertyLoader;

  @Override
  public String getLabel() {
    return propertyLoader
        .getProperties(ExperimentProperties.class)
        .newWorkspaceExperiment()
        .toString();
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public Class<? extends ExperimentProvider> getProviderClass() {
    return WorkspaceExperimentProvider.class;
  }

  @Override
  public String getIconURI() {
    // TODO Auto-generated method stub
    return null;
  }
}
