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
 * This file is part of uk.co.saiman.eclipse.fx.
 *
 * uk.co.saiman.eclipse.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.fx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.ui.fx.impl;

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.eclipse.ui.TransferDestination.BEFORE_CHILD;
import static uk.co.saiman.eclipse.ui.TransferDestination.OVER;

import java.util.List;
import java.util.stream.Stream;

class TransferIn<T> {
  private final ItemList<T> itemList;
  private final T object;
  private final TransfersIn owner;

  public TransferIn(ItemList<T> itemList, T object, TransfersIn owner) {
    this.itemList = itemList;
    this.object = object;
    this.owner = owner;
  }

  public void handleDrop() {
    List<T> items;
    if (owner.position() == OVER) {
      items = itemList.getItems().map(Item::getObject).collect(toList());
    } else {
      items = itemList.getItems().flatMap(i -> {
        if (i == owner.adjacentItem()) {
          if (owner.position() == BEFORE_CHILD) {
            return Stream.of(object, i.getObject());
          } else {
            return Stream.of(i.getObject(), object);
          }
        } else {
          return Stream.of(i.getObject());
        }
      }).collect(toList());
    }
    itemList.getUpdate().get().accept(items);
  }
}