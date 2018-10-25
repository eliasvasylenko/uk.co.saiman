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

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.eclipse.ui.SaiUiModel.TRANSFER_FORMAT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.ui.TransferFormat;

public class ItemList<T> {
  private final Cell model;
  private final List<Item<T>> items;
  private final Optional<Consumer<? super List<? extends T>>> update;

  public ItemList(Cell model, T object) {
    this.model = requireNonNull(model);
    this.items = asList(new Item<>(this, object));
    this.update = Optional.empty();
  }

  public ItemList(Cell model, T object, Consumer<? super T> update) {
    this.model = requireNonNull(model);
    this.items = asList(new Item<>(this, object, update));
    this.update = Optional.empty();
  }

  public ItemList(Cell model, List<? extends T> objects) {
    this.model = requireNonNull(model);
    this.items = objects.stream().map(object -> new Item<>(this, object)).collect(toList());
    this.update = Optional.empty();
  }

  public ItemList(
      Cell model,
      List<? extends T> objects,
      Consumer<? super List<? extends T>> update) {
    this.model = requireNonNull(model);
    this.items = IntStream
        .range(0, objects.size())
        .mapToObj(i -> new Item<>(this, objects.get(i), object -> {
          List<T> newObjects = new ArrayList<>(objects);
          newObjects.set(i, object);
          update.accept(newObjects);
        }))
        .collect(toList());
    this.update = Optional.of(update);
  }

  public Stream<Item<T>> getItems() {
    return items.stream();
  }

  public Optional<Consumer<? super List<? extends T>>> getUpdate() {
    return update;
  }

  public Cell getModelSnippet() {
    return model;
  }

  @SuppressWarnings("unchecked")
  public Stream<TransferFormat<T>> getTransferFormats() {
    return ((Collection<?>) getModelSnippet().getTransientData().get(TRANSFER_FORMAT))
        .stream()
        .map(format -> (TransferFormat<T>) format);
  }
}