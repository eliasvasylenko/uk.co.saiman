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

import static uk.co.saiman.eclipse.treeview.DefaultContribution.setLabel;
import static uk.co.saiman.eclipse.treeview.DefaultContribution.setSupplemental;

import java.util.Arrays;

import org.eclipse.e4.ui.di.AboutToShow;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.eclipse.treeview.Contributor;
import uk.co.saiman.eclipse.treeview.PseudoClassContributor;
import uk.co.saiman.eclipse.treeview.TreeContribution;
import uk.co.saiman.eclipse.treeview.TreeEntry;
import uk.co.saiman.eclipse.treeview.TreeEntryChild;
import uk.co.saiman.eclipse.treeview.TreeEntryChildren;
import uk.co.saiman.experiment.processing.Convolution;
import uk.co.saiman.experiment.processing.ProcessingProperties;

@Component(scope = ServiceScope.PROTOTYPE)
public class ConvolutionContribution implements TreeContribution {
  private final Contributor pseudoClass = new PseudoClassContributor(getClass().getSimpleName());

  @AboutToShow
  public void prepare(HBox node, TreeEntry<Convolution> entry, TreeEntryChildren children) {
    setSupplemental(node, Arrays.toString(entry.data().getConvolutionVector()));

    children
        .add(
            TreeEntryChild
                .withType(int.class)
                .withGetter(() -> entry.data().getConvolutionVectorCentre())
                .withSetter(
                    result -> entry.update(data -> data.withConvolutionVectorCentre(result)))
                .withContribution(new TreeContribution() {
                  @AboutToShow
                  public void prepare(HBox vectorNode, @Localize ProcessingProperties properties) {
                    setLabel(vectorNode, properties.vectorLabel().get());
                  }
                })
                .build());

    children
        .add(
            TreeEntryChild
                .withType(double[].class)
                .withGetter(() -> entry.data().getConvolutionVector())
                .withSetter(result -> entry.update(data -> data.withConvolutionVector(result)))
                .withContribution(new TreeContribution() {
                  @AboutToShow
                  public void prepare(HBox vectorNode, @Localize ProcessingProperties properties) {
                    setLabel(vectorNode, properties.vectorLabel().get());
                  }
                })
                .build());

    pseudoClass.configureCell(node);
  }
}
