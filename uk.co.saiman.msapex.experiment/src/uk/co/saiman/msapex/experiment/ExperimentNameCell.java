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

import static uk.co.saiman.msapex.experiment.ExperimentNodeCell.SUPPLEMENTAL_PSEUDO_CLASS;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.event.Event;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.ui.SaiUiEvents;
import uk.co.saiman.eclipse.ui.fx.EditableCellText;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.RenameNodeEvent;

/**
 * Contribution for root experiment nodes in the experiment tree
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentNameCell {
  @Inject
  Cell cell;
  Cell parent;

  @Inject
  ExperimentConfiguration configuration;

  @Inject
  EditableCellText nameEditor;

  @Inject
  ExperimentNode<?, ?> experiment;

  @PostConstruct
  public void prepare(HBox node, @Named(ExperimentNodeCell.SUPPLEMENTAL_TEXT) Label supplemental) {
    parent = (Cell) (MUIElement) cell.getParent();

    parent.setLabel(configuration.getName());
    setIcon();

    node.getChildren().add(nameEditor);
    HBox.setHgrow(nameEditor, Priority.SOMETIMES);

    nameEditor.setText(configuration.getName());
    nameEditor.setUpdate(name -> configuration.setName(name));
    nameEditor.getLabel().pseudoClassStateChanged(SUPPLEMENTAL_PSEUDO_CLASS, true);
  }

  @Inject
  @Optional
  public void updateName(RenameNodeEvent event) {
    if (event.node() == experiment) {
      nameEditor.setText(event.id());
    }
  }

  @Optional
  @Inject
  public void expanded(@UIEventTopic(SaiUiEvents.Cell.TOPIC_EXPANDED) Event expanded) {
    if (expanded.getProperty(UIEvents.EventTags.ELEMENT) == parent) {
      setIcon();
    }
  }

  private void setIcon() {
    parent
        .setIconURI(
            parent.isExpanded()
                ? "platform:/plugin/uk.co.saiman.icons.fugue/uk/co/saiman/icons/fugue/size16/book-open.png"
                : "platform:/plugin/uk.co.saiman.icons.fugue/uk/co/saiman/icons/fugue/size16/book.png");
  }
}
