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

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Service;

import uk.co.saiman.eclipse.ui.ChildrenService;
import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.msapex.DeviceCell;

public class InstrumentTree {
  public static final String ID = "uk.co.saiman.instrument.msapex.tree";

  @Inject
  public void prepare(@Service List<Device<?>> data, ChildrenService children) {
    children.setItems(DeviceCell.ID, Device.class, List.copyOf(data));
  }
}
