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
package uk.co.saiman.msapex.experiment.workspace;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;
import static org.eclipse.e4.ui.internal.workbench.E4Workbench.INSTANCE_LOCATION;
import static org.osgi.service.component.ComponentConstants.COMPONENT_NAME;
import static uk.co.saiman.experiment.filesystem.FileSystemStore.FILE_SYSTEM_STORE_ID;
import static uk.co.saiman.log.Log.Level.INFO;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.OSGiBundle;
import org.eclipse.e4.ui.model.application.MAddon;
import org.eclipse.fx.core.di.Service;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import uk.co.saiman.experiment.ExperimentProcedure;
import uk.co.saiman.experiment.Procedure;
import uk.co.saiman.experiment.Store;
import uk.co.saiman.experiment.event.ExperimentEvent;
import uk.co.saiman.experiment.filesystem.FileSystemStore;
import uk.co.saiman.experiment.service.ProcedureService;
import uk.co.saiman.experiment.service.StorageService;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.msapex.experiment.workspace.event.AddExperimentEvent;
import uk.co.saiman.msapex.experiment.workspace.event.RemoveExperimentEvent;
import uk.co.saiman.msapex.experiment.workspace.event.WorkspaceEvent;
import uk.co.saiman.msapex.experiment.workspace.event.WorkspaceEventKind;
import uk.co.saiman.msapex.experiment.workspace.event.WorkspaceExperimentEvent;

/**
 * Addon for registering an experiment workspace in the root application
 * context. The addon also registers and manages an eclipse adapter over the
 * experiment types available in the workspace.
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentAddon {
  private static final String EXPERIMENTS = "experiments";

  @Inject
  private IEclipseContext context;
  @Inject
  @OSGiBundle
  private BundleContext bundleContext;
  @Inject
  private MAddon addon;
  @Inject
  private Log log;

  @Inject
  private IAdapterManager adapterManager;
  private ExperimentNodeAdapterFactory experimentNodeAdapterFactory;
  private ExperimentAdapterFactory experimentAdapterFactory;

  @Inject
  @Service
  private ProcedureService procedureService;
  @Inject
  @Service
  private StorageService storageService;
  @Inject
  @Service
  private volatile List<Procedure<?, ?>> experimentTypes;

  private Workspace workspace;
  private FileSystemStore workspaceStore;
  private ServiceRegistration<?> workspaceStoreRegsitration;

  @PostConstruct
  void initialize(@Named(INSTANCE_LOCATION) Location instanceLocation) throws URISyntaxException {
    try {
      Path rootPath = Paths.get(instanceLocation.getURL().toURI());
      registerWorkspaceStore(rootPath);
      registerWorkspace(rootPath);

      initializeAdapters();
      initializeEvents();
    } catch (Exception e) {
      e.printStackTrace();
      log.log(Level.ERROR, e);
    }
  }

  private void registerWorkspaceStore(Path rootPath) {
    workspaceStore = new FileSystemStore(rootPath);

    Dictionary<String, String> configuration = new Hashtable<>();
    configuration.put(COMPONENT_NAME, FILE_SYSTEM_STORE_ID);

    workspaceStoreRegsitration = bundleContext
        .registerService(
            new String[] { Store.class.getName(), FileSystemStore.class.getName() },
            workspaceStore,
            configuration);
  }

  private void registerWorkspace(Path rootPath) {
    workspace = new Workspace(rootPath, procedureService, storageService);
    context.set(Workspace.class, workspace);
    loadWorkspace();
  }

  @Inject
  @Optional
  public void update(AddExperimentEvent event) {
    saveWorkspace();
  }

  @Inject
  @Optional
  public void update(RemoveExperimentEvent event) {
    saveWorkspace();
  }

  private void loadWorkspace() {
    String experiments = addon.getPersistedState().get(EXPERIMENTS);
    if (experiments != null) {
      Stream
          .of(experiments.split(","))
          .map(String::strip)
          .filter(not(String::isBlank))
          .forEach(workspace::loadExperiment);
    }
  }

  private void saveWorkspace() {
    String experiments = workspace
        .getWorkspaceExperiments()
        .map(WorkspaceExperiment::name)
        .collect(joining(","));
    addon.getPersistedState().put(EXPERIMENTS, experiments);
  }

  @PreDestroy
  void destroy() {
    if (workspaceStoreRegsitration != null) {
      workspaceStoreRegsitration.unregister();
    }
    if (experimentAdapterFactory != null) {
      experimentAdapterFactory.unregister();
    }
    if (experimentNodeAdapterFactory != null) {
      experimentNodeAdapterFactory.unregister();
    }
  }

  private Stream<Procedure<?, ?>> getExperimentTypes() {
    return new ArrayList<>(experimentTypes).stream();
  }

  private void initializeAdapters() {
    experimentNodeAdapterFactory = new ExperimentNodeAdapterFactory(
        adapterManager,
        () -> concat(Stream.of(ExperimentProcedure.instance()), getExperimentTypes()));
    experimentAdapterFactory = new ExperimentAdapterFactory(
        adapterManager,
        experimentNodeAdapterFactory);
  }

  private void initializeEvents() {
    workspace.events().weakReference(this).observe(o -> {
      var workspaceEvent = o.message();
      o.owner().context.set(WorkspaceEvent.class, workspaceEvent);
      o.owner().context.remove(WorkspaceEvent.class);
      o.owner().context.set(workspaceEvent.kind().type().getName(), workspaceEvent);
      o.owner().context.remove(workspaceEvent.kind().type().getName());

      if (workspaceEvent.kind() == WorkspaceEventKind.EXPERIMENT) {
        var experimentEvent = ((WorkspaceExperimentEvent) workspaceEvent).experimentEvent();
        o.owner().context.set(ExperimentEvent.class, experimentEvent);
        o.owner().context.remove(ExperimentEvent.class);
        o.owner().context.set(experimentEvent.kind().type().getName(), experimentEvent);
        o.owner().context.remove(experimentEvent.kind().type().getName());
      }

      log.log(INFO, workspaceEvent.toString());
    });
  }
}
