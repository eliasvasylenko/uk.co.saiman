package uk.co.saiman.experiment.msapex.workspace;

import static java.util.function.Function.identity;
import static uk.co.saiman.experiment.storage.filesystem.FileSystemStore.FILE_SYSTEM_STORE_ID;
import static uk.co.saiman.state.Accessor.stringAccessor;

import java.util.stream.Stream;

import org.eclipse.e4.ui.model.application.MAddon;
import org.osgi.framework.BundleContext;

import uk.co.saiman.experiment.service.ExperimentServiceConstants;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.experiment.storage.Store;
import uk.co.saiman.experiment.storage.filesystem.FileSystemStore;
import uk.co.saiman.experiment.storage.service.StorageService;
import uk.co.saiman.osgi.ServiceIndex;
import uk.co.saiman.osgi.ServiceRecord;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.StateMap;

public class EclipseStorageService implements StorageService {
  private static final MapIndex<String> STORE_ID = new MapIndex<>(
      ExperimentServiceConstants.STORE_ID,
      stringAccessor());

  public static final String STORAGE_SERVICE_ID = "uk.co.saiman.experiment.storage";

  public static final String WORKSPACE_STORE_ID = FILE_SYSTEM_STORE_ID + "~" + "ExperimentAddon";

  private final ServiceIndex<StorageService, String, StorageService> storage;
  private final MAddon addon;
  private final FileSystemStore workspaceStore;

  public EclipseStorageService(
      BundleContext bundleContext,
      MAddon addon,
      FileSystemStore workspaceStore) {
    this.storage = ServiceIndex.open(bundleContext, StorageService.class, identity());
    this.addon = addon;
    this.workspaceStore = workspaceStore;
  }

  public void close() {
    storage.close();
  }

  private java.util.Optional<StorageService> getBackingService() {
    return storage
        .highestRankedRecord(addon.getPersistedState().get(STORAGE_SERVICE_ID))
        .tryGet()
        .map(ServiceRecord::serviceObject);
  }

  @Override
  public Stream<Store<?>> stores() {
    return getBackingService().stream().flatMap(StorageService::stores);
  }

  @Override
  public <T> StateMap deconfigureStorage(StorageConfiguration<T> processor) {
    if (processor.store() == workspaceStore) {
      return processor.deconfigure().with(STORE_ID, WORKSPACE_STORE_ID);

    } else {
      return getBackingService().orElseThrow().deconfigureStorage(processor);
    }
  }

  @Override
  public StorageConfiguration<?> configureStorage(StateMap persistedState) {
    if (persistedState.get(STORE_ID).equals(WORKSPACE_STORE_ID)) {
      return new StorageConfiguration<>(workspaceStore, persistedState);

    } else {
      return getBackingService().orElseThrow().configureStorage(persistedState);
    }
  }
}
