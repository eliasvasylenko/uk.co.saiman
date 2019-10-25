package uk.co.saiman.eclipse.ui.fx.impl;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import uk.co.saiman.eclipse.utilities.ContextBuffer;

public class ChildrenContainer {
  private class ChildGroup {
    private final MUIElement markerElement;
    private final Map<MUIElement, ContextBuffer> elements = new LinkedHashMap<>();

    public ChildGroup(String snippetId) {
      var markerElement = models.cloneSnippet(application, snippetId, null);
      markerElement.setElementId(snippetId + "-marker");
      markerElement.setVisible(false);

      if (markerElement instanceof MContribution) {
        ((MContribution) markerElement).setContributionURI(null);
      }
      if (markerElement instanceof MElementContainer<?>) {
        ((MElementContainer<?>) markerElement).getChildren().clear();
      }
      container.getChildren().add(markerElement);

      this.markerElement = markerElement;
    }

    public int markerIndex() {
      return container.getChildren().indexOf(markerElement);
    }

    public ContextBuffer getContextBuffer(MUIElement element) {
      return elements.get(element);
    }

    public void addElement(MUIElement child, ContextBuffer context) {
      elements.put(child, context);
    }

    public void removeElement(MUIElement child) {
      elements.remove(child);
    }
  }

  private final Map<String, ChildGroup> childGroups = new LinkedHashMap<>();
  private final MApplication application;
  private final EModelService models;
  private final MElementContainer<MUIElement> container;

  public ChildrenContainer(
      MApplication application,
      EModelService models,
      MElementContainer<MUIElement> container) {
    this.application = application;
    this.models = models;
    this.container = container;
  }

  public synchronized void updateChildren(
      String snippetId,
      Collection<? extends ContextBuffer> contexts) {

    var childGroup = childGroups.computeIfAbsent(snippetId, ChildGroup::new);
    var startIndex = childGroup.markerIndex() + 1;
    var insertIndex = startIndex;

    Set<MUIElement> newChildren = new LinkedHashSet<>();

    for (var context : contexts) {
      MUIElement child = null;

      // search for appropriate existing matches to keep in place
      for (int i = insertIndex; i < container.getChildren().size(); i++) {
        var existingElement = container.getChildren().get(i);
        var existingContextBuffer = childGroup.getContextBuffer(existingElement);

        if (context.equals(existingContextBuffer)) {
          child = existingElement;
          insertIndex = i;
          break;
        }
      }

      if (child == null) {
        // search for appropriate skipped-over elements to move into place
        for (int i = startIndex; i < insertIndex; i++) {
          var existingElement = container.getChildren().get(i);
          var existingContextBuffer = childGroup.getContextBuffer(existingElement);

          if (context.equals(existingContextBuffer)) {
            child = existingElement;
            container.getChildren().remove(child);
            container.getChildren().add(--insertIndex, child);
            break;
          }
        }

        if (child == null) {
          // add new element
          child = models.cloneSnippet(application, snippetId, null);
          child.setElementId(snippetId + "-clone");

          childGroup.addElement(child, context);
          container.getChildren().add(insertIndex, child);
        }
      }
      newChildren.add(child);
      insertIndex++;
    }

    // remove orphans
    for (int i = startIndex; i < container.getChildren().size(); i++) {
      var existingElement = container.getChildren().get(i);
      var existingContextBuffer = childGroup.getContextBuffer(existingElement);

      if (existingContextBuffer != null && !newChildren.contains(existingElement)) {
        childGroup.removeElement(existingElement);
        existingElement.setToBeRendered(false);
      }
    }
  }

  public Optional<ContextBuffer> getContextBuffer(MUIElement element) {
    return childGroups
        .values()
        .stream()
        .flatMap(elements -> Optional.ofNullable(elements.getContextBuffer(element)).stream())
        .findFirst();
  }
}
