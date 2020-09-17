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
 * This file is part of uk.co.saiman.collections.
 *
 * uk.co.saiman.collections is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.collections is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.collection;

import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import uk.co.saiman.function.ThrowingSupplier;

/**
 * A collection of static utility methods for working with streams.
 * 
 * @author Elias N Vasylenko
 */
public class StreamUtilities {
  private StreamUtilities() {}

  private static final BinaryOperator<Object> THROWING_SERIAL_COMBINER = (a, b) -> {
    throw new IllegalArgumentException("Cannot combine parallel execution of serial stream");
  };

  @SuppressWarnings("unchecked")
  public static <T> BinaryOperator<T> throwingSerialCombiner() {
    return (BinaryOperator<T>) THROWING_SERIAL_COMBINER;
  }

  private static final BinaryOperator<Object> THROWING_MERGER = (a, b) -> {
    throw new IllegalArgumentException("Cannot combine items " + a + ", " + b);
  };

  @SuppressWarnings("unchecked")
  public static <T> BinaryOperator<T> throwingMerger() {
    return (BinaryOperator<T>) THROWING_MERGER;
  }

  public static boolean equals(Stream<?> first, Stream<?> second) {
    Iterator<?> firstIterator = first.iterator();
    Iterator<?> secondIterator = second.iterator();

    while (firstIterator.hasNext() && secondIterator.hasNext()) {
      if (!Objects.equals(firstIterator.next(), secondIterator.next())) {
        return false;
      }
    }

    return !firstIterator.hasNext() && !secondIterator.hasNext();
  }

