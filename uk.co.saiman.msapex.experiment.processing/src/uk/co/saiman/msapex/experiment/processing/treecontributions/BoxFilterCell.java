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
 * This file is part of uk.co.saiman.msapex.experiment.processing.
 *
 * uk.co.saiman.msapex.experiment.processing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment.processing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment.processing.treecontributions;

import static uk.co.saiman.eclipse.ui.ListItems.ITEM_DATA;

import org.eclipse.e4.ui.di.AboutToShow;

import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.ui.ListItems;
import uk.co.saiman.eclipse.variable.NamedVariable;
import uk.co.saiman.experiment.processing.BoxFilter;
import uk.co.saiman.experiment.processing.ProcessingProperties;
import uk.co.saiman.property.Property;

public class BoxFilterCell {
  public static final String ID = "uk.co.saiman.experiment.processing.cell.boxfilter";

  @AboutToShow
  public void prepare(
      HBox node,
      @NamedVariable(ITEM_DATA) Property<BoxFilter> entry,
      ListItems children) {
    // TODO setSupplemental(node, Integer.toString(entry.get().getWidth()));

    children
        .addItem(
            Width.ID,
            entry.get().getWidth(),
            result -> entry.set(entry.get().withWidth(result)));
  }

  static class Width {
    public static final String ID = BoxFilterCell.ID + ".width";

    @AboutToShow
    void prepare(Cell cell, @Localize ProcessingProperties properties) {
      cell.setLabel(properties.widthLabel().get());
    }
  }
}
