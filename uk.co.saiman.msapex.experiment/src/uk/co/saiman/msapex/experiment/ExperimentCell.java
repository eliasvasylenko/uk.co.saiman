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

import static java.util.Collections.emptyList;
import static java.util.EnumSet.allOf;
import static java.util.stream.Collectors.toList;
import static javafx.css.PseudoClass.getPseudoClass;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.event.Event;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import uk.co.saiman.eclipse.dialog.DialogUtilities;
import uk.co.saiman.eclipse.localization.Localize;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.ui.ChildrenService;
import uk.co.saiman.eclipse.ui.SaiUiEvents;
import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentLifecycleState;
import uk.co.saiman.experiment.ExperimentStep;
import uk.co.saiman.experiment.event.AttachStepEvent;
import uk.co.saiman.experiment.event.DetachStepEvent;
import uk.co.saiman.experiment.event.ExperimentLifecycleEvent;
import uk.co.saiman.experiment.event.RenameStepEvent;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.msapex.experiment.i18n.ExperimentProperties;
import uk.co.saiman.msapex.experiment.workspace.WorkspaceExperiment;
import uk.co.saiman.msapex.experiment.workspace.WorkspaceExperiment.Status;
import uk.co.saiman.msapex.experiment.workspace.event.WorkspaceEvent;

/**
 * Contribution for all experiment nodes in the experiment tree
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentCell {
  public static final String ID = "uk.co.saiman.msapex.experiment.cell";

  @Inject
  Log log;
  @Inject
  @Localize
  ExperimentProperties text;
  @Inject
  IEclipseContext context;
  @Inject
  WorkspaceExperiment experiment;

  Label supplementalText = new Label();
  Label lifecycleIndicator = new Label();

  @Inject
  Cell cell;
  @Inject
  ChildrenService children;

  @Optional
  @Inject
  public void execute(@UIEventTopic(SaiUiEvents.Cell.TOPIC_EXPANDED) @Optional Event expanded) {
    if (cell.isExpanded()) {
      try {
        experiment.open();

      } catch (Exception e) {
        log.log(Level.ERROR, e);

        Alert alert = new Alert(AlertType.ERROR);
        DialogUtilities.addStackTrace(alert, e);
        alert.setTitle(text.openExperimentFailedDialog().toString());
        alert.setHeaderText(text.openExperimentFailedText(experiment).toString());
        alert.setContentText(text.openExperimentFailedDescription().toString());
        alert.showAndWait();
      }
    }
  }

  @PostConstruct
  public void prepare(HBox node) {
    /*
     * configure label
     */
    cell.setLabel(experiment.name());
    node.getChildren().add(supplementalText);

    /*
     * label to show lifecycle state icon
     */
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.SOMETIMES);
    node.getChildren().add(spacer);
    node.getChildren().add(lifecycleIndicator);

    /*
     * Children
     */
    if (experiment.status() == Status.OPEN) {
      open();
    } else {
      updateIcon();
    }
  }

  @Inject
  @Optional
  public void update(WorkspaceEvent event) {
    if (event.experiment() == experiment) {
      switch (event.kind()) {
      case OPEN:
        open();
        break;
      case CLOSE:
        close();
        break;
      default:
      }
    }
  }

  private void open() {
    context.set(Experiment.class, experiment.experiment());
    context.set(ExperimentStep.class, experiment.experiment());
    context.set(ExperimentConfiguration.class, experiment.experiment().getVariables());
    context.set(ExperimentLifecycleState.class, experiment.experiment().getLifecycleState());
    updateIcon();
    updateChildren();
  }

  private void close() {
    try {
      removeChildren();
      context.remove(Experiment.class);
      context.remove(ExperimentStep.class);
      context.remove(ExperimentConfiguration.class);
      context.remove(ExperimentLifecycleState.class);
      updateIcon();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Inject
  @Optional
  public void update(RenameStepEvent event) {
    if (experiment.status() == Status.OPEN && event.node() == experiment.experiment()) {
      cell.setLabel(event.id());
    }
  }

  @Inject
  @Optional
  public void update(ExperimentLifecycleEvent event) {
    if (experiment.status() == Status.OPEN && event.node() == experiment.experiment()) {
      context.set(ExperimentLifecycleState.class, event.lifecycleState());
    }
  }

  @Inject
  @Optional
  public void update(AttachStepEvent event) {
    if (experiment.status() == Status.OPEN && event.parent() == experiment.experiment()) {
      updateChildren();
    }
  }

  @Inject
  @Optional
  public void update(DetachStepEvent event) {
    if (experiment.status() == Status.OPEN && event.previousParent() == experiment.experiment()) {
      updateChildren();
    }
  }

  private void removeChildren() {
    children.setItems(ExperimentNodeCell.ID, ExperimentStep.class, emptyList());
  }

  private void updateChildren() {
    children
        .setItems(
            ExperimentNodeCell.ID,
            ExperimentStep.class,
            experiment.experiment().getChildren().collect(toList()));
  }

  @Inject
  public void updateLifecycle(@Optional ExperimentLifecycleState state) {
    allOf(ExperimentLifecycleState.class)
        .stream()
        .forEach(
            s -> lifecycleIndicator.pseudoClassStateChanged(getPseudoClass(s.toString()), false));
    if (state == null || state == ExperimentLifecycleState.DETACHED) {
      supplementalText.setText(null);
    } else {
      lifecycleIndicator.pseudoClassStateChanged(getPseudoClass(state.toString()), true);
      supplementalText.setText("[" + text.lifecycleState(state) + "]");
    }
  }

  @Optional
  @Inject
  public void expanded(@UIEventTopic(SaiUiEvents.Cell.TOPIC_EXPANDED) Event expanded) {
    if (expanded.getProperty(UIEvents.EventTags.ELEMENT) == cell) {
      updateIcon();
    }
  }

  private void updateIcon() {
    cell
        .setIconURI(
            (experiment.status() == Status.CLOSED)
                ? "platform:/plugin/uk.co.saiman.icons.fugue/uk/co/saiman/icons/fugue/size16/book-small.png"
                : cell.isExpanded()
                    ? "platform:/plugin/uk.co.saiman.icons.fugue/uk/co/saiman/icons/fugue/size16/book-open.png"
                    : "platform:/plugin/uk.co.saiman.icons.fugue/uk/co/saiman/icons/fugue/size16/book.png");
  }
}
