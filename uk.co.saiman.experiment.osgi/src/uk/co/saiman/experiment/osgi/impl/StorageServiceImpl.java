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
package uk.co.saiman.experiment.osgi.impl;

import static org.osgi.service.component.annotations.ConfigurationPolicy.OPTIONAL;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;
import static uk.co.saiman.state.Accessor.stringAccessor;

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

import uk.co.saiman.experiment.osgi.ExperimentServiceConstants;
import uk.co.saiman.experiment.osgi.impl.StorageServiceImpl.StorageServiceConfiguration;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.experiment.storage.Store;
import uk.co.saiman.experiment.storage.service.StorageService;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.StateMap;

@Designate(ocd = StorageServiceConfiguration.class, factory = true)
@Component(configurationPid = StorageServiceImpl.CONFIGURATION_PID, configurationPolicy = OPTIONAL)
public class StorageServiceImpl implements StorageService {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Storage Service",
      description = "A service over a set of available experiment storage implementations")
  public @interface StorageServiceConfiguration {}

  static final String CONFIGURATION_PID = "uk.co.saiman.experiment.storage";

  private static final MapIndex<String> STORE_ID = new MapIndex<>(
      ExperimentServiceConstants.STORE_ID,
      stringAccessor());

  private final BundleContext context;
  private final Set<ServiceReference<?>> references;

  private final Map<String, Store<?>> stores;
  private final Map<Store<?>, String> ids;

  @Activate
  public StorageServiceImpl(
      BundleContext context,
      @Reference(name = "stores", policyOption = GREEDY) List<ServiceReference<Store<?>>> stores) {
    this.context = context;
    this.references = new HashSet<>();

    this.stores = new HashMap<>();
    this.ids = new HashMap<>();

    for (var storeReference : stores) {
      var store = context.getService(storeReference);
      references.add(storeReference);
      storeIndexer(storeReference).ifPresent(id -> {
        this.stores.put(id, store);
        this.ids.put(store, id);
      });
    }
  }

  @Deactivate
  public void deactivate() {
    references.forEach(context::ungetService);
  }

  private static Optional<String> storeIndexer(ServiceReference<Store<?>> serviceReference) {
    return Optional
        .ofNullable((String) serviceReference.getProperty(ExperimentServiceConstants.STORE_ID));
  }

  @Override
  public Stream<Store<?>> stores() {
    return stores.values().stream();
  }

  @Override
  public StorageConfiguration<?> configureStorage(StateMap persistedState) {
    return new StorageConfiguration<>(stores.get(persistedState.get(STORE_ID)), persistedState);
  }

  @Override
  public <T> StateMap deconfigureStorage(StorageConfiguration<T> processor) {
    return processor.deconfigure().with(STORE_ID, ids.get(processor.store()));
  }
}
