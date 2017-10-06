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
package uk.co.saiman.msapex.experiment;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.fx.core.di.LocalInstance;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.Constants;

import aQute.bnd.annotation.headers.RequireCapability;
import javafx.fxml.FXMLLoader;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.experiment.WorkspaceFactory;

/**
 * Experiment management view part. Manage experiments and their results in the
 * experiment tree.
 * 
 * @author Elias N Vasylenko
 */
/*
 * Specify a service capability requirement on the ExperimentWorkspaceFactory
 * injection via the bundle manifest.
 */
@RequireCapability(
    ns = ExperimentAddon.OSGI_SERVICE,
    filter = "(" + Constants.OBJECTCLASS + "=uk.co.saiman.experiment.WorkspaceFactory)")
public class ExperimentAddon {
  static final String OSGI_SERVICE = "osgi.service";
  static final String ADD_EXPERIMENT_COMMAND = "uk.co.saiman.msapex.experiment.command.addexperiment";

  @Inject
  private IEclipseContext context;

  @Inject
  private IAdapterManager adapterManager;
  private ExperimentNodeAdapterFactory experimentNodeAdapterFactory;
  private ExperimentAdapterFactory experimentAdapterFactory;

  @PostConstruct
  void initialize(
      @LocalInstance FXMLLoader loader,
      @Named(E4Workbench.INSTANCE_LOCATION) Location instanceLocation,
      WorkspaceFactory workspaceFactory) {
    Path workspaceLocation;
    try {
      workspaceLocation = Paths.get(instanceLocation.getURL().toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }

    Workspace workspace = workspaceFactory.openWorkspace(workspaceLocation);

    context.set(Workspace.class, workspace);

    experimentNodeAdapterFactory = new ExperimentNodeAdapterFactory(adapterManager, workspace);
    experimentAdapterFactory = new ExperimentAdapterFactory(
        adapterManager,
        experimentNodeAdapterFactory);

    /*
     * 
     * 
     * TODO invalidate the AdapterManager cache when new types of experiment are
     * added to the workspace
     * 
     * 
     * 
     */
  }

  @PreDestroy
  void destroy() {
    experimentAdapterFactory.unregister();
    experimentNodeAdapterFactory.unregister();
  }
}
