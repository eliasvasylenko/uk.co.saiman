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

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static uk.co.saiman.eclipse.treeview.DefaultContribution.setLabel;
import static uk.co.saiman.eclipse.treeview.DefaultContribution.setSupplemental;

import java.util.Arrays;

import org.eclipse.e4.ui.di.AboutToShow;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.eclipse.treeview.Contributor;
import uk.co.saiman.eclipse.treeview.EditingTextField;
import uk.co.saiman.eclipse.treeview.PseudoClassContributor;
import uk.co.saiman.eclipse.treeview.TreeChildren;
import uk.co.saiman.eclipse.treeview.TreeContribution;
import uk.co.saiman.eclipse.treeview.TreeEditor;
import uk.co.saiman.eclipse.treeview.TreeEntry;
import uk.co.saiman.experiment.processing.Convolution;
import uk.co.saiman.experiment.processing.ProcessingProperties;

@Component(scope = ServiceScope.PROTOTYPE)
public class ConvolutionContribution implements TreeContribution {
  private final Contributor pseudoClass = new PseudoClassContributor(getClass().getSimpleName());

  @AboutToShow
  public void prepare(HBox node, TreeEntry<Convolution.State> entry, TreeChildren children) {
    setSupplemental(node, Arrays.toString(entry.data().getConvolutionVector()));

    children.addChild(new TreeContribution() {
      @AboutToShow
      public void prepare(
          HBox centreNode,
          TreeEditor<TreeContribution> editor,
          @Localize ProcessingProperties properties) {
        setLabel(centreNode, properties.centreLabel().get());

        String centreString = Integer.toString(entry.data().getConvolutionVectorCentre());

        if (editor.isEditing()) {
          TextField widthField = new EditingTextField(
              centreString,
              editor,
              centreNode.getChildren()::add);

          editor
              .addEditListener(
                  () -> entry
                      .data()
                      .setConvolutionVector(
                          entry.data().getConvolutionVector(),
                          Integer.parseInt(widthField.getText())));
        } else {
          setSupplemental(centreNode, centreString);
        }
      }
    });

    children.addChild(new TreeContribution() {
      @AboutToShow
      public void prepare(
          HBox vectorNode,
          TreeEditor<TreeContribution> editor,
          @Localize ProcessingProperties properties) {
        setLabel(vectorNode, properties.vectorLabel().get());

        String vectorString = Arrays
            .stream(entry.data().getConvolutionVector())
            .mapToObj(d -> Double.toString(d))
            .collect(joining(", "));

        if (editor.isEditing()) {
          TextField vectorField = new EditingTextField(
              vectorString,
              editor,
              vectorNode.getChildren()::add);

          editor
              .addEditListener(
                  () -> entry
                      .data()
                      .setConvolutionVector(
                          stream(vectorField.getText().split(","))
                              .mapToDouble(Double::parseDouble)
                              .toArray(),
                          entry.data().getConvolutionVectorCentre()));
        } else {
          setSupplemental(vectorNode, vectorString);
        }
      }
    });

    pseudoClass.configureCell(node);
  }
}
