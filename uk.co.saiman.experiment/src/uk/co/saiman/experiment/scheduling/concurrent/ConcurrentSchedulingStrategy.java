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
