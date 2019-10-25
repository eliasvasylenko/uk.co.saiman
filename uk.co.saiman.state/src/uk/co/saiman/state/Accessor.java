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
 * This file is part of uk.co.saiman.state.
 *
 * uk.co.saiman.state is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.state is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.state;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static uk.co.saiman.state.StateKind.LIST;
import static uk.co.saiman.state.StateKind.MAP;
import static uk.co.saiman.state.StateKind.PROPERTY;
import static uk.co.saiman.state.StateList.toStateList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

/**
 * A bijection between a Java type and a serialization state. Can be used to
 * {@link MapIndex index into a map} or {@link ListIndex a list} to read data
 * from that location, or derive a new state with data set at that location.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of the Java object
 * @param <U>
 *          the type of the state object
 */
/*
 * TODO add API for creating accessors over multiple types of state, e.g. can
 * format as a property or a map depending on type.
 */
public interface Accessor<T, U extends State> {
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
      var owner = this;
      return new PropertyAccessor<>() {
        @Override
        public V read(StateProperty data) {
          return read.apply(owner.read(data));
        }

        @Override
        public StateProperty write(V value) {
          return owner.write(write.apply(value));
        }
      };
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
      var owner = this;
      return new MapAccessor<>() {
        @Override
        public V read(StateMap data) {
          return read.apply(owner.read(data));
        }

        @Override
        public StateMap write(V value) {
          return owner.write(write.apply(value));
        }
      };
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
      var owner = this;
      return new ListAccessor<>() {
        @Override
        public V read(StateList data) {
          return read.apply(owner.read(data));
        }

        @Override
        public StateList write(V value) {
          return owner.write(write.apply(value));
        }
      };
    }
  }

  static PropertyAccessor<StateProperty> propertyAccessor() {
    return new PropertyAccessor<>() {
      @Override
      public StateProperty read(StateProperty data) {
        return data;
      }

      @Override
      public StateProperty write(StateProperty value) {
        return value;
      }
    };
  }

  static MapAccessor<StateMap> mapAccessor() {
    return new MapAccessor<>() {
      @Override
      public StateMap read(StateMap data) {
        return data;
      }

      @Override
      public StateMap write(StateMap value) {
        return value;
      }
    };
  }

  static <T> ListAccessor<StateList> listAccessor() {
    return new ListAccessor<>() {
      @Override
      public StateList read(StateList data) {
        return data;
      }

      @Override
      public StateList write(StateList value) {
        return value;
      }
    };
  }

  static PropertyAccessor<String> stringAccessor() {
    return propertyAccessor().map(StateProperty::getValue, StateProperty::new);
  }

  static PropertyAccessor<Integer> intAccessor() {
    return stringAccessor().map(Integer::parseInt, Objects::toString);
  }

  static PropertyAccessor<Long> longAccessor() {
    return stringAccessor().map(Long::parseLong, Objects::toString);
  }

  static PropertyAccessor<Float> floatAccessor() {
    return stringAccessor().map(Float::parseFloat, Objects::toString);
  }

  static PropertyAccessor<Double> doubleAccessor() {
    return stringAccessor().map(Double::parseDouble, Objects::toString);
  }

  static PropertyAccessor<Boolean> booleanAccessor() {
    return stringAccessor().map(Boolean::parseBoolean, Objects::toString);
  }

  @SuppressWarnings("unchecked")
  default ListAccessor<Stream<T>> toStreamAccessor() {
    return listAccessor()
        .map(
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

  @SuppressWarnings("unchecked")
  default MapAccessor<Optional<T>> toOptionalAccessor() {
    return mapAccessor()
        .map(
            s -> s.getOptional("value").map(v -> read((U) v.as(getKind()))),
            o -> o
                .map(v -> StateMap.empty().with("value", write(v)))
                .orElseGet(() -> StateMap.empty()));
  }
}
