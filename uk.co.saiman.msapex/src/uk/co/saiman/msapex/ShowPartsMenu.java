/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.eclipse.part.command.PartCommandConstants.CLOSE_COMMAND_ID;
import static uk.co.saiman.eclipse.part.command.PartCommandConstants.OPEN_COMMAND_ID;
import static uk.co.saiman.eclipse.part.command.PartCommandConstants.PART_ID_PARAMETER;
import static uk.co.saiman.eclipse.part.command.PartCommandConstants.PERSPECTIVE_ID_PARAMETER;

import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import uk.co.saiman.eclipse.perspective.EPerspectiveService;
import uk.co.saiman.eclipse.perspective.IPerspectiveContainer;

public class ShowPartsMenu {
  @AboutToShow
  public void aboutToShow(
      List<MMenuElement> items,
      EPerspectiveService perspectiveService,
      MApplication application,
      EModelService modelService,
      EPartService partService) {
    IPerspectiveContainer container = perspectiveService.getActiveContainer();
    if (container == null) {
      return;
    }

    MPerspective active = container.getActivePerspective();

    var openPartCommand = getOpenPartCommand(modelService, application);
    var closePartCommand = getClosePartCommand(modelService, application);
    var perspectiveParamter = getPerspectiveParameter(active);

    var parts = modelService
        .getTopLevelWindowFor(active)
        .getSharedElements()
        .stream()
        .filter(e -> e instanceof MPart)
        .map(e -> (MPart) e)
        .collect(toList());

    for (MPart part : parts) {
      var partParameter = getPartParameter(part);
      boolean visible = partService.isPartOrPlaceholderInPerspective(part.getElementId(), active);

      MHandledMenuItem dynamicItem = MMenuFactory.INSTANCE.createHandledMenuItem();
      dynamicItem.setType(ItemType.CHECK);
      dynamicItem.setSelected(visible);
      dynamicItem.setLabel(part.getLocalizedLabel());
      dynamicItem.setIconURI(part.getIconURI());
      dynamicItem.setCommand(visible ? closePartCommand : openPartCommand);
      dynamicItem.getParameters().add(perspectiveParamter);
      dynamicItem.getParameters().add(partParameter);

      items.add(dynamicItem);
    }
  }

  private MParameter getPerspectiveParameter(MPerspective perspective) {
    MParameter perspectiveParameter = MCommandsFactory.INSTANCE.createParameter();
    perspectiveParameter.setName(PERSPECTIVE_ID_PARAMETER);
    perspectiveParameter.setValue(perspective.getElementId());

    return perspectiveParameter;
  }

  private MParameter getPartParameter(MPart part) {
    MParameter partParameter = MCommandsFactory.INSTANCE.createParameter();
    partParameter.setName(PART_ID_PARAMETER);
    partParameter.setValue(part.getElementId());

    return partParameter;
  }

  private MCommand getOpenPartCommand(EModelService modelService, MApplication application) {
    return modelService
        .findElements(application, OPEN_COMMAND_ID, MCommand.class, List.of())
        .get(0);
  }

  private MCommand getClosePartCommand(EModelService modelService, MApplication application) {
    return modelService
        .findElements(application, CLOSE_COMMAND_ID, MCommand.class, List.of())
        .get(0);
  }
}
