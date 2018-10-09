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
import static java.util.stream.Stream.concat;
import static javafx.css.PseudoClass.getPseudoClass;
import static uk.co.saiman.collection.StreamUtilities.streamNullable;

import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;

import javafx.css.PseudoClass;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import uk.co.saiman.collection.StreamUtilities;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.ui.ChildrenService;
import uk.co.saiman.experiment.AttachNodeEvent;
import uk.co.saiman.experiment.DetachNodeEvent;
import uk.co.saiman.experiment.ExperimentLifecycleEvent;
import uk.co.saiman.experiment.ExperimentLifecycleState;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.RenameNodeEvent;
import uk.co.saiman.msapex.editor.Editor;
import uk.co.saiman.msapex.editor.EditorService;
import uk.co.saiman.msapex.experiment.i18n.ExperimentProperties;

/**
 * Contribution for all experiment nodes in the experiment tree
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentNodeCell {
  public static final String ID = "uk.co.saiman.msapex.experiment.cell.node";
  public static final String SUPPLEMENTAL_TEXT = ID + ".supplemental";
  public static final PseudoClass SUPPLEMENTAL_PSEUDO_CLASS = getPseudoClass(
      SUPPLEMENTAL_TEXT.replace('.', '-'));

  @Inject
  @Localize
  private ExperimentProperties text;

  @Inject
  private EditorService editorService;

  @Inject
  private IEclipseContext context;

  @Inject
  private ExperimentNode<?, ?> experiment;

  private Label supplementalText = new Label();
  private Label lifecycleIndicator = new Label();

  @Inject
  private Cell cell;

  @Inject
  private ChildrenService children;

  @CanExecute
  public boolean canExecute() {
    return editorService.getApplicableEditors(experiment).findFirst().isPresent();
  }

  @Execute
  public void execute() {
    editorService.getApplicableEditors(experiment).findFirst().ifPresent(Editor::openPart);
  }

  @PostConstruct
  public void prepare(HBox node) {
    /*
     * configure label
     */
    cell.setLabel(experiment.getId());
    node.getChildren().add(supplementalText);
    supplementalText.pseudoClassStateChanged(SUPPLEMENTAL_PSEUDO_CLASS, true);
    context.set(SUPPLEMENTAL_TEXT, supplementalText);

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
     * Inject configuration
     */
    IContextFunction configurationFunction = (c, k) -> c
        .get(IAdapterManager.class)
        .getAdapter(c.get(ExperimentNode.class), k);
    StreamUtilities
        .<Class<?>>flatMapRecursive(
            experiment.getVariables().getClass(),
            t -> concat(streamNullable(t.getSuperclass()), Stream.of(t.getInterfaces())))
        .forEach(type -> context.set(type.getName(), configurationFunction));

    /*
     * Inject events
     */
    context.set(ExperimentLifecycleState.class, experiment.getLifecycleState());

    /*
     * Children
     */
    updateChildren();
  }

  @Inject
  @Optional
  public void update(RenameNodeEvent event) {
    if (event.node() == experiment) {
      cell.setLabel(event.id());
    }
  }

  @Inject
  @Optional
  public void update(ExperimentLifecycleEvent event) {
    if (event.node() == experiment) {
      context.set(ExperimentLifecycleState.class, event.lifecycleState());
    }
  }

  @Inject
  @Optional
  public void update(AttachNodeEvent event) {
    if (event.parent().filter(experiment::equals).isPresent()) {
      updateChildren();
    }
  }

  @Inject
  @Optional
  public void update(DetachNodeEvent event) {
    if (event.previousParent().filter(experiment::equals).isPresent()) {
      updateChildren();
    }
  }

  private void updateChildren() {
    children
        .setItems(
            ExperimentNodeCell.ID,
            ExperimentNode.class,
            experiment.getChildren().collect(toList()));
  }

  @Inject
  @Optional
  public void updateLifecycle(ExperimentLifecycleState state) {
    allOf(ExperimentLifecycleState.class)
        .stream()
        .forEach(
            s -> lifecycleIndicator.pseudoClassStateChanged(getPseudoClass(s.toString()), false));
    lifecycleIndicator.pseudoClassStateChanged(getPseudoClass(state.toString()), true);
    supplementalText.setText("[" + text.lifecycleState(state) + "]");
  }
}
