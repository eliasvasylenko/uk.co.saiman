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
 * This file is part of uk.co.saiman.eclipse.perspectives.
 *
 * uk.co.saiman.eclipse.perspectives is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.perspectives is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.perspective.addon;

import static uk.co.saiman.eclipse.perspective.addon.PerspectiveServiceAddon.clonePerspectiveSnippet;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import uk.co.saiman.eclipse.perspective.EPerspectiveService;
import uk.co.saiman.eclipse.perspective.IPerspectiveContainer;

public class PerspectiveServiceImpl implements EPerspectiveService {
  @Inject
  private EModelService modelService;
  @Inject
  private EPartService partService;

  @Inject
  private MApplication application;
  @Inject
  @Optional
  private MWindow window;
  @Inject
  @Optional
  private MPerspectiveStack perspectiveStack;

  @Override
  public IPerspectiveContainer getActiveContainer() {
    var perspectiveStack = getPerspectiveStack();

    if (perspectiveStack == null) {
      return null;
    }

    return new PerspectiveContainerImpl(perspectiveStack);
  }

  private MPerspectiveStack getPerspectiveStack() {
    var perspectiveStack = this.perspectiveStack;

    if (perspectiveStack == null) {
      var activePart = partService.getActivePart();
      var activePerspective = modelService.getPerspectiveFor(activePart);

      perspectiveStack = PerspectiveServiceAddon.getPerspectiveStack(activePerspective);
    }

    return perspectiveStack;
  }

  @Override
  public IPerspectiveContainer findContainer(String perspectiveStackId) {
    var perspectiveStacks = modelService
        .findElements(application, perspectiveStackId, MPerspectiveStack.class, List.of());

    if (perspectiveStacks.isEmpty()) {
      return null;
    }

    var perspectiveStack = perspectiveStacks.get(0);

    if (perspectiveStack == null) {
      return null;
    }

    return new PerspectiveContainerImpl(perspectiveStack);
  }

  @Override
  public MPerspective findPerspective(String perspectiveId) {
    var perspectives = modelService
        .findElements(application, perspectiveId, MPerspective.class, List.of());

    if (perspectives.isEmpty()) {
      return null;
    }

    var perspective = perspectives.get(0);

    if (perspective == null) {
      return null;
    }

    return perspective;
  }

  @Override
  public void resetPerspective(MPerspective perspective) {
    if (perspective == null) {
      return;
    }

    String snippetId = perspective.getPersistedState().get(PERSPECTIVE_SOURCE_SNIPPET);

    if (snippetId != null) {
      var perspectiveStack = PerspectiveServiceAddon.getPerspectiveStack(perspective);
      var snippetContainer = PerspectiveServiceAddon.getSnippetContainer(perspectiveStack);
      if (snippetContainer != null) {
        clonePerspectiveSnippet(modelService, snippetContainer, snippetId, perspectiveStack, true);
      }
    }
  }

  @Override
  public void activatePerspective(MPerspective perspective) {
    partService.switchPerspective(perspective);
  }

  class PerspectiveContainerImpl implements IPerspectiveContainer {
    private final MPerspectiveStack perspectiveStack;

    public PerspectiveContainerImpl(MPerspectiveStack perspectiveStack) {
      this.perspectiveStack = perspectiveStack;
    }

    @Override
    public MPerspectiveStack getPerspectiveStack() {
      return perspectiveStack;
    }

    @Override
    public List<MPerspective> getPerspectives() {
      return List.copyOf(perspectiveStack.getChildren());
    }

    @Override
    public MPerspective getActivePerspective() {
      return perspectiveStack.getSelectedElement();
    }

    @Override
    public MPerspective findPerspective(String perspectiveId) {
      return getPerspectives()
          .stream()
          .filter(p -> p.getElementId().equals(perspectiveId))
          .findFirst()
          .orElse(null);
    }

    @Override
    public void resetPerspective(String perspectiveId) {
      PerspectiveServiceImpl.this.resetPerspective(findPerspective(perspectiveId));
    }

    @Override
    public void activatePerspective(String perspectiveId) {
      PerspectiveServiceImpl.this.activatePerspective(findPerspective(perspectiveId));
    }
  }
}
