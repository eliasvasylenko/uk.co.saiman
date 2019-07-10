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

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static uk.co.saiman.eclipse.ui.TransferDestination.BEFORE_CHILD;
import static uk.co.saiman.eclipse.ui.TransferDestination.OVER;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javafx.scene.input.Dragboard;
import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.ui.TransferDestination;
import uk.co.saiman.eclipse.ui.TransferFormat;
import uk.co.saiman.eclipse.ui.TransferMode;
import uk.co.saiman.eclipse.ui.fx.ClipboardCache;
import uk.co.saiman.eclipse.ui.fx.ClipboardService;
import uk.co.saiman.eclipse.ui.fx.TransferCellIn;
import uk.co.saiman.eclipse.ui.fx.TransferModes;

public class TransferCellInImpl implements TransferCellIn {
  private final TransferDestination position;
  private final MCell adjacentItem;
  private final Map<TransferMode, Optional<? extends TransferInImpl<?>>> dropCandidates;

  public TransferCellInImpl(
      List<ItemList<?>> itemLists,
      Dragboard clipboard,
      ClipboardService clipboardService,
      TransferDestination position,
      MCell adjacentItem) {
    this.position = position;
    this.adjacentItem = adjacentItem;

    this.dropCandidates = clipboard
        .getTransferModes()
        .stream()
        .map(TransferModes::fromJavaFXTransferMode)
        .collect(
            toMap(
                identity(),
                transferMode -> forItemLists(
                    itemLists,
                    clipboardService.getCache(clipboard),
                    transferMode)));
  }

  private Optional<? extends TransferInImpl<?>> forItemLists(
      List<ItemList<?>> itemLists,
      ClipboardCache clipboard,
      TransferMode transferMode) {
    return itemLists
        .stream()
        .map(itemList -> forItemList(itemList, clipboard, transferMode))
        .findFirst()
        .flatMap(t -> t);
  }

  private <T> Optional<TransferInImpl<T>> forItemList(
      ItemList<T> itemList,
      ClipboardCache clipboard,
      TransferMode transferMode) {
    return itemList
        .getTransferFormats()
        .filter(transferFormat -> transferFormat.transferModes().contains(transferMode))
        .map(TransferFormat::dataFormat)
        .map(clipboard::getData)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(object -> new TransferInImpl<>(itemList, object, this))
        .findFirst();
  }

  @Override
  public Set<TransferMode> supportedTransferModes() {
    return dropCandidates
        .keySet()
        .stream()
        .filter(mode -> dropCandidates.get(mode).isPresent())
        .collect(toSet());
  }

  @Override
  public void handle(TransferMode transferMode) {
    dropCandidates.get(transferMode).get().handleDrop();
  }

  class TransferInImpl<T> {
    private final ItemList<T> itemList;
    private final T object;

    public TransferInImpl(ItemList<T> itemList, T object, TransferCellInImpl owner) {
      this.itemList = itemList;
      this.object = object;
    }

    public void handleDrop() {
      List<T> items;
      if (position == OVER) {
        items = itemList.getItems().map(Item::getObject).collect(toList());
      } else {
        items = itemList.getItems().flatMap(i -> {
          if (i.getModel() == adjacentItem) {
            if (position == BEFORE_CHILD) {
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
}
