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
 * This file is part of uk.co.saiman.instrument.msapex.
 *
 * uk.co.saiman.instrument.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.msapex;

import static javafx.css.PseudoClass.getPseudoClass;

import javax.inject.Inject;

import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.msapex.device.DevicePresentationService;

public class DeviceCell {
  public static final String ID = "uk.co.saiman.instrument.device.cell";

  @Inject
  public void prepare(
      HBox node,
      MCell cell,
      Device item,
      DevicePresentationService presentationService) {
    cell.setLabel(presentationService.present(item).getLocalizedLabel());
    cell.setIconURI(presentationService.present(item).getIconURI());

    item.status().value().observe(state -> {
      node.pseudoClassStateChanged(getPseudoClass(state.toString()), true);
      // cell.setIcon(iconFor(state));
      // TODO cell.setSupplemental(state.toString());
    });
  }
}
