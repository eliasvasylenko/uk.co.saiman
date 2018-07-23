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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.fx.core.di.Service;
import org.osgi.service.cm.ConfigurationAdmin;

import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.Workspace;
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

  @Inject
  @Service
  private ConfigurationAdmin configurationAdmin;

  @Inject
  @Service
  private Workspace workspace;
  @Inject
  @Service
  private List<ExperimentType<?, ?>> experimentTypes;

  @PostConstruct
  void initialize() {
    context.set(Workspace.class, workspace);

    try {
      initializeAdapters();
    } catch (Exception e) {
      log.log(Level.ERROR, e);
      e.printStackTrace();
    }
  }

  private Stream<ExperimentType<?, ?>> getExperimentTypes() {
    return Stream
        .concat(
            Stream.of(workspace.getExperimentRootType()),
            new ArrayList<>(experimentTypes).stream());
  }

  private void initializeAdapters() {
    experimentNodeAdapterFactory = new ExperimentNodeAdapterFactory(
        adapterManager,
        this::getExperimentTypes);
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
