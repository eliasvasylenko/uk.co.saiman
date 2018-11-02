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
 * This file is part of uk.co.saiman.osgi.
 *
 * uk.co.saiman.osgi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.osgi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.osgi;

import static java.util.Collections.sort;
import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentConstants;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Elias N Vasylenko
 *
 * @param <T> the type of the service to track
 */
public class ServiceIndex<T, U> extends ServiceTracker<T, ServiceRecord<T, U>> {
  class ServiceRecordImpl implements ServiceRecord<T, U> {
    private final ServiceReference<T> reference;

    private T object;
    private U id;
    private int rank;

    public ServiceRecordImpl(ServiceReference<T> reference, T object) {
      this.reference = reference;
      this.object = object;
      refresh();
    }

    private void refresh() {
      this.id = indexer.apply(reference);
      Integer rank = (Integer) reference.getProperty(Constants.SERVICE_RANKING);
      this.rank = rank == null ? 0 : rank;
    }

    @Override
    public ServiceReference<T> serviceReference() {
      return reference;
    }

    @Override
    public T serviceObject() {
      return object;
    }

    @Override
    public U id() {
      return id;
    }

    public int rank() {
      return rank;
    }

    @Override
    public Bundle bundle() {
      return reference.getBundle();
    }
  }

  private final Function<ServiceReference<T>, U> indexer;
  private final Map<ServiceReference<T>, ServiceRecord<T, U>> records = new HashMap<>();
  private final Map<U, List<ServiceRecord<T, U>>> recordsById = new HashMap<>();

  protected ServiceIndex(
      BundleContext context,
      Class<T> clazz,
      Function<ServiceReference<T>, U> indexer) {
    super(context, clazz, null);
    this.indexer = indexer;
  }

  protected ServiceIndex(
      BundleContext context,
      String reference,
      Function<ServiceReference<T>, U> indexer) {
    super(context, reference, null);
    this.indexer = indexer;
  }

  public static <T, U> ServiceIndex<T, U> open(
      BundleContext context,
      Class<T> clazz,
      Function<ServiceReference<T>, U> indexer) {
    return new ServiceIndex<>(context, clazz, indexer);
  }

  public static <T> ServiceIndex<T, String> open(BundleContext context, Class<T> clazz) {
    var serviceIndex = new ServiceIndex<>(
        context,
        clazz,
        reference -> (String) reference.getProperty(ComponentConstants.COMPONENT_NAME));
    serviceIndex.open();
    return serviceIndex;
  }

  public static <T, U> ServiceIndex<T, U> open(
      BundleContext context,
      String reference,
      Function<ServiceReference<T>, U> indexer) {
    return new ServiceIndex<>(context, reference, indexer);
  }

  public static <T> ServiceIndex<T, String> open(BundleContext context, String reference) {
    ServiceIndex<T, String> serviceIndex = new ServiceIndex<>(
        context,
        reference,
        r -> (String) r.getProperty(ComponentConstants.COMPONENT_NAME));
    serviceIndex.open();
    return serviceIndex;
  }

  private void addRecordById(ServiceRecord<T, U> record) {
    if (record.id() != null) {
      recordsById.computeIfAbsent(record.id(), i -> new ArrayList<>()).add(record);
      sort(
          recordsById.get(record.id()),
          comparing(r -> ((ServiceIndex<?, ?>.ServiceRecordImpl) r).rank()).reversed());
    }
  }

  private void removeRecordById(ServiceRecord<T, U> record) {
    var previousSet = recordsById.get(record.id());
    if (previousSet != null) {
      previousSet.remove(record);
      if (previousSet.isEmpty()) {
        recordsById.remove(record.id());
      }
    }
  }

  @Override
  public synchronized ServiceRecord<T, U> addingService(ServiceReference<T> reference) {
    var record = new ServiceRecordImpl(reference, (T) context.getService(reference));
    records.put(reference, record);
    addRecordById(record);
    return record;
  }

  @Override
  public synchronized void modifiedService(
      ServiceReference<T> reference,
      ServiceRecord<T, U> service) {
    var record = (ServiceRecordImpl) service;
    removeRecordById(record);
    record.refresh();
    addRecordById(record);
  }

  @Override
  public synchronized void removedService(
      ServiceReference<T> reference,
      ServiceRecord<T, U> service) {
    records.remove(reference);
    removeRecordById(service);
    context.ungetService(reference);
  }

  public Stream<U> ids() {
    return recordsById.keySet().stream();
  }

  public Stream<ServiceRecord<T, U>> records() {
    return records.values().stream().filter(record -> record.id() != null);
  }

  public Stream<T> objects() {
    return records().map(ServiceRecord::serviceObject);
  }

  public Optional<ServiceRecord<T, U>> findRecord(T serviceObject) {
    return records
        .values()
        .stream()
        .filter(record -> record.serviceObject() == serviceObject)
        .findFirst();
  }

  public Optional<ServiceRecord<T, U>> get(U id) {
    return recordsById.get(id).stream().findFirst();
  }
}
