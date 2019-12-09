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
package uk.co.saiman.experiment.msapex.environment;

import static java.util.function.Function.identity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.extensions.OSGiBundle;
import org.eclipse.e4.core.di.extensions.Service;
import org.osgi.framework.BundleContext;

import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.environment.service.LocalEnvironmentService;
import uk.co.saiman.osgi.ServiceIndex;

public class ResourceService {
  @Inject
  private IEclipseContext context;

  @Inject
  @OSGiBundle
  private BundleContext bundleContext;

  @Inject
  @Service
  private LocalEnvironmentService localEnvironmentService;

  private ServiceIndex<GlobalEnvironment, String, GlobalEnvironment> sharedResources;
  private final Map<Class<?>, ResourcePresenter<?>> resourcePresenters = new HashMap<>();

  @PostConstruct
  void initialize() {
    sharedResources = ServiceIndex
        .open(
            bundleContext,
            GlobalEnvironment.class,
            identity(),
            (e, s) -> Optional
                .ofNullable(s.getProperty("target-perspective-id").toString())
                .stream());
  }

  /*
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * TODO shared resources are identified directly from their service
   * registrations via SharedEnvironmentService. The resources are then correlated
   * with the associated Provision objects and a SharedEnvironment is created from
   * the service, and injected into the context of the appropriate perspective.
   * The environment is recreated and reinjected each time a provision is
   * registered or the service changes.
   * 
   * 
   * Local resources are created from shared ones according to some function. We
   * don't need to know which local resources will be available until we try to
   * acquire them and either succeed or fail.
   * 
   * 
   * 
   * 
   * TODO open question as to whether we register ResourcePresentations
   * programmatically via this service injected into an addon, or simply by
   * registering a ResourcePresentation service in the framework. The latter
   * option probably is better and more closely aligns with how DevicePresentation
   * is likely to work.
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   */
}
