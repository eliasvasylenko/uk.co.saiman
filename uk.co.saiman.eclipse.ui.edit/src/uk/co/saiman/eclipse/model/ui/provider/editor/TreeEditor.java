package uk.co.saiman.eclipse.model.ui.provider.editor;

import static uk.co.saiman.eclipse.model.ui.provider.UISaimanEditPlugin.INSTANCE;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.e4.tools.emf.ui.common.Util;
import org.eclipse.e4.tools.emf.ui.internal.common.VirtualEntry;
import org.eclipse.e4.tools.emf.ui.internal.common.component.ControlFactory;
import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl;
import org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.databinding.swt.IWidgetValueProperty;
import org.eclipse.swt.widgets.Composite;

public class TreeEditor extends AbstractEditor {
  private static final String VIRTUAL_TREE_CHILDREN_MENU = "uk.co.saiman.eclipse.model.ui.tree.children.virtual";

  private final IListProperty<?, ?> CHILDREN = EMFProperties
      .list(UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN);
  // TODO .list(eINSTANCE.getTree().getEStructuralFeature(TREE__CHILDREN));

  protected String getString(String key) {
    return INSTANCE.getPluginResourceLocator().getString(key);
  }

  @Override
  public String getLabel(Object element) {
    return getString("_UI_Tree_type");
  }

  @Override
  public String getDetailLabel(Object element) {
    final MContribution contrib = (MContribution) element;
    if (contrib.getContributionURI() != null && contrib.getContributionURI().trim().length() > 0) {
      return contrib
          .getContributionURI()
          .substring(contrib.getContributionURI().lastIndexOf('/') + 1);
    }
    return null;
  }

  @Override
  public String getDescription(Object element) {
    return getString("_UI_Tree_editor_description");
  }

  @Override
  protected void createDefaultControls(
      Composite parent,
      EMFDataBindingContext context,
      IObservableValue<?> master,
      IWidgetValueProperty textProp) {

    ControlFactory
        .createTextField(
            parent,
            Messages.ModelTooling_Common_Id,
            master,
            context,
            textProp,
            EMFEditProperties
                .value(
                    getEditingDomain(),
                    ApplicationPackageImpl.Literals.APPLICATION_ELEMENT__ELEMENT_ID));

    ControlFactory
        .createClassURIField(
            parent,
            Messages,
            this,
            Messages.AddonsEditor_ClassURI,
            ApplicationPackageImpl.Literals.CONTRIBUTION__CONTRIBUTION_URI,
            getEditor().getContributionCreator(ApplicationPackageImpl.Literals.ADDON),
            getProject(),
            context,
            eclipseContext);

    ControlFactory
        .createMapProperties(
            parent,
            Messages,
            this,
            Messages.ModelTooling_Contribution_PersistedState,
            ApplicationPackageImpl.Literals.APPLICATION_ELEMENT__PERSISTED_STATE,
            VERTICAL_LIST_WIDGET_INDENT);
  }

  @Override
  protected void createSupplementaryControls(Composite parent) {
    ControlFactory
        .createStringListWidget(
            parent,
            Messages,
            this,
            Messages.ModelTooling_Context_Variables,
            Messages.ModelTooling_Context_Variables_Tooltip,
            UiPackageImpl.Literals.CONTEXT__VARIABLES,
            VERTICAL_LIST_WIDGET_INDENT);

    ControlFactory
        .createStringListWidget(
            parent,
            Messages,
            this,
            Messages.CategoryEditor_Tags,
            ApplicationPackageImpl.Literals.APPLICATION_ELEMENT__TAGS,
            VERTICAL_LIST_WIDGET_INDENT);
  }

  @Override
  public IObservableList<?> getChildList(final Object element) {
    final WritableList<VirtualEntry<?>> list = new WritableList<>();

    if (getEditor().isModelFragment() && Util.isImport((EObject) element)) {
      return list;
    }

    list
        .add(
            new VirtualEntry<Object>(
                VIRTUAL_TREE_CHILDREN_MENU,
                CHILDREN,
                element,
                getString("_UI_children")) {
              @Override
              protected boolean accepted(Object o) {
                return true;
              }
            });

    return list;
  }
}
