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
 * This file is part of uk.co.saiman.eclipse.fx.
 *
 * uk.co.saiman.eclipse.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.fx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.ui.fx;

import java.util.function.Consumer;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.event.Event;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import uk.co.saiman.eclipse.model.ui.EditableCell;
import uk.co.saiman.eclipse.ui.SaiUiEvents;
import uk.co.saiman.eclipse.ui.SaiUiModel;

@Creatable
public class EditableCellText extends StackPane {
  private final Label label = new Label();
  private final TextField field = new TextField();
  private Consumer<String> update;

  @Inject
  EditableCell cell;

  public EditableCellText() {
    StackPane.setAlignment(label, Pos.CENTER_LEFT);
    getChildren().add(label);
    getChildren().add(field);
    field.setOnAction(event -> {
      cell.setEditing(false);
      event.consume();
    });
  }

  @Inject
  public void editing(@Optional @UIEventTopic(SaiUiEvents.EditableCell.TOPIC_EDITING) Event event) {
    if (event != null && event.getProperty(UIEvents.EventTags.ELEMENT) != cell) {
      return;
    }

    label.setVisible(!cell.isEditing());
    field.setVisible(cell.isEditing());

    if (cell.isEditing()) {
      field.requestFocus();

    } else {
      if (cell.getTags().contains(SaiUiModel.EDIT_CANCELED)) {
        field.setText(label.getText());

      } else {
        label.setText(field.getText());
        if (update != null) {
          update.accept(field.getText());
        }
      }
    }
  }

  public void setText(String name) {
    label.setText(name);
    field.setText(name);
  }

  public void setUpdate(Consumer<String> update) {
    this.update = update;
  }

  public Label getLabel() {
    return label;
  }

  public TextField getTextField() {
    return field;
  }
}
