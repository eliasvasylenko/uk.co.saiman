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
import org.osgi.util.tracker.ServiceTracker;

import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

/**
 * A service index is an OSGi service tracker which dynamically registers
 * tracked services into an index. As services come and go, it extracts a key
 * from the respective service reference and enters the reference into the index
 * by that key.
 * <p>
 * By default the key extracted is the component name of the service. When
 * multiple services are found with the same key, only the highest ranking is
 * indexed.
 * <p>
 * The component name of a service can be used as a persistent id, so the
 * default behavior of indexing by component name allows consumers to choose an
 * indexed service, persist their choice, and then recall their choice as
 * services come and go or at the next framework restart.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the type of the service to track
 */
public class ServiceIndex<S, U, T> extends ServiceTracker<S, ServiceRecord<S, U, T>> {
  private static final String DEFAULT_KEY = Constants.SERVICE_PID;

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

    @Override
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
  private final Map<U, List<ServiceRecord<S, U, T>>> recordsById = new HashMap<>();
  private final HotObservable<ServiveEvent> events = new HotObservable<>();

  private ServiceIndex(
      BundleContext context,
      Class<S> clazz,
      Function<S, T> extractor,
      BiFunction<T, ServiceReference<S>, U> indexer) {
    super(context, clazz, null);
    this.extractor = extractor;
    this.indexer = indexer;
  }

  private ServiceIndex(
      BundleContext context,
      String reference,
      Function<S, T> extractor,
      BiFunction<T, ServiceReference<S>, U> indexer) {
    super(context, reference, null);
    this.extractor = extractor;
    this.indexer = indexer;
  }

  public static <T> ServiceIndex<T, String, T> open(BundleContext context, Class<T> clazz) {
    var serviceIndex = new ServiceIndex<>(
        context,
        clazz,
        identity(),
        (s, reference) -> (String) reference.getProperty(DEFAULT_KEY));
    serviceIndex.open();
    return serviceIndex;
  }

  public static <T> ServiceIndex<T, String, T> open(BundleContext context, String reference) {
    ServiceIndex<T, String, T> serviceIndex = new ServiceIndex<>(
        context,
        reference,
        identity(),
        (s, r) -> (String) r.getProperty(DEFAULT_KEY));
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
        extractor,
        (s, reference) -> (String) reference.getProperty(DEFAULT_KEY));
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
        extractor,
        (s, r) -> (String) r.getProperty(DEFAULT_KEY));
    serviceIndex.open();
    return serviceIndex;
  }

  private void addRecordById(ServiceRecord<S, U, T> record) {
    if (record.id() != null) {
      recordsById.computeIfAbsent(record.id(), i -> new ArrayList<>()).add(record);
      sort(recordsById.get(record.id()));
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
    addRecordById(record);
    events.next(new ServiceAddedEvent(record));
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
    events.next(new ServiceModifiedEvent(service));
  }

  @Override
  public synchronized void removedService(
      ServiceReference<S> reference,
      ServiceRecord<S, U, T> service) {
    removeRecordById(service);
    context.ungetService(reference);
    events.next(new ServiceRemovedEvent(reference));
  }

  public Stream<U> ids() {
    return recordsById.keySet().stream();
  }

  public Stream<ServiceRecord<S, U, T>> records() {
    return recordsById.values().stream().flatMap(List::stream).sorted();
  }

  public Stream<T> objects() {
    return records().map(ServiceRecord::serviceObject);
  }

  public Optional<ServiceRecord<S, U, T>> findRecord(T serviceObject) {
    return records().filter(record -> record.serviceObject() == serviceObject).findFirst();
  }

  public Optional<ServiceRecord<S, U, T>> get(U id) {
    return Optional
        .ofNullable(recordsById.get(id))
        .flatMap(records -> records.stream().findFirst());
  }

  public Observable<ServiveEvent> events() {
    return events;
  }
}
