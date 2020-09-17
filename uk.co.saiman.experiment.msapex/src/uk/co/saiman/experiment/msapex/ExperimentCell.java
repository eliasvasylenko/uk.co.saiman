/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.msapex.
 *
 * uk.co.saiman.experiment.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.msapex;

import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Service;
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
import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.ui.Children;
import uk.co.saiman.eclipse.ui.CompleteTransfer;
import uk.co.saiman.eclipse.ui.SaiUiEvents;
import uk.co.saiman.eclipse.ui.TransferSink;
import uk.co.saiman.eclipse.utilities.ContextBuffer;
import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.definition.ExperimentDefinition;
import uk.co.saiman.experiment.event.ExperimentEvent;
import uk.co.saiman.experiment.msapex.i18n.ExperimentProperties;
import uk.co.saiman.experiment.msapex.workspace.WorkspaceExperiment;
import uk.co.saiman.experiment.msapex.workspace.WorkspaceExperiment.Status;
import uk.co.saiman.experiment.msapex.workspace.event.WorkspaceEvent;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

/**
 * Contribution for all experiment nodes in the experiment tree
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentCell {
  public static final String ID = "uk.co.saiman.experiment.cell";

  @Inject
  Log log;
  @Inject
  @Service
  ExperimentProperties text;
  @Inject
  IEclipseContext context;
  @Inject
  WorkspaceExperiment experiment;

  Label supplementalText = new Label();
  Label lifecycleIndicator = new Label();

  @Inject
  MCell cell;

  @PostConstruct
  public void prepare(HBox node) {
    /*
     * configure label
     */
    cell.setLabel(experiment.id().name());
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

  @CompleteTransfer
  public TransferSink completeTransfer() {
    System.out.println(" complete transfer at " + experiment);
    return new TransferSink();
  }

  private void open() {
    context.set(Experiment.class, experiment.open());
    context.set(ExperimentDefinition.class, experiment.open().getDefinition());
    context.set(ExperimentPath.class, ExperimentPath.toRoot());
    updateIcon();
  }

  private void close() {
    context.remove(Experiment.class);
    context.remove(ExperimentDefinition.class);
    context.remove(ExperimentPath.class);
    updateIcon();
  }

  @Inject
  @Optional
  public void update(ExperimentEvent event) {
    if (experiment.status() == Status.OPEN && event.experiment() == experiment.open()) {
      cell.setLabel(event.experimentDefinition().id().name());
    }
  }

  @Children(snippetId = ExperimentStepCell.ID)
  private Stream<ContextBuffer> updateChildren(
      @Optional WorkspaceEvent workspaceEvent,
      @Optional ExperimentEvent experimentEvent) {
    if ((experiment.status() != Status.OPEN
        || experimentEvent != null && experimentEvent.experiment() != experiment.open())
        || (workspaceEvent != null && workspaceEvent.experiment() != experiment)) {
      return null;
    }

    switch (experiment.status()) {
    case OPEN:
      return experiment
          .open()
          .getIndependentSteps()
          .map(step -> ContextBuffer.empty().set(Step.class, step));
    default:
      return Stream.empty();
    }
  }

  @Optional
  @Inject
  public void expanded(@UIEventTopic(SaiUiEvents.Cell.TOPIC_EXPANDED) Event expanded) {
    if (expanded.getProperty(UIEvents.EventTags.ELEMENT) == cell) {
      updateIcon();
    }
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

  private void updateIcon() {
    try {
      cell
          .setIconURI(
              (experiment.status() == Status.CLOSED)
                  ? "fugue:size16/book-small.png"
                  : cell.isExpanded() ? "fugue:size16/book-open.png" : "fugue:size16/book.png");
    } catch (Exception e) {
      log.log(Level.ERROR, "Problem updating icon", e);
    }
  }
}
