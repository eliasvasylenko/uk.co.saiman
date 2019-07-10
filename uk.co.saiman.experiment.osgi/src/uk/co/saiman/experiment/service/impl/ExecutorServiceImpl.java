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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.service.impl;

import static java.util.function.Function.identity;
import static org.osgi.framework.FrameworkUtil.createFilter;
import static org.osgi.service.component.annotations.ConfigurationPolicy.OPTIONAL;

import java.util.Optional;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.instruction.Executor;
import uk.co.saiman.experiment.instruction.ExecutorService;
import uk.co.saiman.experiment.service.ExperimentServiceConstants;
import uk.co.saiman.experiment.service.impl.ExecutorServiceImpl.ExecutorServiceConfiguration;
import uk.co.saiman.osgi.ServiceIndex;
import uk.co.saiman.osgi.ServiceRecord;

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
  @ObjectClassDefinition(name = "Executor Service", description = "A service over a set of executors which may interact")
  public @interface ExecutorServiceConfiguration {
    String executorFilter() default "";
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.experiment.executors";

  private final ServiceIndex<?, String, Executor<?>> executors;

  @Activate
  public ExecutorServiceImpl(ExecutorServiceConfiguration configuration, BundleContext context)
      throws InvalidSyntaxException {
    String filterString = "(" + Constants.OBJECTCLASS + "=" + Executor.class.getName() + ")";
    if (!configuration.executorFilter().isBlank()) {
      filterString = "(&" + filterString + configuration.executorFilter() + ")";
    }
    executors = ServiceIndex
        .open(
            context,
            createFilter(filterString),
            identity(),
            ExecutorServiceImpl::executorIndexer);
  }

  private static Optional<String> executorIndexer(
      Executor<?> object,
      ServiceReference<Executor<?>> serviceReference) {
    return Optional
        .ofNullable((String) serviceReference.getProperty(ExperimentServiceConstants.EXECUTOR_ID));
  }

  @Override
  public Stream<Executor<?>> executors() {
    return executors.objects();
  }

  @Override
  public Executor<?> getExecutor(String id) {
    return executors.get(id).get().serviceObject();
  }

  @Override
  public String getId(Executor<?> executor) {
    return executors.findRecord(executor).flatMap(ServiceRecord::id).get();
  }
}
