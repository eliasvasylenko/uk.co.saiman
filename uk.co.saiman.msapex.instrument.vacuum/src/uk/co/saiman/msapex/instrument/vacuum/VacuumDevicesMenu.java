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
 * This file is part of uk.co.saiman.msapex.instrument.vacuum.
 *
 * uk.co.saiman.msapex.instrument.vacuum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.instrument.vacuum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.instrument.vacuum;

import static java.util.stream.Collectors.toCollection;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.fx.core.di.Service;

import uk.co.saiman.instrument.vacuum.VacuumDevice;

/**
 * Track vacuum devices available through OSGi services and select which device
 * to display in the vacuum part.
 * 
 * @author Elias N Vasylenko
 */
public class VacuumDevicesMenu {
  @AboutToShow
  void aboutToShow(
      List<MMenuElement> items,
      @Service List<VacuumDevice<?>> available,
      @Optional VacuumDeviceSelection selection) {
    if (selection == null)
      selection = new VacuumDeviceSelection();

    Set<VacuumDevice<?>> selectedDevices = selection
        .getSelectedDevices()
        .collect(toCollection(LinkedHashSet::new));

    for (VacuumDevice<?> module : available) {
      MDirectMenuItem moduleItem = MMenuFactory.INSTANCE.createDirectMenuItem();
      moduleItem.setLabel(module.getName());
      moduleItem.setType(ItemType.CHECK);
      moduleItem.setSelected(selectedDevices.contains(module));
      moduleItem.setObject(new Object() {
        @Execute
        public void execute(IEclipseContext context) {
          if (moduleItem.isSelected()) {
            selectedDevices.add(module);
          } else {
            selectedDevices.remove(module);
          }
          context.modify(VacuumDeviceSelection.class, new VacuumDeviceSelection(selectedDevices));
        }
      });

      items.add(moduleItem);
    }
  }
}
