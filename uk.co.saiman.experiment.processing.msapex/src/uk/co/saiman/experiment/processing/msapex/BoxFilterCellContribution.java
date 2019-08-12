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
 * This file is part of uk.co.saiman.experiment.processing.msapex.
 *
 * uk.co.saiman.experiment.processing.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.processing.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.processing.msapex;

import org.eclipse.e4.core.di.extensions.Service;
import org.eclipse.e4.ui.di.AboutToShow;

import javafx.scene.layout.HBox;
import uk.co.saiman.data.function.processing.BoxFilter;
import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.ui.ChildrenService;
import uk.co.saiman.experiment.processing.msapex.i18n.ProcessingProperties;
import uk.co.saiman.property.Property;

public class BoxFilterCellContribution {
  public static final String ID = "uk.co.saiman.experiment.processing.cell.boxfilter";

  @AboutToShow
  public void prepare(HBox node, Property<BoxFilter> entry, ChildrenService children) {
    // TODO setSupplemental(node, Integer.toString(entry.get().getWidth()));

    children
        .setItem(
            Width.ID,
            int.class,
            entry.get().getWidth(),
            result -> entry.set(entry.get().withWidth(result)));
  }

  public static class Width {
    public static final String ID = BoxFilterCellContribution.ID + ".width";

    @AboutToShow
    void prepare(MCell cell, int value, @Service ProcessingProperties properties) {
      cell.setLabel(properties.widthLabel().get());
    }
  }
}
