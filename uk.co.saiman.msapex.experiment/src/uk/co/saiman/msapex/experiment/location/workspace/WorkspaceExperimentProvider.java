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
package uk.co.saiman.msapex.experiment.location.workspace;

import static org.eclipse.e4.ui.internal.workbench.E4Workbench.INSTANCE_LOCATION;

import java.net.URISyntaxException;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.osgi.service.datalocation.Location;

import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.Store;
import uk.co.saiman.msapex.experiment.RenameExperimentDialog;
import uk.co.saiman.msapex.experiment.i18n.ExperimentProperties;
import uk.co.saiman.msapex.experiment.location.ExperimentProvider;
import uk.co.saiman.msapex.experiment.workspace.Workspace;

public class WorkspaceExperimentProvider implements ExperimentProvider<Void> {
  private final Workspace workspace;
  private final ExperimentProperties text;

  @Inject
  public WorkspaceExperimentProvider(
      @Named(INSTANCE_LOCATION) Location instanceLocation,
      Workspace workspace,
      @Localize ExperimentProperties text)
      throws URISyntaxException {
    this.workspace = workspace;
    this.text = text;
  }

  @Override
  public Store<Void> store() {
    return null;
  }

  @Override
  public void createExperiments(Function<Void, Experiment> createExperiment) {
    Experiment experiment = createExperiment.apply(null);
    new RenameExperimentDialog(workspace, text, null);
    experiment.getVariables().setName("name");
  }
}
