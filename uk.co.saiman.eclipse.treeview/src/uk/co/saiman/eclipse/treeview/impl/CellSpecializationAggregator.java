package uk.co.saiman.eclipse.treeview.impl;

import static org.eclipse.e4.core.internal.contexts.ContextObjectSupplier.getObjectSupplier;

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
import uk.co.saiman.eclipse.ui.ListItems;
import uk.co.saiman.eclipse.ui.model.MCell;

public class CellSpecializationAggregator {
  private final static IInjector INJECTOR = InjectorFactory.getDefault();

  private final Object data;
  private final HBox node;
  private final TreeEntryChildrenImpl children;

  private final PrimaryObjectSupplier contextSupplier;
  private final PrimaryObjectSupplier localSupplier;

  public CellSpecializationAggregator(IEclipseContext context, Object data, HBox node) {
    this.data = data;
    this.node = node;
    this.children = new TreeEntryChildrenImpl();

    contextSupplier = getObjectSupplier(context, INJECTOR);
    localSupplier = new TreeContributionObjectSupplier();
  }

  public void inject(MCell contribution) {
    INJECTOR
        .invoke(
            contribution.getContributionClass(),
            AboutToShow.class,
            null,
            contextSupplier,
            localSupplier);
  }

  public Stream<ListItemConfigurationImpl<?>> getChildren() {
    return children.configurations();
  }

  public final class TreeContributionObjectSupplier extends PrimaryObjectSupplier {
    public ListItems getChildren() {
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

        if (desired == ListItems.class) {
          actualValues[i] = children;

        } else if (desired == HBox.class) {
          actualValues[i] = node;

        }
      }
    }
  }

  public boolean isEditable() {
    // TODO Auto-generated method stub
    return false;
  }
}
