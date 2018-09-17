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
package uk.co.saiman.msapex.experiment;

import static java.util.EnumSet.allOf;
import static java.util.stream.Collectors.toList;
import static javafx.css.PseudoClass.getPseudoClass;
import static uk.co.saiman.experiment.WorkspaceEventState.COMPLETED;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.ui.ChildrenService;
import uk.co.saiman.experiment.ExperimentLifecycleState;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.msapex.editor.Editor;
import uk.co.saiman.msapex.editor.EditorService;

/**
 * Contribution for all experiment nodes in the experiment tree
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentNodeCell {
  public static final String ID = "uk.co.saiman.msapex.experiment.cell.node";

  @Inject
  private Workspace workspace;

  @Inject
  @Localize
  private ExperimentProperties text;

  @Inject
  private EditorService editorService;

  @Inject
  private ExperimentNode<?, ?> experiment;

  private Label supplementalText = new Label();
  private Label lifecycleIndicator = new Label();

  @Execute
  public void execute(ExperimentNode<?, ?> experiment) {
    editorService.getApplicableEditors(experiment).findFirst().map(Editor::openPart).isPresent();
  }

  @PostConstruct
  public void prepare(HBox node, Cell cell) {
    /*
     * configure label
     */
    cell.setLabel(experiment.getId());
    node.getChildren().add(supplementalText);

    /*
     * add spacer
     */
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.SOMETIMES);
    node.getChildren().add(spacer);

    /*
     * label to show lifecycle state icon
     */

    node.getChildren().add(lifecycleIndicator);

    /*
     * Observe lifecycle
     */

    experiment
        .lifecycleState()
        .weakReference(this)
        .observe(m -> m.owner().updateStyle(m.message()));
  }

  public void updateStyle(ExperimentLifecycleState state) {
    allOf(ExperimentLifecycleState.class)
        .stream()
        .forEach(
            s -> lifecycleIndicator.pseudoClassStateChanged(getPseudoClass(s.toString()), false));
    lifecycleIndicator.pseudoClassStateChanged(getPseudoClass(state.toString()), true);
    supplementalText
        .setText(experiment.getType().getName() + " [" + text.lifecycleState(state) + "]");
  }

  @Inject
  public void children(ChildrenService children) {
    workspace
        .events(COMPLETED)
        .filter(e -> e.getNode().getParent().filter(experiment::equals).isPresent())
        .take(1)
        .observe(m -> {
          children.invalidate();
        });

    /*
     * add children
     */
    children
        .setItems(
            ExperimentNodeCell.ID,
            ExperimentNode.class,
            experiment.getChildren().collect(toList()));
  }
}
