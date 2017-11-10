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

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * A tree cell contribution intended to be supplied via {@link TreeContribution}
 * so as to be injected according to an eclipse context.
 * <p>
 * This contribution registers an E4 popup menu to the cell, which can be
 * activated via right click or the context menu key.
 * 
 * @author Elias N Vasylenko
 */
@Creatable
public class MenuContributor {
  @Inject
  EModelService modelService;

  @Inject
  EMenuService menuService;

  @Inject
  MPart part;

  @Inject
  MApplication application;

  private String menuId;
  private ContextMenu menu;

  protected ContextMenu createMenu(String menuId) {
    Control menuControl = new Control() {};

    MMenu menu = (MMenu) modelService.cloneSnippet(application, menuId, null);
    part.getMenus().add(menu);

    if (menuService.registerContextMenu(menuControl, menuId)) {
      ContextMenu contextMenu = menuControl.getContextMenu();
      contextMenu.addEventHandler(KeyEvent.ANY, Event::consume);
      return contextMenu;
    } else {
      throw new RuntimeException("Unable to register tree cell context menu " + menuId);
    }
  }

  public Node configureCell(String menuId, Node content) {
    ContextMenu menu;
    synchronized (this) {
      if (this.menuId != menuId) {
        menu = createMenu(menuId);

        this.menuId = menuId;
        this.menu = menu;
      } else {
        menu = this.menu;
      }
    }

    content.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
      menu.show(content, event.getScreenX(), event.getScreenY());
      event.consume();
    });
    content.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
      menu.hide();
    });

    return content;
  }
}
