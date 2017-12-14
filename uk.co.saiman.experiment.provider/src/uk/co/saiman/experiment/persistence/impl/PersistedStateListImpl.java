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

import static uk.co.saiman.collection.StreamUtilities.upcastStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.experiment.persistence.PersistedStateList;

public class PersistedStateListImpl implements PersistedStateList {
  private final List<PersistedStateImpl> maps = new ArrayList<>();

  private final Runnable update;

  public PersistedStateListImpl(Runnable update) {
    this.update = update;
  }

  private void update() {
    update.run();
  }

  boolean isEmpty() {
    return maps.stream().allMatch(PersistedStateImpl::isEmpty);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator<PersistedState> iterator() {
    // TODO stupid casting, wait for JEP 300
    return (Iterator<PersistedState>) (Iterator<?>) maps.iterator();
  }

  @Override
  public PersistedStateImpl add() {
    return add(size());
  }

  @Override
  public PersistedStateImpl add(int index) {
    PersistedStateImpl element = new PersistedStateImpl(update);
    maps.add(index, element);
    update();
    return element;
  }

  @Override
  public PersistedStateImpl get(int index) {
    return maps.get(index);
  }

  @Override
  public PersistedStateImpl remove(int index) {
    PersistedStateImpl removed = maps.remove(index).removeImpl();
    update();
    return removed;
  }

  @Override
  public int size() {
    return maps.size();
  }

  @Override
  public Stream<PersistedState> stream() {
    return upcastStream(maps.stream());
  }

  @Override
  public PersistedState move(int fromIndex, int toIndex) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void clear() {
    // TODO Auto-generated method stub

  }
}
