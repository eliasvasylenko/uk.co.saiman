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
 * This file is part of uk.co.saiman.experiment.msapex.
 *
 * uk.co.saiman.experiment.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.msapex.workspace;

import static java.util.function.Function.identity;
import static uk.co.saiman.experiment.storage.filesystem.FileSystemStore.FILE_SYSTEM_STORE_ID;
import static uk.co.saiman.state.Accessor.stringAccessor;

import java.util.stream.Stream;

import org.eclipse.e4.ui.model.application.MAddon;
import org.osgi.framework.BundleContext;

import uk.co.saiman.experiment.osgi.ExperimentServiceConstants;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.experiment.storage.Store;
import uk.co.saiman.experiment.storage.filesystem.SharedFileSystemStore;
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
  private final SharedFileSystemStore workspaceStore;

  public EclipseStorageService(
      BundleContext bundleContext,
      MAddon addon,
      SharedFileSystemStore workspaceStore) {
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
