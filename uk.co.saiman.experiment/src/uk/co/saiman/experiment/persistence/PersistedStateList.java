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
package uk.co.saiman.experiment.persistence;

import static java.util.stream.Collectors.toList;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import uk.co.saiman.experiment.persistence.PersistedState.PersistedStateSubscription;
import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

public class PersistedStateList implements Iterable<PersistedState> {
  private final List<PersistedStateSubscription> maps = new ArrayList<>();

  private final HotObservable<PersistedStateList> changes = new HotObservable<>();

  public PersistedStateList() {}

  public PersistedStateList(PersistedStateList base) {
    for (PersistedStateSubscription subscription : base.maps) {
      PersistedState copy = new PersistedState(subscription.get());
      maps.add(new PersistedStateSubscription(this::update, copy));
    }
  }

  private void update() {
    changes.next(this);
  }

  boolean isEmpty() {
    return stream().allMatch(PersistedState::isEmpty);
  }

  @Override
  public Iterator<PersistedState> iterator() {
    Iterator<PersistedStateSubscription> iterator = maps.iterator();
    return new Iterator<PersistedState>() {
      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public PersistedState next() {
        return iterator.next().get();
      }
    };
  }

  public PersistedState add() {
    return add(size());
  }

  public PersistedState add(int index) {
    PersistedStateSubscription subscription = new PersistedStateSubscription(this::update);
    maps.add(index, subscription);
    update();
    return subscription.get();
  }

  public void add(PersistedState map) {
    add(size(), map);
  }

  public void add(int index, PersistedState map) {
    PersistedStateSubscription subscription = new PersistedStateSubscription(this::update, map);
    maps.add(index, subscription);
    update();
  }

  public PersistedState set(int index, PersistedState map) {
    PersistedStateSubscription subscription = new PersistedStateSubscription(this::update, map);
    PersistedStateSubscription previous = maps.set(index, subscription);
    previous.cancel();
    update();
    return previous.get();
  }

  public PersistedState get(int index) {
    return maps.get(index).get();
  }

  public PersistedState remove(PersistedState state) {
    for (int i = 0; i < size(); i++) {
      if (get(i).equals(state)) {
        return remove(i);
      }
    }
    throw new IndexOutOfBoundsException();
  }

  public PersistedState remove(int index) {
    PersistedStateSubscription removed = maps.remove(index);
    removed.cancel();
    update();
    return removed.get();
  }

  public int size() {
    return maps.size();
  }

  public Stream<PersistedState> stream() {
    return maps.stream().map(PersistedStateSubscription::get);
  }

  public void clear() {
    maps.forEach(Disposable::cancel);
    maps.clear();
    update();
  }

  public Observable<PersistedStateList> changes() {
    return changes;
  }

  static class PersistedStateListSubscription implements Disposable {
    private final PersistedStateList persistedStateList;
    private final Disposable disposable;

    public PersistedStateListSubscription(Runnable update) {
      persistedStateList = new PersistedStateList();
      disposable = persistedStateList.changes().observe(m -> update.run());
    }

    public PersistedStateListSubscription(Runnable update, PersistedStateList persistedStateList) {
      this.persistedStateList = persistedStateList;
      disposable = persistedStateList.changes().observe(m -> update.run());
    }

    public PersistedStateList get() {
      return persistedStateList;
    }

    @Override
    public void cancel() {
      disposable.cancel();
    }
  }

  public <T> List<T> map(
      Function<? super PersistedState, ? extends T> out,
      Function<? super T, ? extends PersistedState> in) {
    return new AbstractList<T>() {
      private final Map<PersistedState, T> elements = new HashMap<>();

      {
        changes().weakReference(this).observe(o -> o.owner().clearUnused());
      }

      private void clearUnused() {
        elements.keySet().retainAll(PersistedStateList.this.stream().collect(toList()));
      }

      @Override
      public T get(int index) {
        T item = elements.computeIfAbsent(PersistedStateList.this.get(index), out::apply);
        return item;
      }

      @Override
      public T set(int index, T element) {
        T previous = get(index);
        PersistedState state = in.apply(element);
        elements.put(state, element);
        PersistedStateList.this.set(index, state);
        return previous;
      }

      @Override
      public void add(int index, T element) {
        PersistedState state = in.apply(element);
        elements.put(state, element);
        PersistedStateList.this.add(index, state);
      }

      @Override
      public T remove(int index) {
        T previous = get(index);
        PersistedStateList.this.remove(index);
        return previous;
      }

      @Override
      public int size() {
        return PersistedStateList.this.size();
      }
    };
  }
}
