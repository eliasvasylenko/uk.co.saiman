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

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static org.osgi.service.component.annotations.ConfigurationPolicy.OPTIONAL;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.dependency.Resource;
import uk.co.saiman.experiment.dependency.ResourceClosingException;
import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.environment.LocalEnvironment;
import uk.co.saiman.experiment.environment.ResourceMissingException;
import uk.co.saiman.experiment.environment.ResourceUnavailableException;
import uk.co.saiman.experiment.environment.osgi.ExclusiveResource;
import uk.co.saiman.experiment.environment.osgi.ExclusiveResourceProvider;
import uk.co.saiman.experiment.environment.osgi.impl.LocalEnvironmentServiceImpl.LocalEnvironmentServiceConfiguration;
import uk.co.saiman.experiment.environment.service.LocalEnvironmentService;

@Designate(ocd = LocalEnvironmentServiceConfiguration.class, factory = true)
@Component(
    configurationPid = LocalEnvironmentServiceImpl.CONFIGURATION_PID,
    configurationPolicy = OPTIONAL)
public class LocalEnvironmentServiceImpl implements LocalEnvironmentService {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Local Environment Service",
      description = "A service for management of local experiment environments for exclusive resources")
  public @interface LocalEnvironmentServiceConfiguration {}

  static final String CONFIGURATION_PID = "uk.co.saiman.experiment.environment.local";

  private final Map<Class<?>, ExclusiveResourceProvider<?>> executors;
  private final Map<ExclusiveResourceProvider<?>, Class<?>> ids;

  @Activate
  public LocalEnvironmentServiceImpl(
      BundleContext context,
      @Reference(
          name = "exclusiveResourceProviders",
          policyOption = GREEDY) List<ExclusiveResourceProvider<?>> exclusiveResourceProviders) {
    this.executors = new HashMap<>();
    this.ids = new HashMap<>();

    for (var resourceProvider : exclusiveResourceProviders) {
      serviceProviderIndexer(resourceProvider).forEach(id -> {
        this.executors.put(id, resourceProvider);
        this.ids.put(resourceProvider, id);
      });
    }
  }

  private Stream<Class<?>> serviceProviderIndexer(ExclusiveResourceProvider<?> provider) {
    return Stream.of(provider.getProvision());
  }

  private <T> ExclusiveResource<T> getExclusiveResource(
      Class<T> type,
      GlobalEnvironment globalEnvironment,
      long timeout,
      TimeUnit unit) {
    if (executors.containsKey(type)) {
      try {
        @SuppressWarnings("unchecked")
        var exclusiveResource = ((ExclusiveResourceProvider<T>) executors.get(type))
            .deriveResource(globalEnvironment, timeout, unit);
        return exclusiveResource;
      } catch (Exception e) {
        throw new ResourceUnavailableException(type, e);
      }
    } else {
      throw new ResourceMissingException(type);
    }
  }

  @Override
  public LocalEnvironment openLocalEnvironment(
      GlobalEnvironment globalEnvironment,
      Collection<? extends java.lang.Class<?>> resources,
      long timeout,
      TimeUnit unit) {

    Map<Class<?>, ExclusiveResource<?>> exclusiveResources = resources
        .stream()
        .filter(not(globalEnvironment::providesValue))
        .<ExclusiveResource<?>>map(r -> getExclusiveResource(r, globalEnvironment, timeout, unit))
        .collect(Collectors.toMap(ExclusiveResource::getType, Function.identity()));

    return new LocalEnvironment() {
      private final ReentrantLock lock = new ReentrantLock();

      private final Map<Class<?>, Condition> heldExclusiveResources = new HashMap<>();

      @Override
      public void close() {
        try {
          lock.lock();

          heldExclusiveResources.values().forEach(Condition::signalAll);
          exclusiveResources.entrySet().stream().flatMap(c -> {
            try {
              c.getValue().close();
              return Stream.empty();
            } catch (Exception e) {
              return Stream.of(new ResourceClosingException(c.getKey(), e));
            }
          }).reduce((e1, e2) -> {
            e1.addSuppressed(e2);
            return e1;
          }).ifPresent(e -> {
            throw e;
          });

        } finally {
          heldExclusiveResources.clear();
          exclusiveResources.clear();

          lock.unlock();
        }
      }

      @Override
      public Stream<Class<?>> providedResources() {
        try {
          lock.lock();

          return Stream
              .of(globalEnvironment.providedValues(), exclusiveResources.keySet().stream())
              .flatMap(s -> s)
              .collect(toList())
              .stream();
        } finally {
          lock.unlock();
        }
      }

      @Override
      public <T> Resource<T> provideResource(Class<T> provision) {
        try {
          lock.lock();

          if (globalEnvironment.providesValue(provision)) {
            return Resource
                .over(provision, globalEnvironment.provideValue(provision), t -> () -> {});
          }

          while (heldExclusiveResources.containsKey(provision)) {
            heldExclusiveResources.get(provision).await();
          }

          @SuppressWarnings("unchecked")
          var exclusiveResource = (ExclusiveResource<T>) exclusiveResources.get(provision);
          if (exclusiveResource == null) {
            throw new ResourceMissingException(provision);
          }

          Condition condition = lock.newCondition();
          heldExclusiveResources.put(provision, condition);
          var resource = Resource.over(provision, exclusiveResource.getValue(), e -> () -> {
            try {
              lock.lock();
              var c = heldExclusiveResources.remove(provision);
              if (c != null) {
                c.signalAll();
              }

            } finally {
              lock.unlock();
            }
          });
          return resource;

        } catch (InterruptedException e) {
          throw new ResourceUnavailableException(provision, e);

        } finally {
          lock.unlock();
        }
      }

      @Override
      public GlobalEnvironment getGlobalEnvironment() {
        return globalEnvironment;
      }
    };
  }

  @Override
  public LocalEnvironment openLocalEnvironment(
      LocalEnvironment localEnvironment,
      Collection<? extends Class<?>> resources,
      long timeout,
      TimeUnit unit) {
    /*
     * TODO inherit the resources already available in the given local environment,
     * acquire the rest
     */
    throw new UnsupportedOperationException();
  }
}
