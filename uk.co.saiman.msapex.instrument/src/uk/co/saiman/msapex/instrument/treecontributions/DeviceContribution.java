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
package uk.co.saiman.msapex.instrument.treecontributions;

import static javafx.css.PseudoClass.getPseudoClass;
import static uk.co.saiman.eclipse.ui.fx.TableService.setLabel;
import static uk.co.saiman.eclipse.ui.fx.TableService.setSupplemental;

import javax.inject.Named;

import org.eclipse.e4.ui.di.AboutToShow;
import org.osgi.service.component.annotations.Component;

import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.ui.model.MCell;
import uk.co.saiman.eclipse.ui.model.MCellImpl;
import uk.co.saiman.instrument.ConnectionState;
import uk.co.saiman.instrument.Device;

@Component(name = DeviceContribution.ID, service = MCell.class)
public class DeviceContribution extends MCellImpl {
  public static final String ID = "uk.co.saiman.instrument.cell.device";

  public DeviceContribution() {
    super(ID, Contribution.class);
  }

  public class Contribution {
    @AboutToShow
    public void prepare(HBox node, @Named(ENTRY_DATA) Device item) {
      ConnectionState state = item.connectionState().get();

      node.pseudoClassStateChanged(getPseudoClass(state.toString()), true);

      setLabel(node, item.getName());
      setSupplemental(node, state.toString());
    }
  }
}
