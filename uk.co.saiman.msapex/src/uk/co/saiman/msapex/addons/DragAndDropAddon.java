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
package uk.co.saiman.msapex.addons;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.fx.ui.workbench.renderers.base.services.DnDService;
import org.eclipse.fx.ui.workbench.renderers.base.widget.WDragTargetWidget.DropLocation;

public class DragAndDropAddon implements DnDService {
  private static final Object SEGREGATED_DND = "segregatedDnD";

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

    System.out.println((sourceAncestor == targetAncestor) ? "YEAH!!!" : "nah.");
    return sourceAncestor == targetAncestor;
  }

  @Override
  public boolean splitAllowed(MUIElement element, MUIElement sourceElement, DropLocation dropType) {
    return testSegragationBoundaries(sourceElement.getParent(), element);
  }

  @Override
  public boolean detachAllowed(MUIElement element) {
    System.out.println("detach???");
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
    return false;
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
    return false;
  }
}
