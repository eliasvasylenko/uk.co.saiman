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
 * This file is part of uk.co.saiman.experiment.provider.
 *
 * uk.co.saiman.experiment.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.persistence.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.property.Property;

public class PersistedStateImpl implements PersistedState {
  private final Map<String, String> strings = new HashMap<>();
  private final Map<String, PersistedStateImpl> maps = new HashMap<>();
  private final Map<String, PersistedStateListImpl> mapLists = new HashMap<>();

  private Runnable update;

  public PersistedStateImpl(Runnable update) {
    this.update = update;
  }

  private void update() {
    update.run();
  }

  @Override
  public void clear() {
    strings.clear();
    update();
  }

  public PersistedStateImpl removeImpl() {
    update = null;
    return this;
  }

  boolean isEmpty() {
    return strings.isEmpty() && maps.values().stream().allMatch(PersistedStateImpl::isEmpty)
        && mapLists.values().stream().allMatch(PersistedStateListImpl::isEmpty);
  }

  @Override
  public Stream<String> getStrings() {
    return strings.keySet().stream();
  }

  @Override
  public Property<String> forString(String id) {
    return Property.over(() -> strings.get(id), v -> {
      if (!Objects.equals(strings.put(id, v), v)) {
        update();
      }
    });
  }

  @Override
  public Stream<String> getMaps() {
    return maps.keySet().stream();
  }

  @Override
  public PersistedStateImpl forMap(String id) {
    return maps.computeIfAbsent(id, i -> new PersistedStateImpl(update));
  }

  @Override
  public Stream<String> getMapLists() {
    return mapLists.keySet().stream();
  }

  @Override
  public PersistedStateListImpl forMapList(String id) {
    return mapLists.computeIfAbsent(id, i -> new PersistedStateListImpl(update));
  }

  @Override
  public void copyState(PersistedState from) {
    clear();
    from.getStrings().forEach(s -> forString(s).set(from.forString(s).get()));

    // TODO
  }
}
