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

import java.util.AbstractList;
import java.util.List;

public class MergeIndicesListView<T> extends AbstractList<T> {
  private final List<? extends List<? extends T>> backingList;

  public MergeIndicesListView(List<? extends List<? extends T>> backingList) {
    this.backingList = backingList;
  }

  @Override
  public final T get(int index) {
    int size = 0;
    int previousSize = 0;

    for (List<? extends T> major : backingList) {
      size += major.size();

      if (size > index) {
        return major.get(index - previousSize);
      }

      previousSize = size;
    }

    throw new ArrayIndexOutOfBoundsException();
  }

  @Override
  public final int size() {
    int size = 0;

    for (List<?> major : backingList) {
      size += major.size();
    }

    return size;
  }
}
