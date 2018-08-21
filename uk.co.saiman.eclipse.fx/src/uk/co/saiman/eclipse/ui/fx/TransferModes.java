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

import java.util.Optional;

import uk.co.saiman.eclipse.ui.TransferMode;

public class TransferModes {
  private TransferModes() {}

  public static Optional<javafx.scene.input.TransferMode> toJavaFXTransferMode(TransferMode from) {
    switch (from) {
    case COPY:
      return Optional.of(javafx.scene.input.TransferMode.COPY);
    case LINK:
      return Optional.of(javafx.scene.input.TransferMode.LINK);
    case MOVE:
      return Optional.of(javafx.scene.input.TransferMode.MOVE);
    case DISCARD:
      return Optional.empty();
    default:
      throw new AssertionError();
    }
  }

  public static TransferMode fromJavaFXTransferMode(javafx.scene.input.TransferMode from) {
    switch (from) {
    case COPY:
      return TransferMode.COPY;
    case LINK:
      return TransferMode.LINK;
    case MOVE:
      return TransferMode.MOVE;
    default:
      throw new AssertionError();
    }
  }
}
