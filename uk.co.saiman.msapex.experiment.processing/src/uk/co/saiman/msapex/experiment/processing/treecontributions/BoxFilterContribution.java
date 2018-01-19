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
import uk.co.saiman.experiment.processing.BoxFilter;
import uk.co.saiman.experiment.processing.ProcessingProperties;

@Component(scope = ServiceScope.PROTOTYPE)
public class BoxFilterContribution implements TreeContribution {
  private final Contributor pseudoClass = new PseudoClassContributor(getClass().getSimpleName());

  @AboutToShow
  public void prepare(HBox node, TreeEntry<BoxFilter.State> entry, TreeChildren children) {
    setSupplemental(node, "(" + entry.data().getWidth() + ")");

    children.addChild(new TreeContribution() {
      @AboutToShow
      public void prepare(
          HBox widthNode,
          TreeEditor<TreeContribution> editor,
          @Localize ProcessingProperties properties) {
        setLabel(widthNode, properties.widthLabel().get());

        String widthString = Integer.toString(entry.data().getWidth());

        if (editor.isEditing()) {
          TextField widthField = new EditingTextField(
              widthString,
              editor,
              widthNode.getChildren()::add);

          editor
              .addEditListener(() -> entry.data().setWidth(Integer.parseInt(widthField.getText())));
        } else {
          setSupplemental(widthNode, widthString);
        }
      }
    });

    pseudoClass.configureCell(node);
  }
}
