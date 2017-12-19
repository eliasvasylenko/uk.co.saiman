/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.api.
 *
 * uk.co.saiman.experiment.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.persistence;

import static java.util.Optional.of;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import uk.co.saiman.experiment.persistence.PersistedStateList.PersistedStateListSubscription;
import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.property.Property;
import uk.co.saiman.utility.Copyable;

public class PersistedState implements Copyable<PersistedState> {
  private final Map<String, String> strings = new HashMap<>();
  private final Map<String, PersistedStateSubscription> maps = new HashMap<>();
  private final Map<String, PersistedStateListSubscription> mapLists = new HashMap<>();

  private final HotObservable<PersistedState> changes = new HotObservable<>();

  public PersistedState() {}

  public PersistedState(PersistedState base) {
    strings.putAll(base.strings);
    for (String id : base.maps.keySet()) {
      PersistedState copy = new PersistedState(base.maps.get(id).get());
      maps.put(id, new PersistedStateSubscription(this::update, copy));
    }
    for (String id : base.maps.keySet()) {
      PersistedStateList copy = new PersistedStateList(base.mapLists.get(id).get());
      mapLists.put(id, new PersistedStateListSubscription(this::update, copy));
    }
  }

  private void update() {
    changes.next(this);
  }

  public void clear() {
    strings.clear();
    update();
  }

  boolean isEmpty() {
    return strings.isEmpty() && maps.values().stream().allMatch(p -> p.get().isEmpty())
        && mapLists.values().stream().allMatch(p -> p.get().isEmpty());
  }

  public Stream<String> getStrings() {
    return strings.keySet().stream();
  }

  public Property<String> forString(String id) {
    return Property.over(() -> strings.get(id), v -> {
      if (!Objects.equals(strings.put(id, v), v)) {
        update();
      }
    });
  }

  public Stream<String> getMaps() {
    return maps.keySet().stream();
  }

  public PersistedState getMap(String id) {
    return maps.computeIfAbsent(id, i -> new PersistedStateSubscription(this::update)).get();
  }

  public void setMap(String id, PersistedState map) {
    PersistedStateSubscription subscription = new PersistedStateSubscription(this::update, map);
    of(maps.put(id, subscription)).ifPresent(Disposable::cancel);
  }

  public Stream<String> getMapLists() {
    return mapLists.keySet().stream();
  }

  public PersistedStateList getMapList(String id) {
    return mapLists
        .computeIfAbsent(id, i -> new PersistedStateListSubscription(this::update))
        .get();
  }

  public void setMapList(String id, PersistedStateList list) {
    PersistedStateListSubscription subscription = new PersistedStateListSubscription(
        this::update,
        list);
    of(mapLists.put(id, subscription)).ifPresent(Disposable::cancel);
  }

  @Override
  public PersistedState copy() {
    return new PersistedState(this);
  }

  public Observable<PersistedState> changes() {
    return changes;
  }

  static class PersistedStateSubscription implements Disposable {
    private final PersistedState persistedState;
    private final Disposable disposable;

    public PersistedStateSubscription(Runnable update) {
      persistedState = new PersistedState();
      disposable = persistedState.changes().observe(m -> update.run());
    }

    public PersistedStateSubscription(Runnable update, PersistedState persistedState) {
      this.persistedState = persistedState;
      disposable = persistedState.changes().observe(m -> update.run());
    }

    public PersistedState get() {
      return persistedState;
    }

    @Override
    public void cancel() {
      disposable.cancel();
    }
  }
}
