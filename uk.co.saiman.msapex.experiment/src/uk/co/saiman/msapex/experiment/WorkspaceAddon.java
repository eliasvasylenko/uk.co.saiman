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

import static java.util.stream.Stream.concat;
import static org.eclipse.e4.ui.internal.workbench.E4Workbench.INSTANCE_LOCATION;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.fx.core.di.Service;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.service.cm.ConfigurationAdmin;

import uk.co.saiman.data.Data;
import uk.co.saiman.data.resource.PathLocation;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentEvent;
import uk.co.saiman.experiment.ExperimentEventKind;
import uk.co.saiman.experiment.ExperimentProcedure;
import uk.co.saiman.experiment.FullProcedure;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.experiment.state.JsonStateMapFormat;
import uk.co.saiman.experiment.state.StateMap;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.msapex.experiment.i18n.ExperimentProperties;

/**
 * Addon for registering an experiment workspace in the root application
 * context. The addon also registers and manages an eclipse adapter over the
 * experiment types available in the workspace.
 * 
 * @author Elias N Vasylenko
 */
public class WorkspaceAddon {
  @Inject
  IEclipseContext context;

  @Inject
  IAdapterManager adapterManager;
  private ExperimentNodeAdapterFactory experimentNodeAdapterFactory;
  private ExperimentAdapterFactory experimentAdapterFactory;

  @Inject
  Log log;
  @Inject
  @Localize
  ExperimentProperties text;

  @Inject
  @Service
  ConfigurationAdmin configurationAdmin;

  @Inject
  @Service
  volatile List<ExperimentProcedure<?, ?>> experimentTypes;

  @Inject
  @Named(INSTANCE_LOCATION)
  Location instanceLocation;
  private uk.co.saiman.data.resource.Location workspaceRoot;

  private final Workspace workspace = new Workspace();
  private final Map<Experiment, Data<StateMap>> experiments = new HashMap<>();

  @PostConstruct
  void initialize() throws URISyntaxException {
    workspaceRoot = new PathLocation(Paths.get(instanceLocation.getURL().toURI()));

    context.set(Workspace.class, workspace);

    try {
      initializeAdapters();
    } catch (Exception e) {
      log.log(Level.ERROR, e);
      e.printStackTrace();
    }

    /*
     * Inject events
     */
    workspace.events().weakReference(this).observe(o -> {
      o.owner().context.set(ExperimentEvent.class, o.message());
      o.owner().context.remove(ExperimentEvent.class);
      o.owner().context.set(o.message().kind().type().getName(), o.message());
      o.owner().context.remove(o.message().kind().type().getName());
    });
  }

  // @Inject
  // @Optional
  public void saveExperiment(ExperimentEvent event) {
    try {
      event.node().getExperiment().ifPresent(experiment -> {
        Data<StateMap> data = experiments
            .computeIfAbsent(
                experiment,
                e -> Data.locate(workspaceRoot, experiment.getId(), new JsonStateMapFormat()));

        if (event.kind() == ExperimentEventKind.RENAME) {
          data.relocate(workspaceRoot, event.node().getId());
        }

        data.set(experiment.getStateMap());
        data.save();
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Stream<ExperimentProcedure<?, ?>> getExperimentTypes() {
    return new ArrayList<>(experimentTypes).stream();
  }

  private void initializeAdapters() {
    experimentNodeAdapterFactory = new ExperimentNodeAdapterFactory(
        adapterManager,
        () -> concat(Stream.of(FullProcedure.instance()), getExperimentTypes()));
    experimentAdapterFactory = new ExperimentAdapterFactory(
        adapterManager,
        experimentNodeAdapterFactory);
  }

  @PreDestroy
  void destroy() {
    if (experimentAdapterFactory != null)
      experimentAdapterFactory.unregister();
    if (experimentNodeAdapterFactory != null)
      experimentNodeAdapterFactory.unregister();
  }
}
