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

import static uk.co.saiman.eclipse.ui.SaiUiModel.NULLABLE;
import static uk.co.saiman.eclipse.ui.SaiUiModel.PRIMARY_CONTEXT_KEY;
import static uk.co.saiman.eclipse.ui.TransferMode.COPY;
import static uk.co.saiman.eclipse.ui.TransferMode.LINK;
import static uk.co.saiman.eclipse.utilities.EclipseContextUtilities.isModifiable;

import java.util.EnumSet;
import java.util.Set;

import javax.inject.Inject;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.ui.TransferDestination;
import uk.co.saiman.eclipse.ui.TransferMode;
import uk.co.saiman.eclipse.ui.fx.TransferCellHandler;
import uk.co.saiman.eclipse.ui.fx.TransferCellIn;
import uk.co.saiman.eclipse.ui.fx.TransferCellOut;

public class DefaultTransferCellHandler implements TransferCellHandler {
  @Inject
  public DefaultTransferCellHandler() {}

  @Override
  public TransferCellOut transferOut(MCell cell) {
    boolean nullable = isModifiable(
        cell.getContext(),
        cell.getProperties().get(PRIMARY_CONTEXT_KEY)) && cell.getTags().contains(NULLABLE);

    ClipboardContent content = serialize(cell);

    if (content.isEmpty()) {
      return TransferCellOut.UNSUPPORTED;
    }

    return new TransferCellOut() {
      @Override
      public Set<TransferMode> supportedTransferModes() {
        return nullable ? EnumSet.allOf(TransferMode.class) : EnumSet.of(COPY, LINK);
      }

      @Override
      public void handle(TransferMode transferMode) {
        System.out.println("handle transfer out " + transferMode + " ? " + nullable);
        if (nullable && transferMode.isDestructive()) {
          cell.getContext().modify(cell.getProperties().get(PRIMARY_CONTEXT_KEY), null);
        }
      }

      @Override
      public ClipboardContent getClipboardContent() {
        return content;
      }
    };
  }

  @Override
  public TransferCellIn transferIn(MCell cell, Dragboard clipboard, TransferDestination position) {
    if (position != TransferDestination.OVER) {
      return TransferCellIn.UNSUPPORTED;
    }

    boolean modifiable = isModifiable(
        cell.getContext(),
        cell.getProperties().get(PRIMARY_CONTEXT_KEY));

    Object value = deserialize(cell, clipboard);

    return new TransferCellIn() {
      @Override
      public Set<TransferMode> supportedTransferModes() {
        return EnumSet.allOf(TransferMode.class);
      }

      @Override
      public void handle(TransferMode transferMode) {
        System.out.println("handle transfer in " + transferMode + " ? " + modifiable);
        if (transferMode.isDestructive()) {
          cell.getContext().modify(cell.getProperties().get(PRIMARY_CONTEXT_KEY), null);
        }
      }
    };
  }

  static ClipboardContent serialize(MCell cell) {
    return new ClipboardContent(); // TODO
  }

  static Object deserialize(MCell cell, Clipboard content) {
    return null; // TODO
  }
}
