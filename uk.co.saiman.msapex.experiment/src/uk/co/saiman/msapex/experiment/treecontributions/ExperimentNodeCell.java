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
package uk.co.saiman.msapex.experiment.treecontributions;

import static java.util.stream.Collectors.toList;
import static javafx.css.PseudoClass.getPseudoClass;
import static uk.co.saiman.eclipse.ui.ListItems.ITEM_DATA;
import static uk.co.saiman.eclipse.ui.fx.TreeService.setLabel;
import static uk.co.saiman.eclipse.ui.fx.TreeService.setSupplemental;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.AboutToShow;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.eclipse.ui.ListItems;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.msapex.editor.Editor;
import uk.co.saiman.msapex.editor.EditorService;

/**
 * Contribution for all experiment nodes in the experiment tree
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentNodeCell {
  public static final String ID = "uk.co.saiman.experiment.cell.node";

  private static final String EXPERIMENT_TREE_POPUP_MENU = "uk.co.saiman.msapex.experiment.popupmenu.node";

  @Inject
  @Localize
  ExperimentProperties text;

  @Inject
  EditorService editorService;

  @Execute
  public void execute(@Named(ITEM_DATA) ExperimentNode<?, ?> experiment) {
    editorService.getApplicableEditors(experiment).findFirst().map(Editor::openPart).isPresent();
  }

  @AboutToShow
  public void prepare(
      HBox node,
      ListItems children,
      @Named(ITEM_DATA) ExperimentNode<?, ?> experiment) {
    /*
     * configure label
     */
    setLabel(node, experiment.getId());
    setSupplemental(
        node,
        experiment.getType().getName()
            + " ["
            + text.lifecycleState(experiment.lifecycleState().get())
            + "]");

    /*
     * add spacer
     */
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.SOMETIMES);
    node.getChildren().add(spacer);

    /*
     * label to show lifecycle state icon
     */

    Label lifecycleIndicator = new Label();
    node.getChildren().add(lifecycleIndicator);

    experiment
        .lifecycleState()
        .weakReference(lifecycleIndicator)
        .observe(m -> m.owner().pseudoClassStateChanged(getPseudoClass(m.toString()), true));

    /*
     * add children
     */
    children.addItems(ExperimentNodeCell.ID, experiment.getChildren().collect(toList()));
  }
}
