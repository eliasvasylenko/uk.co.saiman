package uk.co.saiman.bytes.conversion;

import static java.util.stream.Stream.concat;
import static uk.co.saiman.collection.StreamUtilities.throwingMerger;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

final class AggregatedByteConversionAnnotations implements ByteConversionAnnotations {
  private final ByteConversionAnnotations first;
  private final ByteConversionAnnotations second;

  AggregatedByteConversionAnnotations(
      ByteConversionAnnotations first,
      ByteConversionAnnotations second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public Stream<Annotation> getAll() {
    Set<Class<?>> repeats = new HashSet<>();
    return concat(
        first.getAll().peek(a -> repeats.add(a.annotationType())),
        second.getAll().filter(a -> repeats.add(a.annotationType())));
  }

  @Override
  public <T extends Annotation> Stream<T> getAll(Class<T> type) {
    if (first.getAll(type).findAny().isPresent()) {
      return first.getAll(type);
    } else {
      return second.getAll(type);
    }
  }

  @Override
  public <T extends Annotation> Optional<T> get(Class<T> type) {
    return getAll(type).reduce(throwingMerger());
  }

  @Override
  public ByteConversionAnnotations and(ByteConversionAnnotations more) {
    return new AggregatedByteConversionAnnotations(this, more);
  }
}