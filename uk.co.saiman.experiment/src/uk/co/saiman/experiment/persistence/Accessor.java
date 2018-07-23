package uk.co.saiman.experiment.persistence;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static uk.co.saiman.experiment.persistence.StateKind.LIST;
import static uk.co.saiman.experiment.persistence.StateKind.MAP;
import static uk.co.saiman.experiment.persistence.StateKind.PROPERTY;
import static uk.co.saiman.experiment.persistence.StateList.toStateList;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public interface Accessor<T, U extends State> {
  String id();

  T read(U data);

  U write(T value);

  StateKind getKind();

  <V> Accessor<V, U> map(
      Function<? super T, ? extends V> read,
      Function<? super V, ? extends T> write);

  interface PropertyAccessor<T> extends Accessor<T, StateProperty> {
    @Override
    default StateKind getKind() {
      return PROPERTY;
    }

    @Override
    default <V> PropertyAccessor<V> map(
        Function<? super T, ? extends V> read,
        Function<? super V, ? extends T> write) {
      return propertyAccessor(id(), s -> read.apply(read(s)), s -> write(write.apply(s)));
    }
  }

  interface MapAccessor<T> extends Accessor<T, StateMap> {
    @Override
    default StateKind getKind() {
      return MAP;
    }

    @Override
    default <V> MapAccessor<V> map(
        Function<? super T, ? extends V> read,
        Function<? super V, ? extends T> write) {
      return mapAccessor(id(), s -> read.apply(read(s)), s -> write(write.apply(s)));
    }
  }

  interface ListAccessor<T> extends Accessor<T, StateList> {
    @Override
    default StateKind getKind() {
      return LIST;
    }

    @Override
    default <V> ListAccessor<V> map(
        Function<? super T, ? extends V> read,
        Function<? super V, ? extends T> write) {
      return listAccessor(id(), s -> read.apply(read(s)), s -> write(write.apply(s)));
    }
  }

  static <T> PropertyAccessor<T> propertyAccessor(
      String name,
      Function<? super StateProperty, ? extends T> read,
      Function<? super T, ? extends StateProperty> write) {
    return new PropertyAccessor<T>() {
      @Override
      public String id() {
        return name;
      }

      @Override
      public T read(StateProperty data) {
        return read.apply(data);
      }

      @Override
      public StateProperty write(T value) {
        return write.apply(value);
      }
    };
  }

  static <T> MapAccessor<T> mapAccessor(
      String name,
      Function<? super StateMap, ? extends T> read,
      Function<? super T, ? extends StateMap> write) {
    return new MapAccessor<T>() {
      @Override
      public String id() {
        return name;
      }

      @Override
      public T read(StateMap data) {
        return read.apply(data);
      }

      @Override
      public StateMap write(T value) {
        return write.apply(value);
      }
    };
  }

  static <T> ListAccessor<T> listAccessor(
      String name,
      Function<? super StateList, ? extends T> read,
      Function<? super T, ? extends StateList> write) {
    return new ListAccessor<T>() {
      @Override
      public String id() {
        return name;
      }

      @Override
      public T read(StateList data) {
        return read.apply(data);
      }

      @Override
      public StateList write(T value) {
        return write.apply(value);
      }
    };
  }

  static PropertyAccessor<String> stringAccessor(String name) {
    return propertyAccessor(name, StateProperty::getValue, StateProperty::stateProperty);
  }

  static PropertyAccessor<Integer> intAccessor(String name) {
    return stringAccessor(name).map(Integer::parseInt, Objects::toString);
  }

  static PropertyAccessor<Long> longAccessor(String name) {
    return stringAccessor(name).map(Long::parseLong, Objects::toString);
  }

  static PropertyAccessor<Float> floatAccessor(String name) {
    return stringAccessor(name).map(Float::parseFloat, Objects::toString);
  }

  static PropertyAccessor<Double> doubleAccessor(String name) {
    return stringAccessor(name).map(Double::parseDouble, Objects::toString);
  }

  static PropertyAccessor<Boolean> booleanAccessor(String name) {
    return stringAccessor(name).map(Boolean::parseBoolean, Objects::toString);
  }

  @SuppressWarnings("unchecked")
  default ListAccessor<Stream<T>> toStreamAccessor() {
    return listAccessor(
        id(),
        s -> s.stream().map(e -> read((U) e)),
        a -> a.map(e -> write(e)).collect(toStateList()));
  }

  default ListAccessor<T[]> toArrayAccessor(IntFunction<T[]> newArray) {
    return toStreamAccessor().map(s -> s.toArray(newArray), Stream::of);
  }

  default ListAccessor<List<T>> toListAccessor() {
    return toStreamAccessor().map(s -> s.collect(toList()), List::stream);
  }

  default ListAccessor<Set<T>> toSetAccessor() {
    return toStreamAccessor().map(s -> s.collect(toSet()), Set::stream);
  }
}
