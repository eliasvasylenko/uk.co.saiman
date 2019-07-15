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
