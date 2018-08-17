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
 * This file is part of uk.co.saiman.msapex.experiment.spectrum.
 *
 * uk.co.saiman.msapex.experiment.spectrum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment.spectrum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment.spectrum;

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.eclipse.ui.ListItems.ITEM_DATA;
import static uk.co.saiman.eclipse.ui.fx.TreeService.setLabel;
import static uk.co.saiman.eclipse.ui.fx.TreeService.setSupplemental;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.eclipse.e4.ui.di.AboutToShow;

import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.adapter.AdaptNamed;
import uk.co.saiman.eclipse.ui.ListItems;
import uk.co.saiman.eclipse.variable.NamedVariable;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.processing.Processor;
import uk.co.saiman.experiment.spectrum.SpectrumResultConfiguration;
import uk.co.saiman.property.Property;

public class SpectrumProcessingExperimentNodeCell {
  public static final String ID = "uk.co.saiman.experiment.spectrum.cell.processing";

  @AboutToShow
  public void prepare(
      HBox node,
      @Named(ITEM_DATA) ExperimentNode<?, ?> data,
      @AdaptNamed(ITEM_DATA) SpectrumResultConfiguration state,
      ListItems children) {
    setLabel(node, data.getType().getName());
    setSupplemental(node, state.getSpectrumName());

    children.addItems(Processors.ID, state.getProcessing().collect(toList()));
  }

  static class Processors {
    public static final String ID = SpectrumProcessingExperimentNodeCell.ID + ".processors";

    @AboutToShow
    public void prepare(
        @NamedVariable(ITEM_DATA) Property<List<Processor<?>>> entry,
        ListItems children) {

      children.addItems(ID, entry.get(), r -> entry.set(new ArrayList<>(r)));
    }
  }
}
