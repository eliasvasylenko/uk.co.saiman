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

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * A tree cell contribution intended to be supplied via {@link TreeContribution}
 * so as to be injected according to an eclipse context.
 * <p>
 * This contribution registers an action to the cell, which can be activated via
 * double click or the enter key.
 * 
 * @author Elias N Vasylenko
 */
public interface ActionContributor extends Contributor {
  @Override
  default Node configureCell(Node content) {
    content.addEventHandler(MouseEvent.ANY, event -> {
      if (event.getClickCount() == 2
          && event.getButton().equals(MouseButton.PRIMARY)
          && event.getEventType().equals(MouseEvent.MOUSE_PRESSED)
          && performAction(content)) {
        event.consume();
      }
    });

    content.addEventHandler(KeyEvent.ANY, event -> {
      if (event.getCode() == KeyCode.ENTER) {
        if (event.getEventType().equals(KeyEvent.KEY_PRESSED) && performAction(content)) {
          event.consume();
        }
      }
    });

    return content;
  }

  boolean performAction(Node content);
}
