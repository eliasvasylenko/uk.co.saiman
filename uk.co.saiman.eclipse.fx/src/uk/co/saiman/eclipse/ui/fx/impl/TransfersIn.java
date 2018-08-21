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

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javafx.scene.input.Dragboard;
import uk.co.saiman.eclipse.ui.TransferDestination;
import uk.co.saiman.eclipse.ui.TransferFormat;
import uk.co.saiman.eclipse.ui.TransferMode;
import uk.co.saiman.eclipse.ui.fx.ClipboardCache;
import uk.co.saiman.eclipse.ui.fx.ClipboardService;
import uk.co.saiman.eclipse.ui.fx.TransferModes;

class TransfersIn {
  private final TransferDestination position;
  private final Item<?> adjacentItem;
  private final Map<TransferMode, Optional<? extends TransferIn<?>>> dropCandidates;

  public TransfersIn(
      List<ItemList<?>> itemLists,
      Dragboard clipboard,
      ClipboardService clipboardService,
      TransferDestination position,
      Item<?> adjacentItem) {
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

  private Optional<? extends TransferIn<?>> forItemLists(
      List<ItemList<?>> itemLists,
      ClipboardCache clipboard,
      TransferMode transferMode) {
    return itemLists
        .stream()
        .map(itemList -> forItemList(itemList, clipboard, transferMode))
        .findFirst()
        .flatMap(t -> t);
  }

  private <T> Optional<TransferIn<T>> forItemList(
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
        .map(object -> new TransferIn<>(itemList, object, this))
        .findFirst();
  }

  public TransferDestination position() {
    return position;
  }

  public Item<?> adjacentItem() {
    return adjacentItem;
  }

  Set<TransferMode> getCandidateTransferModes() {
    return dropCandidates
        .keySet()
        .stream()
        .filter(mode -> dropCandidates.get(mode).isPresent())
        .collect(toSet());
  }

  TransferIn<?> getCandidate(TransferMode transferMode) {
    return dropCandidates.get(transferMode).get();
  }
}
