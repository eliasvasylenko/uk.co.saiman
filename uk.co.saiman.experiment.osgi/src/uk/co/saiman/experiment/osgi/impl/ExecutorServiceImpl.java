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
package uk.co.saiman.experiment.osgi.impl;

import static org.osgi.service.component.annotations.ConfigurationPolicy.OPTIONAL;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.service.ExecutorService;
import uk.co.saiman.experiment.osgi.ExperimentServiceConstants;
import uk.co.saiman.experiment.osgi.impl.ExecutorServiceImpl.ExecutorServiceConfiguration;

/**
 * An OSGi-based implementation of the {@link ExecutorService experiment
 * executor service}.
 * <p>
 * The component collects executor services in the framework and indexes them
 * based on the {@link ExperimentServiceConstants#EXECUTOR_ID executor id}
 * service property.
 * <p>
 * A default component instance is configured if no configuration is provided,
 * which picks up all executor services in the framework.
 * <p>
 * It is recommended that each configuration for an instrument configures its
 * own executor service. By convention they should specify a filter such as
 * <code>(|(uk.co.saiman.experiment.executors=my.instrument.id)(!(uk.co.saiman.experiment.executors=*)))</code>.
 * Then each executor which is intended to operate only on that instrument
 * should publish that service property, while any "universal" executor (e.g.
 * data processors) should omit it. If every configuration follows this
 * convention, this precludes executors which are intended to operate over a
 * different instrument from being collected. This ensures that different
 * instances of the same pieces of hardware don't interact, e.g. if multiple
 * instruments of the same type are attached to a framework.
 * 
 * @author Elias N Vasylenko
 */
@Designate(ocd = ExecutorServiceConfiguration.class, factory = true)
@Component(configurationPid = ExecutorServiceImpl.CONFIGURATION_PID, configurationPolicy = OPTIONAL)
public class ExecutorServiceImpl implements ExecutorService {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Executor Service",
      description = "A service over a set of executors which may interact")
  public @interface ExecutorServiceConfiguration {}

  static final String CONFIGURATION_PID = "uk.co.saiman.experiment.executors";

  private final BundleContext context;
  private final Set<ServiceReference<?>> references;

  private final Map<String, Executor> executors;
  private final Map<Executor, String> ids;

  @Activate
  public ExecutorServiceImpl(
      BundleContext context,
      @Reference(
          name = "executors",
          policyOption = GREEDY) List<ServiceReference<Executor>> executors) {
    this.context = context;
    this.references = new HashSet<>();

    this.executors = new HashMap<>();
    this.ids = new HashMap<>();

    for (var executorReference : executors) {
      var executor = context.getService(executorReference);
      references.add(executorReference);
      executorIndexer(executorReference).ifPresent(id -> {
        this.executors.put(id, executor);
        this.ids.put(executor, id);
      });
    }
  }

  @Deactivate
  public void deactivate() {
    references.forEach(context::ungetService);
  }

  private static Optional<String> executorIndexer(ServiceReference<Executor> serviceReference) {
    return Optional
        .ofNullable((String) serviceReference.getProperty(ExperimentServiceConstants.EXECUTOR_ID));
  }

  @Override
  public Stream<Executor> executors() {
    return executors.values().stream();
  }

  @Override
  public Executor getExecutor(String id) {
    return executors.get(id);
  }

  @Override
  public String getId(Executor executor) {
    return ids.get(executor);
  }
}
