/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
import static uk.co.saiman.experiment.environment.osgi.EnvironmentServiceConstants.RESOURCE_AVAILABILITY_EXCLUSIVE;
import static uk.co.saiman.experiment.environment.osgi.EnvironmentServiceConstants.RESOURCE_AVAILABILITY_SHARED;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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

import uk.co.saiman.experiment.dependency.Resource;
import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.environment.ResourceMissingException;
import uk.co.saiman.experiment.environment.ResourceUnavailableException;
import uk.co.saiman.experiment.environment.osgi.EnvironmentServiceConstants;
import uk.co.saiman.experiment.environment.osgi.impl.EnvironmentService.EnvironmentServiceConfiguration;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.osgi.ServiceIndex;
import uk.co.saiman.osgi.ServiceRecord;

@Designate(ocd = EnvironmentServiceConfiguration.class, factory = true)
@Component(
    configurationPid = EnvironmentService.CONFIGURATION_PID,
    configurationPolicy = OPTIONAL,
    enabled = true,
    immediate = true)
public class EnvironmentService {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Global Environment Service",
      description = "A service for management of global experiment environments for shared resources")
  public @interface EnvironmentServiceConfiguration {}

  static final String CONFIGURATION_PID = "uk.co.saiman.experiment.environment.global";

  private final Log log;
  private final ServiceIndex<Object, Class<?>, Object> resourceIndex;
  private ServiceRegistration<Environment> serviceRegistration;

  @Activate
  public EnvironmentService(
      EnvironmentServiceConfiguration configuration,
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
    unregisterService();
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

  private Map<Class<?>, Provider<?>> initializeProviders() {
    var values = new HashMap<Class<?>, Provider<?>>();
    resourceIndex
        .ids()
        .forEach(
            id -> resourceIndex
                .highestRankedRecord(id)
                .tryGet()
                .flatMap(service -> initializeProvider(id, service))
                .ifPresent(provider -> values.put(id, provider)));
    return values;
  }

  private Environment initializeEnvironment() {
    var providers = initializeProviders();
    var environment = new Environment() {
      @Override
      public boolean providesResource(Class<?> provision) {
        return providers.containsKey(provision);
      }

      private <T> Provider<T> getProvider(Class<T> provision) {
        if (!providesResource(provision)) {
          throw new ResourceMissingException(provision);
        }
        @SuppressWarnings("unchecked")
        var provider = (Provider<T>) providers.get(provision);
        if (provider == null) {
          throw new ResourceUnavailableException(provision, new NullPointerException());
        }
        return provider;
      }

      @Override
      public <T> Resource<T> provideResource(Class<T> provision) {
        return getProvider(provision).openResource();
      }

      @Override
      public <T> T provideSharedResource(Class<T> provision) {
        var provider = getProvider(provision);
        if (provider instanceof SharedProvider<?>) {
          return ((SharedProvider<T>) provider).getValue();
        }
        throw new ResourceUnavailableException(provision);
      }

      @Override
      public Stream<Class<?>> exclusiveResources() {
        return providers
            .values()
            .stream()
            .filter(ExclusiveProvider.class::isInstance)
            .map(Provider::type);
      }

      @Override
      public Stream<Class<?>> sharedResources() {
        return providers
            .values()
            .stream()
            .filter(SharedProvider.class::isInstance)
            .map(Provider::type);
      }

      @Override
      public Stream<Class<?>> resources() {
        return providers.keySet().stream();
      }
    };
    return environment;
  }

  private synchronized void registerService(
      BundleContext context,
      Dictionary<String, Object> environmentProperties) {
    unregisterService();

    var environment = initializeEnvironment();
    serviceRegistration = context
        .registerService(Environment.class, environment, environmentProperties);
  }

  private synchronized void unregisterService() {
    if (serviceRegistration != null) {
      serviceRegistration.unregister();
    }
  }

  private static <T> Optional<Provider<T>> initializeProvider(
      Class<? extends T> id,
      ServiceRecord<?, ?, T> record) {
    Object resourceAvailabilityAttribute = record
        .serviceReference()
        .getProperty(EnvironmentServiceConstants.RESOURCE_AVAILABILITY_ATTRIBUTE);

    String resourceAvailability = resourceAvailabilityAttribute != null
        ? resourceAvailabilityAttribute.toString()
        : RESOURCE_AVAILABILITY_SHARED;

    @SuppressWarnings("unchecked")
    Class<T> type = (Class<T>) id;

    switch (resourceAvailability.strip()) {
    case RESOURCE_AVAILABILITY_EXCLUSIVE:
      return Optional.of(new ExclusiveProvider<>(type, record.serviceObject()));
    case RESOURCE_AVAILABILITY_SHARED:
      return Optional.of(new SharedProvider<>(type, record.serviceObject()));
    default:
      return Optional.empty();
    }
  }

  private interface Provider<T> {
    Class<T> type();

    Resource<T> openResource();
  }

  private static class ExclusiveProvider<T> implements Provider<T> {
    private final Class<T> type;
    private final T value;
    private final Lock lock;
    private Condition condition;

    public ExclusiveProvider(Class<T> type, T value) {
      this.type = type;
      this.value = value;

      lock = new ReentrantLock();
    }

    @Override
    public Class<T> type() {
      return type;
    }

    @Override
    public Resource<T> openResource() {
      try {
        lock.lock();

        while (condition != null) {
          try {
            condition.await();
          } catch (InterruptedException e) {
            throw new ResourceUnavailableException(type, e);
          }
        }
        condition = lock.newCondition();

        return new Resource<T>() {
          @Override
          public Class<T> type() {
            return type;
          }

          @Override
          public T value() {
            return value;
          }

          @Override
          public void close() {
            try {
              lock.lock();
              condition.signalAll();
              condition = null;
            } finally {
              lock.unlock();
            }
          }
        };
      } finally {
        lock.unlock();
      }
    }
  }

  private static class SharedProvider<T> implements Provider<T> {
    private final Class<T> type;
    private final T value;

    public SharedProvider(Class<T> type, T value) {
      this.type = type;
      this.value = value;
    }

    @Override
    public Class<T> type() {
      return type;
    }

    @Override
    public Resource<T> openResource() {
      return new Resource<T>() {
        @Override
        public Class<T> type() {
          return type;
        }

        @Override
        public T value() {
          return value;
        }

        @Override
        public void close() {}
      };
    }

    public T getValue() {
      return value;
    }
  }
}
