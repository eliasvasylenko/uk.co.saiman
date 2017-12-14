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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import uk.co.saiman.experiment.persistence.PersistedStateList.PersistedStateListSubscription;
import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.property.Property;

public class PersistedState {
  private final Map<String, String> strings = new HashMap<>();
  private final Map<String, PersistedStateSubscription> maps = new HashMap<>();
  private final Map<String, PersistedStateListSubscription> mapLists = new HashMap<>();

  private final HotObservable<PersistedState> changes = new HotObservable<>();

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

  public PersistedState forMap(String id) {
    return maps.computeIfAbsent(id, i -> new PersistedStateSubscription(this::update)).get();
  }

  public Stream<String> getMapLists() {
    return mapLists.keySet().stream();
  }

  public PersistedStateList forMapList(String id) {
    return mapLists
        .computeIfAbsent(id, i -> new PersistedStateListSubscription(this::update))
        .get();
  }

  public void copyState(PersistedState from) {
    clear();
    from.getStrings().forEach(s -> forString(s).set(from.forString(s).get()));

    // TODO
  }

  public Observable<PersistedState> changes() {
    return changes;
  }

  static class PersistedStateSubscription {
    private final PersistedState persistedState;
    private final Disposable disposable;

    public PersistedStateSubscription(Runnable update) {
      persistedState = new PersistedState();
      disposable = persistedState.changes().observe(m -> update.run());
    }

    public PersistedState get() {
      return persistedState;
    }

    public void cancel() {
      disposable.cancel();
    }
  }
}
