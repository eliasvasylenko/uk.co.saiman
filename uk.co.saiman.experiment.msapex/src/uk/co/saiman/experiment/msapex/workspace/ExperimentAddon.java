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
package uk.co.saiman.experiment.msapex.workspace;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static org.eclipse.e4.ui.internal.workbench.E4Workbench.INSTANCE_LOCATION;
import static org.osgi.framework.Constants.SERVICE_PID;
import static uk.co.saiman.experiment.storage.filesystem.FileSystemStore.FILE_SYSTEM_STORE_ID;
import static uk.co.saiman.log.Log.Level.INFO;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.OSGiBundle;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MAddon;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.UIEvents.EventTags;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.fx.core.di.Service;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;

import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.event.ExperimentEvent;
import uk.co.saiman.experiment.event.RenameExperimentEvent;
import uk.co.saiman.experiment.graph.ExperimentId;
import uk.co.saiman.experiment.instruction.ExecutorService;
import uk.co.saiman.experiment.msapex.workspace.event.AddExperimentEvent;
import uk.co.saiman.experiment.msapex.workspace.event.RemoveExperimentEvent;
import uk.co.saiman.experiment.msapex.workspace.event.WorkspaceEvent;
import uk.co.saiman.experiment.msapex.workspace.event.WorkspaceEventKind;
import uk.co.saiman.experiment.msapex.workspace.event.WorkspaceExperimentEvent;
import uk.co.saiman.experiment.storage.StorageService;
import uk.co.saiman.experiment.storage.Store;
import uk.co.saiman.experiment.storage.filesystem.FileSystemStore;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.osgi.ServiceIndex;

/**
 * Addon for registering an experiment workspace in the root application
 * context. The addon also registers and manages an eclipse adapter over the
 * experiment types available in the workspace.
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentAddon {
  private static final String EXPERIMENTS = "experiments";

  public static final String WORKSPACE_STORE_ID = FILE_SYSTEM_STORE_ID + "~" + "ExperimentAddon";

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
  private ExperimentStepAdapterFactory experimentNodeAdapterFactory;
  private ExperimentAdapterFactory experimentAdapterFactory;

  @Inject
  @Service
  private ExecutorService conductorService;
  @Inject
  @Service
  private StorageService storageService;

  private Workspace workspace;
  private FileSystemStore workspaceStore;
  private ServiceRegistration<?> workspaceStoreRegsitration;

  private ServiceIndex<Environment, String, Environment> environments;

  @PostConstruct
  void initialize(@Named(INSTANCE_LOCATION) Location instanceLocation) throws URISyntaxException {
    try {
      Path rootPath = Paths.get(instanceLocation.getURL().toURI());
      registerWorkspaceStore(rootPath);
      registerWorkspace(rootPath);

      initializeAdapters();
      initializeEvents();

      environments = ServiceIndex.open(bundleContext, Environment.class);
    } catch (Exception e) {
      log.log(Level.ERROR, e);
      throw e;
    }
  }

  private void registerWorkspaceStore(Path rootPath) {
    workspaceStore = new FileSystemStore(rootPath);
    context.set(FileSystemStore.class, workspaceStore);

    Dictionary<String, String> configuration = new Hashtable<>();
    configuration.put(SERVICE_PID, WORKSPACE_STORE_ID);

    workspaceStoreRegsitration = bundleContext
        .registerService(
            new String[] { Store.class.getName(), FileSystemStore.class.getName() },
            workspaceStore,
            configuration);
  }

  private void registerWorkspace(Path rootPath) {
    workspace = new Workspace(rootPath, conductorService, storageService, log);
    context.set(Workspace.class, workspace);

    loadWorkspace();
  }

  @Inject
  @Optional
  public void update(RenameExperimentEvent event) {
    saveWorkspace();
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
    try {
      String experiments = addon.getPersistedState().get(EXPERIMENTS);
      if (experiments != null) {
        Stream
            .of(experiments.split(","))
            .map(String::strip)
            .filter(not(String::isBlank))
            .map(ExperimentId::fromName)
            .forEach(workspace::loadExperiment);
      }
    } catch (Exception e) {
      log.log(Level.ERROR, "Problem loading workspace", e);
      throw e;
    }
  }

  private void saveWorkspace() {
    try {
      String experiments = workspace
          .getWorkspaceExperiments()
          .map(WorkspaceExperiment::id)
          .map(ExperimentId::name)
          .collect(joining(","));
      addon.getPersistedState().put(EXPERIMENTS, experiments);
    } catch (Exception e) {
      log.log(Level.ERROR, "Problem saving workspace", e);
      throw e;
    }
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
    if (environments != null) {
      environments.close();
    }
  }

  private void initializeAdapters() {
    experimentNodeAdapterFactory = new ExperimentStepAdapterFactory(adapterManager);
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

  @Inject
  @Optional
  public void partActivated(@Active MPart part, EModelService modelService) {
    /*
     * When a new perspective is activated, set the perspective's environment on the
     * root window.
     */
    var perspective = modelService.getPerspectiveFor(part);
    var window = modelService.getTopLevelWindowFor(perspective);
    window.getContext().set(Environment.class, perspective.getContext().get(Environment.class));
  }

  @Inject
  @Optional
  public void perspectiveCreated(@UIEventTopic(UIEvents.UIElement.TOPIC_TOBERENDERED) Event event) {
    /*
     * When a new perspective is created, associate it with an experiment
     * environment.
     * 
     * TODO We will put an experiment environment service id property in the
     * persisted state of each perspective of which to select an environment.
     * 
     * TODO it might make sense to promote the "analysis" perspective from the root
     * msapex project to the experiment project, since doing otherwise creates a
     * hidden coupling between the two projects (by way of this property). Besides,
     * analysis is really a type of experiment anyway so it only makes sense to
     * define it here.
     */

    Object element = event.getProperty(EventTags.ELEMENT);
    if (!(element instanceof MPerspective)) {
      return;
    }

    MPerspective perspective = (MPerspective) element;

    System.out.println("Perspective created: " + perspective.getLabel());

    throw new UnsupportedOperationException();
  }
}
