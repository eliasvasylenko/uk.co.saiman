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
 * This file is part of uk.co.saiman.msapex.instrument.
 *
 * uk.co.saiman.msapex.instrument is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.instrument is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.instrument;

import static javafx.css.PseudoClass.getPseudoClass;
import static uk.co.saiman.eclipse.ui.ListItems.ITEM_DATA;

import javax.inject.Named;

import org.eclipse.e4.ui.di.AboutToShow;

import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.instrument.ConnectionState;
import uk.co.saiman.instrument.Device;

public class DeviceCell {
  public static final String ID = "uk.co.saiman.instrument.cell.device";

  @AboutToShow
  public void prepare(HBox node, Cell cell, @Named(ITEM_DATA) Device item) {
    ConnectionState state = item.connectionState().get();

    node.pseudoClassStateChanged(getPseudoClass(state.toString()), true);

    cell.setLabel(item.getName());
    // TODO cell.setSupplemental(state.toString());
  }
}
