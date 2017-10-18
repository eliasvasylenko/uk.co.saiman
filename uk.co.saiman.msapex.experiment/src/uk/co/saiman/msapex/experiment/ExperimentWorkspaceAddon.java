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

import static org.eclipse.e4.ui.internal.workbench.E4Workbench.INSTANCE_LOCATION;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.fx.core.di.LocalInstance;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.Constants;

import aQute.bnd.annotation.headers.RequireCapability;
import javafx.fxml.FXMLLoader;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.experiment.WorkspaceFactory;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

/**
 * Addon for registering an experiment workspace in the root application
 * context. The addon also registers and manages an eclipse adapter over the
 * experiment types available in the workspace.
 * 
 * @author Elias N Vasylenko
 */
/*
 * Specify a service capability requirement on the ExperimentWorkspaceFactory
 * injection via the bundle manifest.
 */
@RequireCapability(
    ns = ExperimentWorkspaceAddon.OSGI_SERVICE,
    filter = "(" + Constants.OBJECTCLASS + "=uk.co.saiman.experiment.WorkspaceFactory)")
public class ExperimentWorkspaceAddon {
  static final String OSGI_SERVICE = "osgi.service";

  @Inject
  private IEclipseContext context;

  @Inject
  private IAdapterManager adapterManager;
  private ExperimentNodeAdapterFactory experimentNodeAdapterFactory;
  private ExperimentAdapterFactory experimentAdapterFactory;

  @Inject
  private Log log;

  @PostConstruct
  void initialize(
      @LocalInstance FXMLLoader loader,
      @Named(INSTANCE_LOCATION) Location instanceLocation,
      WorkspaceFactory workspaceFactory) {
    try {
      Path workspaceLocation = Paths.get(instanceLocation.getURL().toURI());

      Workspace workspace = workspaceFactory.openWorkspace(workspaceLocation);

      context.set(Workspace.class, workspace);

      experimentNodeAdapterFactory = new ExperimentNodeAdapterFactory(adapterManager, workspace);
      experimentAdapterFactory = new ExperimentAdapterFactory(
          adapterManager,
          experimentNodeAdapterFactory);
    } catch (Exception e) {
      log.log(Level.ERROR, e);
    }

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
