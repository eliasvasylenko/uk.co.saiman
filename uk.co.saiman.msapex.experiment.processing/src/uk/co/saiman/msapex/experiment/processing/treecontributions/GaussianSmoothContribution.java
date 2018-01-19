/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.msapex.experiment.
 *
 * uk.co.saiman.msapex.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment.processing.treecontributions;

import static java.lang.Double.parseDouble;
import static uk.co.saiman.eclipse.treeview.DefaultContribution.setLabel;
import static uk.co.saiman.eclipse.treeview.DefaultContribution.setSupplemental;

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
import uk.co.saiman.experiment.processing.GaussianSmooth;
import uk.co.saiman.experiment.processing.ProcessingProperties;

@Component(scope = ServiceScope.PROTOTYPE)
public class GaussianSmoothContribution implements TreeContribution {
  private final Contributor pseudoClass = new PseudoClassContributor(getClass().getSimpleName());

  @AboutToShow
  public void prepare(
      HBox node,
      TreeEntry<GaussianSmooth.State> entry,
      TreeChildren children,
      @Localize ProcessingProperties properties) {
    setSupplemental(node, "(" + entry.data().getStandardDeviation() + ")");

    children.addChild(new TreeContribution() {
      @AboutToShow
      void prepare(HBox deviationNode, TreeEditor<TreeContribution> editor) {
        setLabel(deviationNode, properties.standardDeviationLabel().get());

        String deviationString = Double.toString(entry.data().getStandardDeviation());

        if (editor.isEditing()) {
          TextField deviationField = new EditingTextField(
              deviationString,
              editor,
              deviationNode.getChildren()::add);

          editor
              .addEditListener(
                  () -> entry.data().setStandardDeviation(parseDouble(deviationField.getText())));
        } else {
          setSupplemental(deviationNode, deviationString);
        }
      }
    });

    pseudoClass.configureCell(node);
  }
}
