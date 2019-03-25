package uk.co.saiman.experiment.schedule;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.path.Dependency;
import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Result;
import uk.co.saiman.observable.Observable;

public class Products {
  private final Scheduler scheduler;
  private final Schedule schedule;

  public Products(Scheduler scheduler) {
    this.scheduler = scheduler;
    this.schedule = scheduler.getSchedule().get();
  }

  public Schedule getSchedule() {
    return schedule;
  }

  public Procedure getProcedure() {
    return schedule.getProcedure();
  }

  public Stream<Product> products() {
    throw new UnsupportedOperationException();
  }

  public Optional<Result<?>> resolveResult(ProductPath<?> result) {
    throw new UnsupportedOperationException();
  }

  public <T extends Result<?>> T resolveResult(Dependency<T, ?> path) {
    throw new UnsupportedOperationException();
  }

  public <T extends Product> Observable<T> products(Dependency<T, ?> path) {
    throw new UnsupportedOperationException();
  }

  public synchronized void interrupt() {
    scheduler.interrupt(this);
  }

  public synchronized void clear() throws IOException {
    scheduler.clear(this);
  }
}
