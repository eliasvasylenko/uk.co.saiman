package uk.co.saiman.eclipse.treeview.impl;

import static org.eclipse.e4.core.internal.contexts.ContextObjectSupplier.getObjectSupplier;
import static uk.co.saiman.reflection.Types.getErasedType;
import static uk.co.saiman.reflection.token.TypeToken.forType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.stream.Stream;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.IInjector;
import org.eclipse.e4.core.di.InjectorFactory;
import org.eclipse.e4.core.di.suppliers.IObjectDescriptor;
import org.eclipse.e4.core.di.suppliers.IRequestor;
import org.eclipse.e4.core.di.suppliers.PrimaryObjectSupplier;
import org.eclipse.e4.ui.di.AboutToShow;

import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.treeview.TreeContribution;
import uk.co.saiman.eclipse.treeview.TreeEntry;
import uk.co.saiman.eclipse.treeview.TreeEntryChild;
import uk.co.saiman.eclipse.treeview.TreeEntryChildren;
import uk.co.saiman.eclipse.treeview.TreeEntryClipboard;
import uk.co.saiman.eclipse.treeview.TreeEntryEditor;
import uk.co.saiman.reflection.token.TypeToken;

public class TreeContributionAggregator {
  private final static IInjector INJECTOR = InjectorFactory.getDefault();

  private final TreeEntry<?> entry;
  private final HBox node;
  private final TreeEntryChildrenImpl children;
  private final TreeEntryEditorImpl editor;
  private final TreeClipboardManager dragAndDrop;

  private final PrimaryObjectSupplier contextSupplier;
  private final PrimaryObjectSupplier localSupplier;

  public TreeContributionAggregator(
      IEclipseContext context,
      TreeEntry<?> entry,
      HBox node,
      boolean editing) {
    this.entry = entry;
    this.node = node;
    this.children = new TreeEntryChildrenImpl();
    this.editor = new TreeEntryEditorImpl(editing);
    this.dragAndDrop = new TreeClipboardManager();

    contextSupplier = getObjectSupplier(context, INJECTOR);
    localSupplier = new TreeContributionObjectSupplier();
  }

  public void inject(TreeContribution contribution) {
    INJECTOR.invoke(contribution, AboutToShow.class, null, contextSupplier, localSupplier);
  }

  public boolean isEditable() {
    return editor.isEditable();
  }

  public Stream<TreeEntryChild<?>> getChildren() {
    return children.stream();
  }

  public TreeClipboardManager getDragAndDrop() {
    return dragAndDrop;
  }

  public final class TreeContributionObjectSupplier extends PrimaryObjectSupplier {
    public TreeEntryChildren getChildren() {
      return children;
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

        if (desired == TreeEntryChildren.class) {
          actualValues[i] = children;

        } else if (desired == HBox.class) {
          actualValues[i] = node;

        } else if (desired instanceof ParameterizedType
            && getErasedType(desired) == TreeEntry.class
            && getTypeArgument(desired).isAssignableFrom(entry.type())) {
          actualValues[i] = entry;

        } else if (desired == TreeEntryEditor.class) {
          actualValues[i] = editor;

        } else if (desired instanceof ParameterizedType
            && getErasedType(desired) == TreeEntryClipboard.class) {
          actualValues[i] = dragAndDrop.getForType(getTypeArgument(desired));
        }
      }
    }

    private TypeToken<?> getTypeArgument(Type desired) {
      return forType(desired).getTypeArguments().findFirst().get().getTypeToken();
    }
  }
}
