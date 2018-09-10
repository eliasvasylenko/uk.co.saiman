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
package uk.co.saiman.eclipse.ui.fx;

import static java.util.Collections.emptySet;

import java.util.Set;

import javafx.scene.input.ClipboardContent;
import uk.co.saiman.eclipse.ui.TransferMode;

public interface TransferCellOut {
  TransferCellOut UNSUPPORTED = new TransferCellOut() {
    @Override
    public Set<TransferMode> supportedTransferModes() {
      return emptySet();
    }

    @Override
    public void handle(TransferMode transferMode) {}

    @Override
    public ClipboardContent getClipboardContent() {
      return new ClipboardContent();
    }
  };

  Set<TransferMode> supportedTransferModes();

  void handle(TransferMode transferMode);

  ClipboardContent getClipboardContent();
}
