package uk.co.saiman.experiment.msapex.workspace;

import static java.util.function.Function.identity;

import java.util.stream.Stream;

import org.eclipse.e4.ui.model.application.MAddon;
import org.osgi.framework.BundleContext;

import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.service.ExecutorService;
import uk.co.saiman.osgi.ServiceIndex;
import uk.co.saiman.osgi.ServiceRecord;

public class EclipseExecutorService implements ExecutorService {
  public static final String EXECUTOR_SERVICE_ID = "uk.co.saiman.experiment.executors";

  private final ServiceIndex<ExecutorService, String, ExecutorService> executors;
  private final MAddon addon;

  public EclipseExecutorService(BundleContext bundleContext, MAddon addon) {
    this.executors = ServiceIndex.open(bundleContext, ExecutorService.class, identity());
    this.addon = addon;
  }

  public void close() {
    executors.close();
  }

  private java.util.Optional<ExecutorService> getBackingService() {
    return executors
        .highestRankedRecord(addon.getPersistedState().get(EXECUTOR_SERVICE_ID))
        .tryGet()
        .map(ServiceRecord::serviceObject);
  }

  @Override
  public Stream<Executor> executors() {
    return getBackingService().stream().flatMap(ExecutorService::executors);
  }

  @Override
  public Executor getExecutor(String id) {
    return getBackingService().orElseThrow().getExecutor(id);
  }

  @Override
  public String getId(Executor procedure) {
    return getBackingService().orElseThrow().getId(procedure);
  }
}
