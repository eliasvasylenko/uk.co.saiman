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
      perspectiveStack = PerspectiveServiceAddon
          .getPerspectiveStack(modelService.getActivePerspective(getWindow()));
    }

    return perspectiveStack;
  }

  @Override
  public MPerspective findPerspective(String perspectiveId) {
    // TODO Auto-generated method stub
    return null;
  }

  private MWindow getWindow() {
    var window = this.window;

    if (window == null) {
      window = application.getSelectedElement();
    }

    return window;
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
  public void resetPerspective(MPerspective perspective) {
    if (perspective == null) {
      return;
    }

    if (perspective.getPersistedState().containsKey(SNIPPET_PERSPETIVE_STACK)) {
      var perspectiveStack = PerspectiveServiceAddon.getPerspectiveStack(perspective);
      var snippetContainer = PerspectiveServiceAddon.getSnippetContainer(perspectiveStack);
      if (snippetContainer != null) {
        clonePerspectiveSnippet(
            modelService,
            snippetContainer,
            perspective.getElementId(),
            perspectiveStack,
            true);
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
