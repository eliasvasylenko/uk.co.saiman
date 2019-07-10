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

import static org.osgi.framework.FrameworkUtil.createFilter;
import static org.osgi.service.component.annotations.ConfigurationPolicy.OPTIONAL;
import static uk.co.saiman.state.Accessor.stringAccessor;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.service.ExperimentServiceConstants;
import uk.co.saiman.experiment.service.impl.StorageServiceImpl.StorageServiceConfiguration;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.experiment.storage.StorageService;
import uk.co.saiman.experiment.storage.Store;
import uk.co.saiman.osgi.ServiceIndex;
import uk.co.saiman.osgi.ServiceRecord;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.StateMap;

@Designate(ocd = StorageServiceConfiguration.class, factory = true)
@Component(configurationPid = StorageServiceImpl.CONFIGURATION_PID, configurationPolicy = OPTIONAL)
public class StorageServiceImpl implements StorageService {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "Storage Service", description = "A service over a set of available experiment storage implementations")
  public @interface StorageServiceConfiguration {
    String storeFilter() default "";
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.experiment.storage";

  private static final MapIndex<String> STORE_ID = new MapIndex<>(
      ExperimentServiceConstants.STORE_ID,
      stringAccessor());

  private final ServiceIndex<Store<?>, String, Store<?>> storeIndex;

  @Activate
  public StorageServiceImpl(StorageServiceConfiguration configuration, BundleContext context)
      throws InvalidSyntaxException {
    String filterString = "(" + Constants.OBJECTCLASS + "=" + Store.class.getName() + ")";
    if (!configuration.storeFilter().isBlank()) {
      filterString = "(&" + filterString + configuration.storeFilter() + ")";
    }
    storeIndex = ServiceIndex
        .open(
            context,
            createFilter(filterString),
            Function.identity(),
            StorageServiceImpl::storeIndexer);
  }

  private static Optional<String> storeIndexer(
      Store<?> object,
      ServiceReference<Store<?>> serviceReference) {
    return Optional
        .ofNullable((String) serviceReference.getProperty(ExperimentServiceConstants.STORE_ID));
  }

  @Override
  public Stream<Store<?>> stores() {
    return storeIndex.objects();
  }

  @Override
  public StorageConfiguration<?> configureStorage(StateMap persistedState) {
    Store<?> store = storeIndex.get(persistedState.get(STORE_ID)).get().serviceObject();
    return new StorageConfiguration<>(store, persistedState);
  }

  @Override
  public <T> StateMap deconfigureStorage(StorageConfiguration<T> processor) {
    String id = storeIndex.findRecord(processor.store()).flatMap(ServiceRecord::id).get();
    return processor.deconfigure().with(STORE_ID, id);
  }
}
