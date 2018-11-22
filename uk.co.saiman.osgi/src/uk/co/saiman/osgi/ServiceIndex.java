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
import static java.util.function.Function.identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
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
public class ServiceIndex<S, U, T> extends ServiceTracker<S, ServiceRecord<S, U, T>> {
  class ServiceRecordImpl implements ServiceRecord<S, U, T> {
    private final ServiceReference<S> reference;

    private T object;
    private U id;
    private int rank;

    public ServiceRecordImpl(ServiceReference<S> reference, T object) {
      this.reference = reference;
      this.object = object;
      refresh();
    }

    private void refresh() {
      this.id = indexer.apply(object, reference);
      Integer rank = (Integer) reference.getProperty(Constants.SERVICE_RANKING);
      this.rank = rank == null ? 0 : rank;
    }

    @Override
    public ServiceReference<S> serviceReference() {
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

  private final BiFunction<T, ServiceReference<S>, U> indexer;
  private final Function<S, T> extractor;
  private final Map<ServiceReference<S>, ServiceRecord<S, U, T>> records = new HashMap<>();
  private final Map<U, List<ServiceRecord<S, U, T>>> recordsById = new HashMap<>();

  protected ServiceIndex(
      BundleContext context,
      Class<S> clazz,
      BiFunction<T, ServiceReference<S>, U> indexer,
      Function<S, T> extractor) {
    super(context, clazz, null);
    this.indexer = indexer;
    this.extractor = extractor;
  }

  protected ServiceIndex(
      BundleContext context,
      String reference,
      BiFunction<T, ServiceReference<S>, U> indexer,
      Function<S, T> extractor) {
    super(context, reference, null);
    this.indexer = indexer;
    this.extractor = extractor;
  }

  public static <T> ServiceIndex<T, String, T> open(BundleContext context, Class<T> clazz) {
    var serviceIndex = new ServiceIndex<>(
        context,
        clazz,
        (s, reference) -> (String) reference.getProperty(ComponentConstants.COMPONENT_NAME),
        identity());
    serviceIndex.open();
    return serviceIndex;
  }

  public static <T> ServiceIndex<T, String, T> open(BundleContext context, String reference) {
    ServiceIndex<T, String, T> serviceIndex = new ServiceIndex<>(
        context,
        reference,
        (s, r) -> (String) r.getProperty(ComponentConstants.COMPONENT_NAME),
        identity());
    serviceIndex.open();
    return serviceIndex;
  }

  public static <S, T> ServiceIndex<S, String, T> open(
      BundleContext context,
      Class<S> clazz,
      Function<S, T> extractor) {
    var serviceIndex = new ServiceIndex<>(
        context,
        clazz,
        (s, reference) -> (String) reference.getProperty(ComponentConstants.COMPONENT_NAME),
        extractor);
    serviceIndex.open();
    return serviceIndex;
  }

  public static <S, T> ServiceIndex<S, String, T> open(
      BundleContext context,
      String reference,
      Function<S, T> extractor) {
    ServiceIndex<S, String, T> serviceIndex = new ServiceIndex<>(
        context,
        reference,
        (s, r) -> (String) r.getProperty(ComponentConstants.COMPONENT_NAME),
        extractor);
    serviceIndex.open();
    return serviceIndex;
  }

  private void addRecordById(ServiceRecord<S, U, T> record) {
    if (record.id() != null) {
      recordsById.computeIfAbsent(record.id(), i -> new ArrayList<>()).add(record);
      sort(
          recordsById.get(record.id()),
          comparing(r -> ((ServiceIndex<?, ?, ?>.ServiceRecordImpl) r).rank()).reversed());
    }
  }

  private void removeRecordById(ServiceRecord<S, U, T> record) {
    var previousSet = recordsById.get(record.id());
    if (previousSet != null) {
      previousSet.remove(record);
      if (previousSet.isEmpty()) {
        recordsById.remove(record.id());
      }
    }
  }

  @Override
  public synchronized ServiceRecord<S, U, T> addingService(ServiceReference<S> reference) {
    var record = new ServiceRecordImpl(
        reference,
        extractor.apply((S) context.getService(reference)));
    records.put(reference, record);
    addRecordById(record);
    return record;
  }

  @Override
  public synchronized void modifiedService(
      ServiceReference<S> reference,
      ServiceRecord<S, U, T> service) {
    var record = (ServiceRecordImpl) service;
    removeRecordById(record);
    record.refresh();
    addRecordById(record);
  }

  @Override
  public synchronized void removedService(
      ServiceReference<S> reference,
      ServiceRecord<S, U, T> service) {
    records.remove(reference);
    removeRecordById(service);
    context.ungetService(reference);
  }

  public Stream<U> ids() {
    return recordsById.keySet().stream();
  }

  public Stream<ServiceRecord<S, U, T>> records() {
    return records.values().stream().filter(record -> record.id() != null);
  }

  public Stream<T> objects() {
    return records().map(ServiceRecord::serviceObject);
  }

  public Optional<ServiceRecord<S, U, T>> findRecord(T serviceObject) {
    return records
        .values()
        .stream()
        .filter(record -> record.serviceObject() == serviceObject)
        .findFirst();
  }

  public Optional<ServiceRecord<S, U, T>> get(U id) {
    return recordsById.get(id).stream().findFirst();
  }
}
