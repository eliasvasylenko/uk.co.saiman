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
package uk.co.saiman.msapex.experiment.treecontributions;

import static uk.co.saiman.eclipse.treeview.DefaultTreeCellContribution.setLabel;
import static uk.co.saiman.eclipse.treeview.DefaultTreeCellContribution.setSupplemental;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.ui.di.AboutToShow;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import uk.co.saiman.eclipse.Localize;
import uk.co.saiman.eclipse.treeview.MenuContributor;
import uk.co.saiman.eclipse.treeview.TreeContribution;
import uk.co.saiman.eclipse.treeview.TreeEntry;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.Result;
import uk.co.saiman.reflection.token.TypedReference;

/**
 * Contribution for all experiment nodes in the experiment tree
 * 
 * @author Elias N Vasylenko
 */
@Component(
    scope = ServiceScope.PROTOTYPE,
    property = Constants.SERVICE_RANKING + ":Integer=" + -100)
public class ExperimentNodeContribution implements TreeContribution {
  private static final String EXPERIMENT_TREE_POPUP_MENU = "uk.co.saiman.msapex.experiment.popupmenu.node";

  @Inject
  MenuContributor menuContributor;

  @Inject
  @Localize
  ExperimentProperties text;

  @AboutToShow
  public void prepare(
      HBox node,
      List<TypedReference<?>> children,
      TreeEntry<ExperimentNode<?, ?>> entry) {
    /*
     * configure label
     */
    setLabel(node, entry.data().getID());
    setSupplemental(
        node,
        entry.data().getType().getName() + " ["
            + text.lifecycleState(entry.data().lifecycleState().get()) + "]");

    /*
     * add spacer
     */
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    node.getChildren().add(spacer);

    /*
     * label to show lifecycle state icon
     */
    entry.data().lifecycleState().changes().weakReference(entry).observe(
        m -> m.owner().refresh(false));
    Label lifecycleIndicator = new Label();
    configurePseudoClass(lifecycleIndicator, entry.data().lifecycleState().get().toString());
    node.getChildren().add(lifecycleIndicator);

    /*
     * add popup menu
     */
    menuContributor.configureCell(EXPERIMENT_TREE_POPUP_MENU, node);

    /*
     * add children
     */
    entry.data().getChildren().map(ExperimentNode::asTypedObject).forEach(children::add);
    entry.data().getResults().map(Result::asTypedObject).forEach(children::add);

    /*
     * pseudo class
     */
    configurePseudoClass(node);
  }
}
