package uk.co.saiman.eclipse.treeview;

import static uk.co.saiman.reflection.Types.getErasedType;
import static uk.co.saiman.reflection.token.TypeToken.forType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.eclipse.e4.core.di.suppliers.IObjectDescriptor;
import org.eclipse.e4.core.di.suppliers.IRequestor;
import org.eclipse.e4.core.di.suppliers.PrimaryObjectSupplier;

import javafx.scene.layout.HBox;
import uk.co.saiman.reflection.token.TypeToken;

final class TreeContributionObjectSupplier extends PrimaryObjectSupplier {
  private final TreeEntry<?> entry;
  private final HBox node;
  private final TreeChildrenImpl children;
  private final TreeEditorImpl<?> editor;
  private final TreeClipboardManager dragAndDrop;

  TreeContributionObjectSupplier(
      TreeEntry<?> entry,
      HBox node,
      TreeChildrenImpl children,
      TreeEditorImpl<?> editor,
      TreeClipboardManager dragAndDrop) {
    this.entry = entry;
    this.node = node;
    this.children = children;
    this.editor = editor;
    this.dragAndDrop = dragAndDrop;
  }

  @Override
  public void resumeRecording() {}

  @Override
  public void pauseRecording() {}

  @Override
  public void get(
      IObjectDescriptor[] descriptors,
      Object[] actualValues,
      IRequestor requestor,
      boolean initial,
      boolean track,
      boolean group) {
    for (int i = 0; i < descriptors.length; i++) {
      Type desired = descriptors[i].getDesiredType();

      if (desired == TreeChildren.class) {
        actualValues[i] = children;

      } else if (desired == HBox.class) {
        actualValues[i] = node;

      } else if (desired instanceof ParameterizedType && getErasedType(desired) == TreeEntry.class
          && getTypeArgument(desired).isAssignableFrom(entry.type())) {
        actualValues[i] = entry;

      } else if (desired instanceof ParameterizedType && getErasedType(desired) == TreeEditor.class
          && getTypeArgument(desired).isAssignableFrom(entry.type())) {
        actualValues[i] = editor;

      } else if (desired instanceof ParameterizedType
          && getErasedType(desired) == TreeClipboard.class) {
        actualValues[i] = dragAndDrop.getForType(getTypeArgument(desired));
      }
    }
  }

  private TypeToken<?> getTypeArgument(Type desired) {
    return forType(desired).getTypeArguments().findFirst().get().getTypeToken();
  }
}