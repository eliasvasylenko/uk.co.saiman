package uk.co.saiman.experiment.schedule;

import java.io.IOException;
import java.util.stream.Stream;

import uk.co.saiman.experiment.path.Dependency;
import uk.co.saiman.experiment.path.ProductIndex;
import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.schedule.event.SchedulingEvent;
import uk.co.saiman.observable.Observable;

public class Products implements ProductIndex {
  private final Scheduler scheduler;
  private final Schedule schedule;

  public Products(Scheduler scheduler) {
    this.scheduler = scheduler;
    this.schedule = scheduler.getSchedule().get();
  }

  public Schedule getSchedule() {
    return schedule;
  }

  public Stream<Product> products() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Product resolve(ProductPath result) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T extends Product> T resolve(Dependency<T> path) {
    throw new UnsupportedOperationException();
  }

  /**
   * Observe updates to the result value. Update events are sent with
   * invalidate/lazy-revalidate semantics. This means that once an update has been
   * sent, further updates are withheld until the previous change has actually
   * been {@link #value() observed}. This means that consumers can deal with
   * changes in their own time, and publishers may have the option to skip
   * processing and memory allocation for updates which are not consumed.
   * 
   * @return an observable over update events
   */
  public <T extends Product> Observable<T> products(Dependency<T> path) {
    throw new UnsupportedOperationException();
  }

  public synchronized void interrupt() {
    scheduler.interrupt(this);
  }

  public synchronized void clear() throws IOException {
    scheduler.clear(this);
  }

  public Observable<SchedulingEvent> events() {
    return schedule.events();
  }
}
