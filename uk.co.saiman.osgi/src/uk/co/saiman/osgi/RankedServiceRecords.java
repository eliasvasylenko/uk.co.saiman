package uk.co.saiman.osgi;

import static java.util.Collections.sort;
import static java.util.Collections.synchronizedList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import uk.co.saiman.observable.ObservablePropertyImpl;
import uk.co.saiman.observable.ObservableValue;

public class RankedServiceRecords<S, U, T> {
  private final List<ServiceRecord<S, U, T>> serviceRecords = synchronizedList(new ArrayList<>());
  private final ObservablePropertyImpl<ServiceRecord<S, U, T>> highestRanked = new ObservablePropertyImpl<>();
  private final U id;

  public RankedServiceRecords(U id) {
    this.id = id;
  }

  public void add(ServiceRecord<S, U, T> record) {
    serviceRecords.add(record);
    sort(serviceRecords, (a, b) -> Integer.compare(b.rank(), a.rank()));
    updateHighest();
  }

  public void remove(ServiceRecord<S, U, T> record) {
    serviceRecords.remove(record);
    updateHighest();
  }

  private void updateHighest() {
    stream().findFirst().ifPresentOrElse(highestRanked::set, highestRanked::unset);
  }

  public boolean isEmpty() {
    return serviceRecords.isEmpty();
  }

  public void dispose() {
    serviceRecords.clear();
    highestRanked.unset();
  }

  public Stream<ServiceRecord<S, U, T>> stream() {
    return serviceRecords.stream();
  }

  public U id() {
    return id;
  }

  public ObservableValue<ServiceRecord<S, U, T>> highestRankedRecord() {
    return highestRanked;
  }
}
