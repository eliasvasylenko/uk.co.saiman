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
package uk.co.saiman.msapex.experiment;

import static org.eclipse.e4.ui.internal.workbench.E4Workbench.INSTANCE_LOCATION;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.fx.core.di.LocalInstance;
import org.eclipse.fx.core.di.Service;
import org.eclipse.osgi.service.datalocation.Location;

import javafx.fxml.FXMLLoader;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.experiment.filesystem.FileSystemLocationManager;
import uk.co.saiman.experiment.impl.WorkspaceImpl;
import uk.co.saiman.experiment.persistence.json.JsonPersistenceManager;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

/**
 * Addon for registering an experiment workspace in the root application
 * context. The addon also registers and manages an eclipse adapter over the
 * experiment types available in the workspace.
 * 
 * @author Elias N Vasylenko
 */
public class WorkspaceAddon {
  static final String OSGI_SERVICE = "osgi.service";

  @Inject
  private IEclipseContext context;

  @Inject
  private IAdapterManager adapterManager;
  private ExperimentNodeAdapterFactory experimentNodeAdapterFactory;
  private ExperimentAdapterFactory experimentAdapterFactory;

  @Inject
  private Log log;
  @Inject
  @Localize
  private ExperimentProperties text;

  private List<ExperimentType<?, ?>> experimentTypes;

  @PostConstruct
  void initialize(
      @LocalInstance FXMLLoader loader,
      @Named(INSTANCE_LOCATION) Location instanceLocation,
      @Service List<ExperimentType<?, ?>> experimentTypes) {
    this.experimentTypes = new ArrayList<>(experimentTypes);

    try {
      initializeWorkspace(instanceLocation);
      initializeAdapters();
    } catch (Exception e) {
      log.log(Level.ERROR, e);
      e.printStackTrace();
    }
  }

  private void initializeAdapters() {
    List<ExperimentType<?, ?>> experimentTypes = new ArrayList<>(this.experimentTypes.size() + 1);
    experimentTypes.addAll(this.experimentTypes);
    experimentTypes.add(context.get(Workspace.class).getExperimentRootType());
    experimentNodeAdapterFactory = new ExperimentNodeAdapterFactory(
        adapterManager,
        experimentTypes);
    experimentAdapterFactory = new ExperimentAdapterFactory(
        adapterManager,
        experimentNodeAdapterFactory);
  }

  private Workspace initializeWorkspace(Location instanceLocation) throws URISyntaxException {
    Path workspaceLocation = Paths.get(instanceLocation.getURL().toURI());

    Workspace workspace = new WorkspaceImpl(
        new FileSystemLocationManager(workspaceLocation),
        new JsonPersistenceManager(workspaceLocation, experimentTypes),
        log,
        text);

    context.set(Workspace.class, workspace);

    return workspace;
  }

  @PreDestroy
  void destroy() {
    if (experimentAdapterFactory != null)
      experimentAdapterFactory.unregister();
    if (experimentNodeAdapterFactory != null)
      experimentNodeAdapterFactory.unregister();
  }
}
