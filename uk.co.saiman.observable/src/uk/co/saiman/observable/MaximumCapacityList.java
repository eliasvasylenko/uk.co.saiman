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
 * This file is part of uk.co.saiman.observable.
 *
 * uk.co.saiman.observable is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.observable is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.observable;

import java.util.AbstractList;
import java.util.ArrayList;

class MaximumCapacityList<T> extends AbstractList<T> {
  private final ArrayList<T> component = new ArrayList<>();
  private final long capacity;

  public MaximumCapacityList(long capacity) {
    this.capacity = capacity;
  }

  @Override
  public T get(int index) {
    return component.get(index);
  }

  @Override
  public int size() {
    return component.size();
  }

  @Override
  public void add(int index, T element) {
    if (size() + 1 > capacity)
      throw new IndexOutOfBoundsException();

    component.add(index, element);
  }

  @Override
  public T remove(int index) {
    return component.remove(index);
  }
}
