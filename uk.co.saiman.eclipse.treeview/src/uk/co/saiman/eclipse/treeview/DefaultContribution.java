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
 * This file is part of uk.co.saiman.eclipse.treeview.
 *
 * uk.co.saiman.eclipse.treeview is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.treeview is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.treeview;

import static javafx.geometry.Pos.CENTER_LEFT;

import org.eclipse.e4.ui.di.AboutToShow;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * The default tree cell contribution. This configures a cell with basic text
 * content, which can be overridden using {@link #setLabel(HBox, String)} and
 * {@link #setSupplemental(HBox, String)}.
 * 
 * @author Elias N Vasylenko
 */
@Component(property = Constants.SERVICE_RANKING + ":Integer=" + Integer.MIN_VALUE)
public class DefaultContribution implements TreeContribution {
  public static final String TEXT_ID = "text";
  public static final String SUPPLEMENTAL_TEXT_ID = "supplementalText";

  @AboutToShow
  public void prepare(HBox node, TreeEntry<Object> entry) {
    Label text = new Label(entry.data().toString());
    text.setId(TEXT_ID);
    node.getChildren().add(text);
    HBox.setHgrow(text, Priority.ALWAYS);

    Label supplemental = new Label();
    supplemental.setId(SUPPLEMENTAL_TEXT_ID);
    node.getChildren().add(supplemental);
    HBox.setHgrow(supplemental, Priority.SOMETIMES);

    node.setPrefWidth(0);
    node.setAlignment(CENTER_LEFT);
  }

  public static void setLabel(HBox node, String text) {
    ((Label) node.lookup("#" + TEXT_ID)).setText(text);
  }

  public static void setSupplemental(HBox node, String text) {
    ((Label) node.lookup("#" + SUPPLEMENTAL_TEXT_ID)).setText(text);
  }
}
