package uk.co.saiman.experiment.conductor;

import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.dependency.ProductPath;
import uk.co.saiman.experiment.dependency.Result;
import uk.co.saiman.experiment.output.Output;
import uk.co.saiman.experiment.output.event.OutputEvent;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

class ConductorOutput implements Output {
  private final HotObservable<OutputEvent> events = new HotObservable<>();
  private Output successor;

  @Override
  public Stream<Result<?>> results() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <U extends ExperimentPath<U>> Stream<ProductPath<U, ? extends Result<?>>> resultPaths(
      ExperimentPath<U> path) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T extends Result<?>> T resolveResult(ProductPath<?, T> path) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Observable<OutputEvent> events() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Optional<Output> successiveOutput() {
    // TODO Auto-generated method stub
    return null;
  }

  public void nextEvent(OutputEvent outputEvent) {
    // TODO Auto-generated method stub

  }

}
