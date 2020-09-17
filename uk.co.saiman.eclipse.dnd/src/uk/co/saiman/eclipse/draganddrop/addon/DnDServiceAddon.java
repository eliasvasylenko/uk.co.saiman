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
 * This file is part of uk.co.saiman.eclipse.dnd.
 *
 * uk.co.saiman.eclipse.dnd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.dnd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.draganddrop.addon;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainerElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.fx.ui.workbench.renderers.base.services.DnDService;
import org.eclipse.fx.ui.workbench.renderers.base.widget.WDragTargetWidget.BasicDropLocation;
import org.eclipse.fx.ui.workbench.renderers.base.widget.WDragTargetWidget.DropLocation;
import org.eclipse.fx.ui.workbench.renderers.base.widget.WWidget;

import javafx.scene.Node;

public class DnDServiceAddon implements DnDService {
  private static final Object SEGREGATED_DND = "segregatedDnD";

  @Inject
  EModelService modelService;

  @PostConstruct
  void initialize(IEclipseContext context) {
    context.set(DnDService.class, this);
  }

  boolean testSegragationBoundaries(MUIElement sourceContainer, MUIElement targetContainer) {
    MUIElement sourceAncestor = sourceContainer;
    while (sourceAncestor != null && !sourceAncestor.getTags().contains(SEGREGATED_DND)) {
      sourceAncestor = sourceAncestor.getParent();
    }

    MUIElement targetAncestor = targetContainer;
    while (targetAncestor != null && !targetAncestor.getTags().contains(SEGREGATED_DND)) {
      targetAncestor = targetAncestor.getParent();
    }

    return sourceAncestor == targetAncestor;
  }

  @Override
  public boolean splitAllowed(MUIElement element, MUIElement sourceElement, DropLocation dropType) {
    return testSegragationBoundaries(sourceElement.getParent(), element);
  }

  @Override
  public boolean detachAllowed(MUIElement element) {
    return true;
  }

  @Override
  public boolean reorderAllowed(
      MUIElement reference,
      MUIElement sourceElement,
      DropLocation dropLocation) {
    return testSegragationBoundaries(sourceElement.getParent(), reference.getParent());
  }

  @Override
  public boolean insertAllowed(MUIElement reference, MUIElement sourceElement) {
    return testSegragationBoundaries(sourceElement.getParent(), reference);
  }

  @Override
  public boolean handleDetach(double x, double y, MUIElement sourceElement) {
    Node sourceWidget = (Node) ((WWidget<?>) sourceElement.getWidget()).getWidget();
    this.modelService
        .detach(
            (MPartSashContainerElement) sourceElement,
            (int) x,
            (int) y,
            (int) sourceWidget.getBoundsInParent().getWidth(),
            (int) sourceWidget.getBoundsInParent().getHeight());

    MWindow window = getWindowFor(sourceElement);
    // TODO why can't we maximize this window?

    return true;
  }

  private MWindow getWindowFor(MUIElement element) {
    MUIElement parent = element;
    while (!(parent instanceof MWindow)) {
      parent = parent.getParent();
    }

    return (MWindow) parent;
  }

  @Override
  public boolean handleReorder(
      MUIElement reference,
      MUIElement sourceElement,
      DropLocation dropLocation) {
    return false;
  }

  @Override
  public boolean handleInsert(MUIElement reference, MUIElement sourceElement) {
    return false;
  }

  @Override
  public boolean handleSplit(
      MUIElement reference,
      MUIElement sourceElement,
      DropLocation dropLocation) {
    MElementContainer<MUIElement> parent = reference.getParent();
    if ((MUIElement) parent instanceof MPartStack) {
      split(parent, sourceElement, dropLocation);
    }
    return true;
  }

  private void split(MUIElement toSplit, MUIElement child, DropLocation dropType) {
    // remove the moved element from its parent
    child.setParent(null);

    // remember the index to insert
    MElementContainer<MUIElement> owner = toSplit.getParent();
    int index = owner.getChildren().indexOf(toSplit);

    // remove the split from the parent
    owner.getChildren().remove(toSplit);

    MPartSashContainer container = this.modelService.createModelElement(MPartSashContainer.class);
    container.setContainerData(toSplit.getContainerData());

    MPartStack childContainer = this.modelService.createModelElement(MPartStack.class);
    childContainer.getChildren().add((MStackElement) child);

    toSplit.setContainerData(null);
    childContainer.setContainerData(null);

    container.setToBeRendered(true);
    container.setVisible(true);
    container
        .setHorizontal(
            dropType == BasicDropLocation.SPLIT_LEFT || dropType == BasicDropLocation.SPLIT_RIGHT);
    if (dropType == BasicDropLocation.SPLIT_TOP || dropType == BasicDropLocation.SPLIT_LEFT) {
      container.getChildren().add(childContainer);
      container.getChildren().add((MPartSashContainerElement) toSplit);
    } else {
      container.getChildren().add((MPartSashContainerElement) toSplit);
      container.getChildren().add(childContainer);
    }
    owner.getChildren().add(index, container);
  }
}
