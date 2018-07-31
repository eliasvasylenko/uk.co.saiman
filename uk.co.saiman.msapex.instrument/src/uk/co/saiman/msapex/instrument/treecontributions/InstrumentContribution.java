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

import static java.util.stream.Collectors.toList;
import static org.osgi.service.component.ComponentConstants.COMPONENT_NAME;

import javax.inject.Named;

import org.eclipse.e4.ui.di.AboutToShow;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.eclipse.ui.ListItems;
import uk.co.saiman.eclipse.ui.model.MCell;
import uk.co.saiman.eclipse.ui.model.MCellImpl;
import uk.co.saiman.instrument.Instrument;

@Component(name = InstrumentContribution.ID, service = MCell.class)
public class InstrumentContribution extends MCellImpl {
  public static final String ID = "uk.co.saiman.instrument.cell";
  public static final String DEVICES_ID = ID + ".devices";

  public InstrumentContribution() {
    super(ID, Contribution.class);
  }

  @Reference(target = "(" + COMPONENT_NAME + "=" + DeviceContribution.ID + ")")
  public void setChild(MCell nodes) {
    MCellImpl child = new MCellImpl(DEVICES_ID, null);
    child.setSpecialized(nodes);
    child.setParent(this);
  }

  public class Contribution {
    @AboutToShow
    public void prepare(@Named(ENTRY_DATA) Instrument data, ListItems children) {
      children.getConfiguration(DEVICES_ID).setObjects(data.getDevices().collect(toList()));
    }
  }
}
