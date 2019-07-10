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

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.ui.TransferFormat;

public class Item<T> {
  private final ItemList<T> owner;
  private final MCell model;
  private final T object;
  private final Optional<Consumer<? super T>> update;

  public Item(ItemList<T> owner, T object, Consumer<? super T> update) {
    this.owner = requireNonNull(owner);
    this.object = object;
    this.update = Optional.of(update);
    this.model = owner.getModelSnippet();
  }

  public Item(ItemList<T> owner, T object) {
    this.owner = requireNonNull(owner);
    this.object = object;
    this.update = Optional.empty();
    this.model = owner.getModelSnippet();
  }

  public T getObject() {
    return object;
  }

  public Optional<Consumer<? super T>> getUpdate() {
    return update;
  }

  public Optional<Runnable> getRemove() {
    return getList()
        .getUpdate()
        .map(
            u -> () -> u
                .accept(
                    getList()
                        .getItems()
                        .filter(i -> i != this)
                        .map(Item::getObject)
                        .collect(toList())));
  }

  public MCell getModel() {
    return model;
  }

  public ItemList<T> getList() {
    return owner;
  }

  public Stream<TransferFormat<T>> getTransferFormats() {
    return getList().getTransferFormats();
  }
}
