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
 * This file is part of uk.co.saiman.msapex.chemistry.
 *
 * uk.co.saiman.msapex.chemistry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.chemistry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.chemistry;

import static uk.co.saiman.utility.Enums.next;

import java.util.Arrays;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;

import uk.co.saiman.msapex.chemistry.ChemicalElementTile.Size;

/**
 * Toggle available size options for the periodic table
 * 
 * @author Elias N Vasylenko
 */
public class PeriodicTableResize {
  @Execute
  void execute(MPart part) {
    PeriodicTablePart periodicTablePart = (PeriodicTablePart) part.getObject();

    Size currentSize = periodicTablePart.getPeriodicTableController().getTileSize();

    periodicTablePart.getPeriodicTableController().setTileSize(next(currentSize));
  }

  @AboutToShow
  void aboutToShow(List<MMenuElement> items, MPart part) {
    PeriodicTablePart periodicTablePart = (PeriodicTablePart) part.getObject();

    for (Size size : Arrays.asList(Size.values())) {
      MDirectMenuItem moduleItem = MMenuFactory.INSTANCE.createDirectMenuItem();
      moduleItem.setLabel(size.toString());
      moduleItem.setType(ItemType.RADIO);
      moduleItem.setSelected(periodicTablePart.getPeriodicTableController().getTileSize() == size);
      moduleItem.setObject(new Object() {
        @Execute
        public void execute() {
          if (moduleItem.isSelected()) {
            periodicTablePart.getPeriodicTableController().setTileSize(size);
          }
        }
      });

      items.add(moduleItem);
    }
  }
}
