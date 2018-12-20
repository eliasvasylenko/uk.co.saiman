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
package uk.co.saiman.experiment.scheduling.concurrent;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.scheduling.Scheduler;
import uk.co.saiman.experiment.scheduling.SchedulingContext;
import uk.co.saiman.experiment.scheduling.SchedulingStrategy;
import uk.co.saiman.experiment.scheduling.concurrent.ConcurrentSchedulingStrategy.ConcurrentSchedulingStrategyConfiguration;

@Designate(ocd = ConcurrentSchedulingStrategyConfiguration.class, factory = true)
@Component(name = ConcurrentSchedulingStrategy.CONCURRENT_SCHEDULING_STRATEGY_ID, configurationPolicy = REQUIRE)
public class ConcurrentSchedulingStrategy implements SchedulingStrategy {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "Concurrent Scheduling Strategy Configuration", description = "The configuration for a basic concurrent scheduler for experiment processing")
  public @interface ConcurrentSchedulingStrategyConfiguration {
    @AttributeDefinition(name = "Maximum Concurrency", description = "The maximum number of concurrently executing threads")
    int maximumConcurrency();
  }

  public static final String CONCURRENT_SCHEDULING_STRATEGY_ID = "uk.co.saiman.experiment.scheduling.concurrent";

  private final int maximumConcurrency;

  public ConcurrentSchedulingStrategy(ConcurrentSchedulingStrategyConfiguration configuration) {
    this(configuration.maximumConcurrency());
  }

  public ConcurrentSchedulingStrategy(int maximumConcurrency) {
    this.maximumConcurrency = maximumConcurrency;
  }

  @Override
  public Scheduler provideScheduler(SchedulingContext scheduleCommencer) {
    return new ConcurrentScheduler(scheduleCommencer, maximumConcurrency);
  }
}