  public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }

  /*
   * TODO this should eventually be removed if/when JEP 300 gives us
   * declaration-site variance, as it will surely make this unnecessary.
   */
  @SuppressWarnings("unchecked")
  public static <T> Stream<T> upcastStream(Stream<? extends T> stream) {
    return (Stream<T>) stream;
  }

  /*
   * TODO this should eventually be removed if/when JEP 300 gives us
   * declaration-site variance, as it will surely make this unnecessary.
   */
  @SuppressWarnings("unchecked")
  public static <T> Optional<T> upcastOptional(Optional<? extends T> stream) {
    return (Optional<T>) stream;
  }

  /*
   * TODO this should be removed when we move to java 9 and get flatmap to
   * optional
   */
  @SuppressWarnings("unchecked")
  public static <T> Stream<T> streamOptional(Optional<? extends T> optional) {
    return (Stream<T>) optional.map(Stream::of).orElse(Stream.empty());
  }

  /*
   * TODO this should be removed when we move to java 9 and get flatmap to
   * optional
   */
  public static <T> Stream<T> streamNullable(T optional) {
    return ofNullable(optional).map(Stream::of).orElse(Stream.empty());
  }

  public static <T> Optional<T> tryOptional(
      ThrowingSupplier<? extends T, ? extends Exception> attempt) {
    return tryOptional(attempt, e -> {}, e -> {});
  }

  @SuppressWarnings("unchecked")
  public static <T, E extends Throwable> Optional<T> tryOptional(
      ThrowingSupplier<? extends T, E> attempt,
      Consumer<? super Exception> runtimeExceptions,
      Consumer<? super E> checkedExceptions) {
    try {
      return Optional.of(attempt.get());
    } catch (RuntimeException e) {
      runtimeExceptions.accept(e);
      return Optional.empty();
    } catch (Throwable e) {
      checkedExceptions.accept((E) e);
      return Optional.empty();
    }
  }

  public static <T> Optional<T> tryOptional(
      Supplier<T> attempt,
      Consumer<? super RuntimeException> exceptions) {
    try {
      return Optional.of(attempt.get());
    } catch (RuntimeException e) {
      exceptions.accept(e);
      return Optional.empty();
    }
  }

  public static <A, B> Collector<Entry<? extends A, ? extends B>, ?, Map<A, B>> entriesToMap() {
    return entriesToMap(throwingMerger());
  }

  public static <A, B> Collector<Entry<? extends A, ? extends B>, ?, Map<A, B>> entriesToMap(
      BinaryOperator<B> merge) {
    return Collectors.toMap(Entry::getKey, Entry::getValue, merge, LinkedHashMap::new);
  }

  public static <T, V> Function<T, Entry<T, V>> mapToEntry(Function<T, V> value) {
    return mapToEntry(identity(), value);
  }

  public static <T, K, V> Function<T, Entry<K, V>> mapToEntry(
      Function<T, K> key,
      Function<T, V> value) {
    return t -> new SimpleEntry<>(key.apply(t), value.apply(t));
  }

  public static <A, B> Stream<Entry<A, B>> zip(Stream<A> first, Stream<B> second) {
    return zip(first, second, (Supplier<RuntimeException>) null);
  }

  public static <A, B> Stream<Entry<A, B>> zip(
      Stream<A> first,
      Stream<B> second,
      Supplier<RuntimeException> mismatchedStreams) {
    return zip(first, second, SimpleEntry<A, B>::new, mismatchedStreams);
  }

  public static <A, B, R> Stream<R> zip(
      Stream<A> first,
      Stream<B> second,
      BiFunction<A, B, R> combiner) {
    return zip(first, second, combiner, null);
  }

  public static <A, B, R> Stream<R> zip(
      Stream<A> first,
      Stream<B> second,
      BiFunction<A, B, R> combiner,
      Supplier<RuntimeException> mismatchedStreams) {
    Iterator<A> firstIterator = first.iterator();
    Iterator<B> secondIterator = second.iterator();

    Iterable<R> i = () -> new Iterator<R>() {
      @Override
      public boolean hasNext() {
        if (firstIterator.hasNext() != secondIterator.hasNext()) {
          if (mismatchedStreams != null) {
            throw mismatchedStreams.get();
          } else {
            return false;
          }
        } else {
          return firstIterator.hasNext();
        }
      }

      @Override
      public R next() {
        return combiner.apply(firstIterator.next(), secondIterator.next());
      }
    };

    return StreamSupport.stream(i.spliterator(), false);
  }

  /**
   * @param stream
   *          an ordered stream
   * @param <T>
   *          the type of the stream elements
   * @return a new stream over the elements contained in the given stream in
   *         reverse order
   */
  public static <T> Stream<T> reverse(Stream<? extends T> stream) {
    List<T> collection = stream.collect(Collectors.toList());

    Iterator<T> iterator = new Iterator<T>() {
      private int index = collection.size();

      @Override
      public boolean hasNext() {
        return index > 0;
      }

      @Override
      public T next() {
        return collection.get(--index);
      }
    };

    return StreamSupport
        .stream(
            Spliterators
                .spliterator(
                    iterator,
                    collection.size(),
                    Spliterator.ORDERED | Spliterator.IMMUTABLE),
            false);
  }

  /**
   * A bit like {@link Stream#iterate(Object, UnaryOperator)} but not
   * <em>completely and utterly useless</em> because it actually supports
   * termination.
   * 
   * @param <T>
   *          the type of the stream elements
   * @param root
   *          the root element
   * @param mapping
   *          a mapping from an element to the next element
   * @return a stream over each element in sequence
   */
  public static <T> Stream<T> iterateOptional(
      T root,
      Function<? super T, Optional<? extends T>> mapping) {
    return iterateOptional(Optional.ofNullable(root), mapping);
  }

  /**
   * A bit like {@link Stream#iterate(Object, UnaryOperator)} but not
   * <em>completely and utterly useless</em> because it actually supports
   * termination.
   * 
   * @param <T>
   *          the type of the stream elements
   * @param root
   *          the root element
   * @param mapping
   *          a mapping from an element to the next element
   * @return a stream over each element in sequence
   */
  public static <T> Stream<T> iterateOptional(
      Optional<? extends T> root,
      Function<? super T, Optional<? extends T>> mapping) {
    Iterator<T> iterator = new Iterator<T>() {
      private Optional<? extends T> item = root;

      @Override
      public boolean hasNext() {
        return item.isPresent();
      }

      @Override
      public T next() {
        T result = item.get();

        item = mapping.apply(result);

        return result;
      }
    };
    return StreamSupport
        .stream(
            Spliterators
                .spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.IMMUTABLE),
            false);
  }

  /**
   * Generate a stream which recursively traverses depth-first over the elements
   * of some nested data structure starting from its root.
   * 
   * @param <T>
   *          the type of the stream elements
   * @param root
   *          the root element
   * @param mapping
   *          a mapping from an element to a stream of its direct children
   * @return a stream over the root and each of its children, as well as each of
   *         their children, in a depth first manner
   */
  public static <T> Stream<T> flatMapRecursive(
      T root,
      Function<? super T, ? extends Stream<? extends T>> mapping) {
    return flatMapRecursive(Stream.of(root), mapping);
  }

  /**
   * Generate a stream which recursively traverses depth-first over the elements
   * of some nested data structure starting from those in a given stream.
   * 
   * @param <T>
   *          the type of the stream elements
   * @param stream
   *          the stream of initial elements
   * @param mapping
   *          a mapping from an element to a stream of its direct children
   * @return a stream over elements in a tree and each of their children, as well
   *         as each of their children, in a depth first manner
   */
  public static <T> Stream<T> flatMapRecursive(
      Stream<? extends T> stream,
      Function<? super T, ? extends Stream<? extends T>> mapping) {
    return stream.flatMap(s -> concat(of(s), flatMapRecursive(mapping.apply(s), mapping)));
  }

  public static <T> Stream<T> flatMapRecursive(
      Stream<? extends T> stream,
      Function<? super T, ? extends Stream<? extends T>> mapping,
      Predicate<? super T> filter) {
    return stream
        .filter(filter)
        .flatMap(s -> concat(of(s), flatMapRecursive(mapping.apply(s), mapping, filter)));
  }

  /**
   * Generate a stream which recursively traverses depth-first over the elements
   * of some nested data structure starting from its root.
   * 
   * @param <T>
   *          the type of the stream elements
   * @param root
   *          the root element
   * @param mapping
   *          a mapping from an element to a stream of its direct children
   * @return a stream over the root and each of its children, as well as each of
   *         their children, in a depth first manner
   */
  public static <T> Stream<T> flatMapRecursiveDistinct(
      T root,
      Function<? super T, ? extends Stream<? extends T>> mapping) {
    return flatMapRecursiveDistinct(Stream.of(root), mapping);
  }

  /**
   * Generate a stream which recursively traverses depth-first over the elements
   * of some nested data structure starting from those in a given stream.
   * <p>
   * Repeated elements will be ignored.
   * 
   * @param <T>
   *          the type of the stream elements
   * @param stream
   *          the stream of initial elements
   * @param mapping
   *          a mapping from an element to a stream of its direct children
   * @return a stream over elements in a tree and each of their children, as well
   *         as each of their children, in a depth first manner
   */
  public static <T> Stream<T> flatMapRecursiveDistinct(
      Stream<? extends T> stream,
      Function<? super T, ? extends Stream<? extends T>> mapping) {
    return flatMapRecursiveDistinct(stream, mapping, new HashSet<>());
  }

  protected static <T> Stream<T> flatMapRecursiveDistinct(
      Stream<? extends T> stream,
      Function<? super T, ? extends Stream<? extends T>> mapping,
      Set<T> visited) {
    return stream
        .filter(visited::add)
        .flatMap(s -> concat(of(s), flatMapRecursiveDistinct(mapping.apply(s), mapping, visited)));
  }
}
