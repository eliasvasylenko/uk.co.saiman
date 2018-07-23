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
import static uk.co.saiman.eclipse.treeview.DefaultContribution.setLabel;
import static uk.co.saiman.eclipse.treeview.DefaultContribution.setSupplemental;

import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceRanking;

import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.treeview.Contributor;
import uk.co.saiman.eclipse.treeview.PseudoClassContributor;
import uk.co.saiman.eclipse.treeview.TreeContribution;
import uk.co.saiman.eclipse.treeview.TreeEntry;
import uk.co.saiman.eclipse.treeview.TreeEntryChild;
import uk.co.saiman.eclipse.treeview.TreeEntryChildren;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.processing.Processor;
import uk.co.saiman.experiment.spectrum.SpectrumResultConfiguration;
import uk.co.saiman.reflection.token.TypeToken;

@ServiceRanking(10)
@Component
public class SpectrumProcessingExperimentNodeContribution implements TreeContribution {
  private final Contributor pseudoClass = new PseudoClassContributor(getClass().getSimpleName());

  @AboutToShow
  public void prepare(
      HBox node,
      TreeEntry<ExperimentNode<? extends SpectrumResultConfiguration, ?>> data,
      TreeEntryChildren children) {
    setLabel(node, data.data().getType().getName());
    setSupplemental(node, data.data().getState().getSpectrumName());

    children
        .add(
            0,
            TreeEntryChild
                .withType(new TypeToken<List<Processor<?>>>() {})
                .withValue(data.data().getState().getProcessing().collect(toList()))
                .build());

    pseudoClass.configureCell(node);
  }
}
