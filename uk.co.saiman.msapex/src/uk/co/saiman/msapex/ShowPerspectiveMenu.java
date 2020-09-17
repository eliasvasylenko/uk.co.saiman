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
 * This file is part of uk.co.saiman.msapex.
 *
 * uk.co.saiman.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex;

import static uk.co.saiman.eclipse.perspective.command.PerspectiveCommandConstants.PERSPECTIVE_ID_PARAMETER;
import static uk.co.saiman.eclipse.perspective.command.PerspectiveCommandConstants.PERSPECTIVE_STACK_ID_PARAMETER;
import static uk.co.saiman.eclipse.perspective.command.PerspectiveCommandConstants.SHOW_COMMAND_ID;

import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import uk.co.saiman.eclipse.perspective.EPerspectiveService;
import uk.co.saiman.eclipse.perspective.IPerspectiveContainer;

public class ShowPerspectiveMenu {
  @AboutToShow
  public void aboutToShow(
      List<MMenuElement> items,
      EPerspectiveService perspectiveService,
      MApplication application,
      EModelService modelService) {
    IPerspectiveContainer container = perspectiveService.getActiveContainer();
    if (container == null) {
      return;
    }

    MPerspective active = container.getActivePerspective();

    var showPerspectiveCommand = getShowPerspectiveCommand(modelService, application);
    var containerParameter = getContainerParameter(container);

    for (MPerspective perspective : container.getPerspectives()) {
      var perspectiveParameter = getPerspectiveParameter(perspective);

      MHandledMenuItem dynamicItem = MMenuFactory.INSTANCE.createHandledMenuItem();
      dynamicItem.setType(ItemType.RADIO);
      dynamicItem.setSelected(perspective == active);
      dynamicItem.setLabel(perspective.getLocalizedLabel());
      dynamicItem.setIconURI(perspective.getIconURI());
      dynamicItem.setCommand(showPerspectiveCommand);
      dynamicItem.getParameters().add(containerParameter);
      dynamicItem.getParameters().add(perspectiveParameter);

      items.add(dynamicItem);
    }
  }

  private MCommand getShowPerspectiveCommand(EModelService modelService, MApplication application) {
    return modelService
        .findElements(application, SHOW_COMMAND_ID, MCommand.class, List.of())
        .get(0);
  }

  private MParameter getContainerParameter(IPerspectiveContainer container) {
    var containerParameter = MCommandsFactory.INSTANCE.createParameter();
    containerParameter.setName(PERSPECTIVE_STACK_ID_PARAMETER);
    containerParameter.setValue(container.getPerspectiveStack().getElementId());

    return containerParameter;
  }

  private MParameter getPerspectiveParameter(MPerspective perspective) {
    MParameter perspectiveParameter = MCommandsFactory.INSTANCE.createParameter();
    perspectiveParameter.setName(PERSPECTIVE_ID_PARAMETER);
    perspectiveParameter.setValue(perspective.getElementId());

    return perspectiveParameter;
  }
}
