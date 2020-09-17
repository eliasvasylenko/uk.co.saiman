/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
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
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.observable.ObservableValue;

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
 * @param <T>
 *          the type of the service to track
 */
public class ServiceIndex<S, U, T> extends ServiceTracker<S, ServiceRecord<S, U, T>> {
  private static final String DEFAULT_KEY = Constants.SERVICE_PID;

  private final BiFunction<? super T, ? super ServiceReference<S>, ? extends Stream<? extends U>> indexer;
  private final Function<? super S, ? extends T> extractor;

  private final Map<U, RankedServiceRecords<S, U, T>> records = new HashMap<>();
  private final Map<U, WeakServiceRecords> emptyRecords = new HashMap<>();
  private final ReferenceQueue<RankedServiceRecords<S, U, T>> recordReferenceQueue = new ReferenceQueue<>();

  private final HotObservable<ServiceEvent> events = new HotObservable<>();

  private ServiceIndex(
      BundleContext context,
      Class<S> clazz,
      Function<? super S, ? extends T> extractor,
      BiFunction<? super T, ? super ServiceReference<S>, ? extends Stream<? extends U>> indexer) {
    super(context, clazz, null);
    this.extractor = extractor;
    this.indexer = indexer;
  }

  private ServiceIndex(
      BundleContext context,
      Filter filter,
      Function<? super S, ? extends T> extractor,
      BiFunction<? super T, ? super ServiceReference<S>, ? extends Stream<? extends U>> indexer) {
    super(context, filter, null);
    this.extractor = extractor;
    this.indexer = indexer;
  }

  private static <T, S> Stream<String> defaultIndexer(
      T object,
      ServiceReference<S> serviceReference) {
    return Optional.ofNullable((String) serviceReference.getProperty(DEFAULT_KEY)).stream();
  }

  public static <T> ServiceIndex<T, String, T> open(BundleContext context, Class<T> clazz) {
    var serviceIndex = new ServiceIndex<>(context, clazz, identity(), ServiceIndex::defaultIndexer);
    serviceIndex.open();
    return serviceIndex;
  }

  public static <T> ServiceIndex<T, String, T> open(BundleContext context, Filter filter) {
    ServiceIndex<T, String, T> serviceIndex = new ServiceIndex<>(
        context,
        filter,
        identity(),
        ServiceIndex::defaultIndexer);
    serviceIndex.open();
    return serviceIndex;
  }

  public static <S, T> ServiceIndex<S, String, T> open(
      BundleContext context,
      Class<S> clazz,
      Function<? super S, ? extends T> extractor) {
    ServiceIndex<S, String, T> serviceIndex = new ServiceIndex<>(
        context,
        clazz,
        extractor,
        ServiceIndex::defaultIndexer);
    serviceIndex.open();
    return serviceIndex;
  }

  public static <S, T> ServiceIndex<S, String, T> open(
      BundleContext context,
      Filter filter,
      Function<? super S, ? extends T> extractor) {
    ServiceIndex<S, String, T> serviceIndex = new ServiceIndex<>(
        context,
        filter,
        extractor,
        ServiceIndex::defaultIndexer);
    serviceIndex.open();
    return serviceIndex;
  }

  public static <S, U, T> ServiceIndex<S, U, T> open(
      BundleContext context,
      Class<S> clazz,
      Function<? super S, ? extends T> extractor,
      BiFunction<? super T, ? super ServiceReference<S>, ? extends Stream<? extends U>> indexer) {
    ServiceIndex<S, U, T> serviceIndex = new ServiceIndex<>(context, clazz, extractor, indexer);
    serviceIndex.open();
    return serviceIndex;
  }

  public static <S, U, T> ServiceIndex<S, U, T> open(
      BundleContext context,
      Filter filter,
      Function<S, T> extractor,
      BiFunction<? super T, ? super ServiceReference<S>, ? extends Stream<? extends U>> indexer) {
    var serviceIndex = new ServiceIndex<>(context, filter, extractor, indexer);
    serviceIndex.open();
    return serviceIndex;
  }

  private RankedServiceRecords<S, U, T> getEmptyRecords(U index) {
    return Optional
        .ofNullable(emptyRecords.remove(index))
        .flatMap(reference -> Optional.ofNullable(reference.get()))
        .orElseGet(() -> new RankedServiceRecords<>(index));
  }

  private void addRecordById(ServiceRecord<S, U, T> record) {
    record.ids().forEach(id -> {
      records.computeIfAbsent(id, this::getEmptyRecords).add(record);
    });
  }

  private void removeRecordById(ServiceRecord<S, U, T> record) {
    record.ids().forEach(id -> {
      var previousSet = records.get(id);
      if (previousSet != null) {
        previousSet.remove(record);
        if (previousSet.isEmpty()) {
          records.remove(id);
          emptyRecords.put(id, new WeakServiceRecords(previousSet));
        }
      }
    });
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

  public synchronized Stream<U> ids() {
    return records.keySet().stream();
  }

  public synchronized Stream<ServiceRecord<S, U, T>> records() {
    return records.values().stream().flatMap(RankedServiceRecords::stream).sorted();
  }

  public synchronized Optional<ServiceRecord<S, U, T>> findRecord(T serviceObject) {
    return records().filter(record -> record.serviceObject() == serviceObject).findFirst();
  }

  public synchronized ObservableValue<ServiceRecord<S, U, T>> highestRankedRecord(U id) {
    return Optional
        .ofNullable(records.get(id))
        .orElseGet(() -> getEmptyRecords(id))
        .highestRankedRecord();
  }

  @Override
  public synchronized String toString() {
    return getClass().getSimpleName() + records;
  }

  public Observable<ServiceEvent> events() {
    return events;
  }

  class ServiceRecordImpl implements ServiceRecord<S, U, T> {
    private final ServiceReference<S> reference;

    private T object;
    private List<U> id;
    private int rank;

    public ServiceRecordImpl(ServiceReference<S> reference, T object) {
      this.reference = reference;
      this.object = object;
      refresh();
    }

    private void refresh() {
      this.id = indexer.apply(object, reference).collect(toList());
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
    public Stream<U> ids() {
      return id.stream();
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

  class WeakServiceRecords extends WeakReference<RankedServiceRecords<S, U, T>> {
    private final U id;

    public WeakServiceRecords(RankedServiceRecords<S, U, T> referent) {
      super(referent, recordReferenceQueue);
      this.id = referent.id();
    }

    @Override
    public void clear() {
      super.clear();
      synchronized (ServiceIndex.this) {
        if (emptyRecords.get(id) == this) {
          emptyRecords.remove(id);
        }
      }
    }
  }
}
