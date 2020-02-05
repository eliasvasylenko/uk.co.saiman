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
 * This file is part of uk.co.saiman.experiment.osgi.
 *
 * uk.co.saiman.experiment.osgi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.osgi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.environment.osgi.impl;

import static org.osgi.framework.Constants.OBJECTCLASS;
import static org.osgi.service.component.annotations.ConfigurationPolicy.OPTIONAL;
import static uk.co.saiman.experiment.environment.osgi.EnvironmentServiceConstants.ENVIRONMENT_FILTER_ATTRIBUTE;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.environment.ResourceMissingException;
import uk.co.saiman.experiment.environment.ResourceUnavailableException;
import uk.co.saiman.experiment.environment.osgi.impl.GlobalEnvironmentService.GlobalEnvironmentServiceConfiguration;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.osgi.ServiceIndex;
import uk.co.saiman.osgi.ServiceRecord;

@Designate(ocd = GlobalEnvironmentServiceConfiguration.class, factory = true)
@Component(
    configurationPid = GlobalEnvironmentService.CONFIGURATION_PID,
    configurationPolicy = OPTIONAL,
    enabled = true,
    immediate = true)
public class GlobalEnvironmentService {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Global Environment Service",
      description = "A service for management of global experiment environments for shared resources")
  public @interface GlobalEnvironmentServiceConfiguration {}

  static final String CONFIGURATION_PID = "uk.co.saiman.experiment.environment.global";

  private final Log log;
  private final ServiceIndex<Object, Class<?>, Object> resourceIndex;
  private ServiceRegistration<GlobalEnvironment> serviceRegistration;

  @Activate
  public GlobalEnvironmentService(
      GlobalEnvironmentServiceConfiguration configuration,
      BundleContext context,
      @Reference Log log,
      Map<String, Object> environmentProperties) throws InvalidSyntaxException {
    this.log = log;

    Dictionary<String, Object> dictionary = new Hashtable<>(environmentProperties);
    log.log(Level.INFO, "Global environment dictionary: " + dictionary);

    var resourceFilter = "(" + ENVIRONMENT_FILTER_ATTRIBUTE + "=*)";

    resourceIndex = ServiceIndex
        .open(
            context,
            FrameworkUtil.createFilter(resourceFilter),
            Function.identity(),
            (a, b) -> resourceIndexer(dictionary, b));

    resourceIndex.events().observe(o -> registerService(context, dictionary));
    registerService(context, dictionary);
    log.log(Level.INFO, "Registered global environment: " + this);
  }

  @Deactivate
  public void deactivate() {
    resourceIndex.close();
  }

  private Stream<Class<?>> resourceIndexer(
      Dictionary<String, Object> environmentProperties,
      ServiceReference<?> resourceService) {

    String filterString = resourceService.getProperty(ENVIRONMENT_FILTER_ATTRIBUTE).toString();

    if (!"*".equals(filterString)) {
      try {
        var filter = FrameworkUtil.createFilter(filterString);

        if (!filter.match(environmentProperties)) {
          return Stream.empty();
        }
      } catch (Exception e) {
        log.log(Level.ERROR, e);
        return Stream.empty();
      }
    }

    var classNames = (String[]) resourceService.getProperty(OBJECTCLASS);
    var classLoader = resourceService.getBundle().adapt(BundleWiring.class).getClassLoader();

    return Stream.of(classNames).flatMap(className -> {
      try {
        log.log(Level.INFO, "Loading class for environment: " + className + " @ " + this);
        return Stream.of(classLoader.loadClass(className));
      } catch (ClassNotFoundException e) {
        log.log(Level.ERROR, e);
        return Stream.empty();
      }
    });
  }

  private synchronized void registerService(
      BundleContext context,
      Dictionary<String, Object> environmentProperties) {
    if (serviceRegistration != null) {
      serviceRegistration.unregister();
    }

    var values = new HashMap<Class<?>, Object>();
    resourceIndex
        .ids()
        .forEach(
            id -> resourceIndex
                .highestRankedRecord(id)
                .tryGet()
                .map(ServiceRecord::serviceObject)
                .ifPresent(object -> values.put(id, object)));

    var globalEnvironment = new GlobalEnvironment() {
      @Override
      public Stream<Class<?>> providedValues() {
        return values.keySet().stream();
      }

      @Override
      public boolean providesValue(Class<?> provision) {
        return values.containsKey(provision);
      }

      @Override
      public <T> T provideValue(Class<T> provision) {
        if (!providesValue(provision)) {
          throw new ResourceMissingException(provision);
        }
        @SuppressWarnings("unchecked")
        var value = (T) values.get(provision);
        if (value == null) {
          throw new ResourceUnavailableException(provision, new NullPointerException());
        }
        return value;
      }
    };
    serviceRegistration = context
        .registerService(GlobalEnvironment.class, globalEnvironment, environmentProperties);
  }
}
